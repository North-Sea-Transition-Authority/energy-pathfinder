package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.event.contributor.ContributorsAddedEventListener;
import uk.co.ogauthority.pathfinder.event.contributor.ContributorsAddedEventPublisher;
import uk.co.ogauthority.pathfinder.event.contributor.ContributorsDeletedEventListener;
import uk.co.ogauthority.pathfinder.event.contributor.ContributorsDeletedEventPublisher;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.projectcontributor.ProjectContributorRepository;
import uk.co.ogauthority.pathfinder.service.email.ProjectContributorMailService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
    classes = {
        ProjectContributorsCommonService.class,
        ContributorsDeletedEventPublisher.class,
        ContributorsDeletedEventListener.class,
        ContributorsAddedEventPublisher.class,
        ContributorsAddedEventListener.class,
        TransactionWrapper.class
    }))
@AutoConfigureTestDatabase
@ActiveProfiles("integration-test")
@RunWith(SpringRunner.class)
public class ProjectContributorsCommonServiceEventTest {

    private final ProjectDetail detail = ProjectUtil.getProjectDetails();
    private final PortalOrganisationGroup portalOrganisationGroup1 = TeamTestingUtil.generateOrganisationGroup(1, "org1", "org1");
    private final PortalOrganisationGroup myPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(3, "org3", "org3");
    private final ProjectOperator projectOperator = ProjectOperatorTestUtil.getOperator(detail, myPortalOrganisationGroup);

    @Autowired
    private ProjectContributorsCommonService projectContributorsCommonService;

    @Autowired TransactionWrapper transactionWrapper;

    @MockBean
    private ProjectContributorRepository projectContributorRepository;

    @MockBean
    private ProjectOperatorService projectOperatorService;

    @MockBean
    private PortalOrganisationAccessor portalOrganisationAccessor;

    @MockBean
    private ProjectContributorMailService projectContributorMailService;

    @MockBean
    private ProjectDetailsRepository projectDetailsRepository;

    @Before
    public void setup() {
        when(projectOperatorService.getProjectOperatorByProjectDetailOrError(detail)).thenReturn(projectOperator);
        when(portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(
            List.of(portalOrganisationGroup1.getOrgGrpId())))
            .thenReturn(List.of(portalOrganisationGroup1));

    }

    @Test
    public void saveProjectContributors_whenSuccessfulCommit_thenVerifyMailsSent() {
        var form = new ProjectContributorsForm();
        form.setContributors(List.of(portalOrganisationGroup1.getOrgGrpId()));
        var portalOrganisationGroup2 = TeamTestingUtil.generateOrganisationGroup(2, "org", "org");
        var projectContributorOld = ProjectContributorTestUtil.contributorWithGroupOrg(detail, portalOrganisationGroup2);

        when(projectDetailsRepository.findById(detail.getId())).thenReturn(Optional.of(detail));
        when(projectContributorRepository.findAllByProjectDetail(detail)).thenReturn(List.of(projectContributorOld));

        transactionWrapper.runInNewTransaction(() -> projectContributorsCommonService.saveProjectContributors(form, detail));

        verify(projectContributorMailService, times(1))
            .sendContributorsRemovedEmail(List.of(projectContributorOld), detail);

        var projectContributorListCaptor = ArgumentCaptor.forClass(List.class);
        verify(projectContributorMailService, times(1))
            .sendContributorsAddedEmail(projectContributorListCaptor.capture(), eq(detail));
        var expectedSavedContributor = ProjectContributorTestUtil.contributorWithGroupOrg(detail, portalOrganisationGroup1);
        var savedContributors = (List<ProjectContributor>) projectContributorListCaptor.getValue();
        assertThat(savedContributors).hasSize(1);
        assertThat(savedContributors.get(0)).isEqualTo(expectedSavedContributor);
    }

    @Test
    public void saveProjectContributors_whenRollback_thenVerifyNoMailSent() {
        var form = new ProjectContributorsForm();
        form.setContributors(List.of(portalOrganisationGroup1.getOrgGrpId()));

        try {
            transactionWrapper.runInNewTransaction(() -> {
                projectContributorsCommonService.saveProjectContributors(form, detail);
                throw new RuntimeException("evil exception");
            });
        } catch (Exception e) {
            //do nothing
        }

        verify(projectContributorMailService, never()).sendContributorsRemovedEmail(any(), any());
        verify(projectContributorMailService, never()).sendContributorsAddedEmail(any(), any());
    }

    @Test
    public void deleteProjectContributors_whenSuccessfulCommit_thenVerifyMailSent() {
        var portalOrganisationGroup2 = TeamTestingUtil.generateOrganisationGroup(2, "org", "org");
        var projectContributor = ProjectContributorTestUtil.contributorWithGroupOrg(detail, portalOrganisationGroup2);

        when(projectDetailsRepository.findById(detail.getId())).thenReturn(Optional.of(detail));
        when(projectContributorRepository.findAllByProjectDetail(detail)).thenReturn(List.of(projectContributor));

        transactionWrapper.runInNewTransaction(() -> projectContributorsCommonService.deleteProjectContributors(detail));

        verify(projectContributorMailService, times(1))
            .sendContributorsRemovedEmail(List.of(projectContributor), detail);
    }

    @Test
    public void deleteProjectContributors_whenRollback_thenVerifyNoMailSent() {
        try {
            transactionWrapper.runInNewTransaction(() -> {
                projectContributorsCommonService.deleteProjectContributors(detail);
                throw new RuntimeException("evil exception");
            });
        } catch (Exception e) {
            //do nothing
        }

        verify(projectContributorMailService, never()).sendContributorsRemovedEmail(any(), any());
    }
}