package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.event.contributor.ContributorsAddedEventPublisher;
import uk.co.ogauthority.pathfinder.event.contributor.ContributorsDeletedEventPublisher;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;
import uk.co.ogauthority.pathfinder.model.view.organisationgroup.OrganisationGroupView;
import uk.co.ogauthority.pathfinder.repository.project.projectcontributor.ProjectContributorRepository;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContributorsCommonServiceTest {

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final String regulatorSharedEmail = "email@test.com";

  @Mock
  private ProjectContributorRepository projectContributorRepository;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private ContributorsDeletedEventPublisher contributorsDeletedEventPublisher;

  @Mock
  private ContributorsAddedEventPublisher contributorsAddedEventPublisher;

  private ProjectContributorsCommonService projectContributorsCommonService;

  @Before
  public void setup() {
    projectContributorsCommonService = new ProjectContributorsCommonService(
        projectContributorRepository,
        projectOperatorService,
        portalOrganisationAccessor,
        regulatorSharedEmail,
        contributorsDeletedEventPublisher,
        contributorsAddedEventPublisher);
  }


  @Test
  public void saveProjectContributors_verifyMethodCalls() {
    var form = new ProjectContributorsForm();
    form.setContributors(List.of(1, 2, 3));
    var portalOrg1 = TeamTestingUtil.generateOrganisationGroup(1, "org1", "org1");
    var portalOrg2 = TeamTestingUtil.generateOrganisationGroup(2, "org2", "org2");
    var myPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(3, "org3", "org3");
    var projectOperator = ProjectOperatorTestUtil.getOperator(detail, myPortalOrganisationGroup);
    ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(detail)).thenReturn(projectOperator);
    when(portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(List.of(1, 2))).thenReturn(
        List.of(portalOrg1, portalOrg2)
    );

    projectContributorsCommonService.saveProjectContributors(form, detail);

    verify(projectContributorRepository, times(1)).deleteAllByProjectDetail(detail);
    verify(projectContributorRepository, times(1)).saveAll(argumentCaptor.capture());
    List<ProjectContributor> savedProjectContributors = (List<ProjectContributor>) argumentCaptor.getValue();
    assertThat(savedProjectContributors).hasSize(2);
    assertThat(savedProjectContributors.get(0))
        .extracting(ProjectContributor::getContributionOrganisationGroup, ProjectContributor::getProjectDetail)
        .containsExactly(portalOrg1, detail);
    assertThat(savedProjectContributors.get(1))
        .extracting(ProjectContributor::getContributionOrganisationGroup, ProjectContributor::getProjectDetail)
        .containsExactly(portalOrg2, detail);

    verify(contributorsDeletedEventPublisher, times(1))
        .publishContributorsDeletedEvent(argumentCaptor.capture(), eq(detail));
    var deletedContributors = (List<ProjectContributor>) argumentCaptor.getValue();
    assertThat(deletedContributors).isEmpty();

    verify(contributorsAddedEventPublisher, times(1))
        .publishContributorsAddedEvent(savedProjectContributors, detail);
  }

  @Test
  public void saveProjectContributors_nullList_verifyNoMethodCalls() {
    var form = new ProjectContributorsForm();

    projectContributorsCommonService.saveProjectContributors(form, detail);

    verify(projectContributorRepository, never()).deleteAllByProjectDetail(detail);
    verify(projectContributorRepository, never()).saveAll(any());
    verify(contributorsDeletedEventPublisher, never()).publishContributorsDeletedEvent(any(), any());
    verify(contributorsAddedEventPublisher, never()).publishContributorsAddedEvent(any(), any());
  }

  @Test
  public void saveProjectContributors_removedAContributor_thenRemovedContributorSentToEventPublisher() {
    var form = new ProjectContributorsForm();
    form.setContributors(List.of(1, 2));
    var portalOrg1 = TeamTestingUtil.generateOrganisationGroup(1, "org1", "org1");
    var portalOrg2 = TeamTestingUtil.generateOrganisationGroup(2, "org2", "org2");
    var myPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(3, "org3", "org3");
    var projectOperator = ProjectOperatorTestUtil.getOperator(detail, myPortalOrganisationGroup);
    var removedProjectContributor = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 4);
    ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(detail)).thenReturn(projectOperator);
    when(portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(List.of(1, 2))).thenReturn(
        List.of(portalOrg1, portalOrg2)
    );
    when(projectContributorRepository.findAllByProjectDetail(detail)).thenReturn(List.of(removedProjectContributor));

    projectContributorsCommonService.saveProjectContributors(form, detail);

    verify(contributorsDeletedEventPublisher, times(1))
        .publishContributorsDeletedEvent(argumentCaptor.capture(), eq(detail));
    var deletedContributors = (List<ProjectContributor>) argumentCaptor.getValue();
    assertThat(deletedContributors).containsExactlyInAnyOrder(removedProjectContributor);
  }

  @Test
  public void saveProjectContributors_addedAContributor_thenAddedContributorSentToEventPublisher() {
    var form = new ProjectContributorsForm();
    form.setContributors(List.of(1, 2));
    var portalOrg1 = TeamTestingUtil.generateOrganisationGroup(1, "org1", "org1");
    var portalOrg2 = TeamTestingUtil.generateOrganisationGroup(2, "org2", "org2");
    var oldProjectContributor = ProjectContributorTestUtil.contributorWithGroupOrg(detail, portalOrg1);
    var expectedNewProjectContributor = ProjectContributorTestUtil.contributorWithGroupOrg(detail, portalOrg2);
    var myPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(3, "org3", "org3");
    var projectOperator = ProjectOperatorTestUtil.getOperator(detail, myPortalOrganisationGroup);
    ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(detail)).thenReturn(projectOperator);
    when(portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(List.of(1, 2))).thenReturn(
        List.of(portalOrg1, portalOrg2)
    );
    when(projectContributorRepository.findAllByProjectDetail(detail)).thenReturn(List.of(oldProjectContributor));

    projectContributorsCommonService.saveProjectContributors(form, detail);

    verify(contributorsAddedEventPublisher, times(1))
        .publishContributorsAddedEvent(argumentCaptor.capture(), eq(detail));
    var newProjectContributor = (List<ProjectContributor>) argumentCaptor.getValue();
    assertThat(newProjectContributor).containsExactlyInAnyOrder(expectedNewProjectContributor);
  }

  @Test
  public void setContributorsInForm_previousContributors_assertFormFields() {
    var projectContributor1 = new ProjectContributor(
        detail,
        TeamTestingUtil.generateOrganisationGroup(1, "org", "org")
    );
    var projectContributor2 = new ProjectContributor(
        detail,
        TeamTestingUtil.generateOrganisationGroup(2, "org", "org")
    );
    var form = new ProjectContributorsForm();
    when(projectContributorRepository.findAllByProjectDetail(detail)).thenReturn(List.of(
        projectContributor1,
        projectContributor2
    ));

    projectContributorsCommonService.setContributorsInForm(form, detail);

    assertThat(form.getContributors())
        .containsExactly(
            projectContributor1.getContributionOrganisationGroup().getOrgGrpId(),
            projectContributor2.getContributionOrganisationGroup().getOrgGrpId());
  }

  @Test
  public void setContributorsInForm_noPreviousContributors_assertFormFields() {
    var form = new ProjectContributorsForm();
    when(projectContributorRepository.findAllByProjectDetail(detail)).thenReturn(List.of());

    projectContributorsCommonService.setContributorsInForm(form, detail);

    assertThat(form.getContributors()).isEmpty();
  }

  @Test
  public void setModelAndViewCommonObjects_assertModelObjects() {
    var form = new ProjectContributorsForm();
    var modelAndView = new ModelAndView("");
    var pageName = "My page";

    projectContributorsCommonService.setModelAndViewCommonObjects(
        modelAndView,
        detail,
        form,
        pageName,
        List.of()
    );

    assertThat(modelAndView.getModel()).containsExactly(
        entry("form", form),
        entry("pageName", pageName),
        entry("alreadyAddedContributors", List.of()),
        entry("contributorsRestUrl",
            SearchSelectorService.route(on(OrganisationGroupRestController.class)
                .searchPathfinderOrganisations(null))),
        entry("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(detail.getProject().getId())),
        entry("projectSetupUrl",
            ControllerUtils.getProjectSetupUrl(detail.getProject().getId())),
        entry("regulatorEmailAddress", regulatorSharedEmail),
        entry("errorList", List.of()),
        entry("projectTypeDisplayName", detail.getProjectType().getDisplayName()),
        entry("projectTypeDisplayNameLowercase", detail.getProjectType().getLowercaseDisplayName())
    );
  }

  @Test
  public void setModelAndViewCommonObjects_assertContributorsNamesSorted() {
    var modelAndView = new ModelAndView("");
    var pageName = "My page";
    var portalOrgA = TeamTestingUtil.generateOrganisationGroup(2, "alpha", "org1");
    var portalOrgB = TeamTestingUtil.generateOrganisationGroup(3, "beta", "org2");
    var portalOrgC = TeamTestingUtil.generateOrganisationGroup(1, "charlie", "org1");
    var form = new ProjectContributorsForm();
    form.setContributors(List.of(portalOrgB.getOrgGrpId(), portalOrgC.getOrgGrpId(), portalOrgA.getOrgGrpId()));

    when(portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(form.getContributors()))
        .thenReturn(List.of(portalOrgB, portalOrgC, portalOrgA));

    projectContributorsCommonService.setModelAndViewCommonObjects(
        modelAndView,
        detail,
        form,
        pageName,
        List.of()
    );

    assertThat(modelAndView.getModel().get("alreadyAddedContributors"))
        .asList()
        .isSortedAccordingTo(Comparator.comparing(o -> ((OrganisationGroupView)o).getName()));
  }

  @Test
  public void deleteProjectContributors_verifyMethodCall() {
    var projectContributor1 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 1);
    var projectContributor2 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 2);
    var listOfContributors = List.of(projectContributor1, projectContributor2);

    when(projectContributorRepository.findAllByProjectDetail(detail))
        .thenReturn(listOfContributors);

    projectContributorsCommonService.deleteProjectContributors(detail);

    verify(projectContributorRepository, times(1))
        .deleteAll(listOfContributors);
    verify(contributorsDeletedEventPublisher, times(1))
        .publishContributorsDeletedEvent(listOfContributors, detail);
  }

  @Test
  public void getProjectContributorsForDetail_assertContributors() {
    var projectContributor1 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 1);
    var projectContributor2 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 2);
    when(projectContributorRepository.findAllByProjectDetail(detail))
        .thenReturn(List.of(projectContributor1, projectContributor2));

    var projectContributors = projectContributorsCommonService.getProjectContributorsForDetail(detail);

    assertThat(projectContributors).containsExactlyInAnyOrder(
        projectContributor1,
        projectContributor2
    );
  }

  @Test
  public void hasProjectContributors_whenThereAreContributors_assertTrue() {
    when(projectContributorRepository.findAllByProjectDetail(detail))
        .thenReturn(List.of(ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 1)));

    assertThat(projectContributorsCommonService.hasProjectContributors(detail)).isTrue();
  }

  @Test
  public void hasProjectContributors_whenThereAreNoContributors_assertFalse() {
    when(projectContributorRepository.findAllByProjectDetail(detail)).thenReturn(List.of());

    assertThat(projectContributorsCommonService.hasProjectContributors(detail)).isFalse();
  }
}