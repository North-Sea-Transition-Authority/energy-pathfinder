package uk.co.ogauthority.pathfinder.service.project.workoplanprojectcontribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanprojectcontribution.ForwardWorkPlanContributorDetails;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.projectcontributor.ProjectContributorsView;
import uk.co.ogauthority.pathfinder.model.view.workplanprojectcontributor.ForwardWorkPlanProjectContributorsView;
import uk.co.ogauthority.pathfinder.repository.project.projectcontributor.ProjectContributorRepository;
import uk.co.ogauthority.pathfinder.repository.project.workplanprojectcontributor.ForwardWorkPlanContributorDetailsRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanProjectContributorSummaryServiceTest {

  private final ProjectDetail detail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  @Mock
  private ForwardWorkPlanContributorDetailsRepository forwardWorkPlanContributorDetailsRepository;

  @Mock
  private ProjectContributorRepository projectContributorRepository;

  private ForwardWorkPlanProjectContributorSummaryService forwardWorkPlanProjectContributorSummaryService;

  @Before
  public void setup() {
    forwardWorkPlanProjectContributorSummaryService = new ForwardWorkPlanProjectContributorSummaryService(
        forwardWorkPlanContributorDetailsRepository,
        projectContributorRepository
    );
  }

  @Test
  public void getProjectContributorsViewByDetail_projectContributorsExist_assertGroupNames() {
    var projectContributor1 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 1);
    var projectContributor2 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 2);
    var forwardWorkPlanProjectContributor = new ForwardWorkPlanContributorDetails(detail, true);

    when(projectContributorRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        detail.getProject(), detail.getVersion()))
        .thenReturn(List.of(projectContributor1, projectContributor2));
    when(forwardWorkPlanContributorDetailsRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        detail.getProject(),
        detail.getVersion()
    )).thenReturn(Optional.of(forwardWorkPlanProjectContributor));

    var forwardWorkPlanProjectContributorsView = forwardWorkPlanProjectContributorSummaryService.getProjectContributorsView(
        detail
    );

    assertThat(forwardWorkPlanProjectContributorsView)
        .extracting(ForwardWorkPlanProjectContributorsView::getOrganisationGroupNames)
        .asList()
        .containsExactly(
            projectContributor1.getContributionOrganisationGroup().getName(),
            projectContributor2.getContributionOrganisationGroup().getName()
        );

    assertThat(forwardWorkPlanProjectContributorsView.getHasProjectContributors()).isTrue();
  }

  @Test
  public void getProjectContributorsViewByDetail_projectContributorsDoesNotExist_assertNoGroupNames() {
    when(forwardWorkPlanContributorDetailsRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        detail.getProject(),
        detail.getVersion()
    )).thenReturn(Optional.empty());

    var forwardWorkPlanProjectContributorsView = forwardWorkPlanProjectContributorSummaryService.getProjectContributorsView(
        detail
    );

    assertThat(forwardWorkPlanProjectContributorsView)
        .extracting(ProjectContributorsView::getOrganisationGroupNames)
        .asList()
        .isEmpty();

    assertThat(forwardWorkPlanProjectContributorsView.getHasProjectContributors()).isNull();
  }

  @Test
  public void getProjectContributorsViewByProjectAndVersion_projectContributorsExist_assertGroupNames() {
    var projectContributor1 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 1);
    var projectContributor2 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 2);
    var forwardWorkPlanProjectContributor = new ForwardWorkPlanContributorDetails(detail, true);

    when(projectContributorRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        detail.getProject(), detail.getVersion()))
        .thenReturn(List.of(projectContributor1, projectContributor2));
    when(forwardWorkPlanContributorDetailsRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        detail.getProject(),
        detail.getVersion()
    )).thenReturn(Optional.of(forwardWorkPlanProjectContributor));

    var forwardWorkPlanProjectContributorsView = forwardWorkPlanProjectContributorSummaryService.getProjectContributorsView(
        detail.getProject(),
        detail.getVersion()
    );

    assertThat(forwardWorkPlanProjectContributorsView)
        .extracting(ForwardWorkPlanProjectContributorsView::getOrganisationGroupNames)
        .asList()
        .containsExactly(
            projectContributor1.getContributionOrganisationGroup().getName(),
            projectContributor2.getContributionOrganisationGroup().getName()
        );

    assertThat(forwardWorkPlanProjectContributorsView.getHasProjectContributors()).isTrue();
  }

  @Test
  public void getProjectContributorsViewByProjectAndVersion_projectContributorsDoesNotExist_assertNoGroupNames() {
    when(forwardWorkPlanContributorDetailsRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        detail.getProject(),
        detail.getVersion()
    )).thenReturn(Optional.empty());

    var forwardWorkPlanProjectContributorsView = forwardWorkPlanProjectContributorSummaryService.getProjectContributorsView(
        detail.getProject(),
        detail.getVersion()
    );

    assertThat(forwardWorkPlanProjectContributorsView)
        .extracting(ProjectContributorsView::getOrganisationGroupNames)
        .asList()
        .isEmpty();

    assertThat(forwardWorkPlanProjectContributorsView.getHasProjectContributors()).isNull();
  }
}
