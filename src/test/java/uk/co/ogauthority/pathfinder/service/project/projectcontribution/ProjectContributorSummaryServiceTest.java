package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectcontributor.ProjectContributorsView;
import uk.co.ogauthority.pathfinder.repository.project.projectcontributor.ProjectContributorRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContributorSummaryServiceTest {

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Mock
  private ProjectContributorRepository projectContributorRepository;

  private ProjectContributorSummaryService projectContributorSummaryService;

  @Before
  public void setup() {
    projectContributorSummaryService = new ProjectContributorSummaryService(projectContributorRepository);
  }

  @Test
  public void getProjectContributorsViewByDetail_projectContributorsExist_assertGroupNames() {
    var projectContributor1 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 1);
    var projectContributor2 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 2);

    when(projectContributorRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        detail.getProject(), detail.getVersion()))
        .thenReturn(List.of(projectContributor1, projectContributor2));

    var projectContributorsView = projectContributorSummaryService.getProjectContributorsView(detail);

    assertThat(projectContributorsView)
        .extracting(ProjectContributorsView::getOrganisationGroupNames)
        .asList()
        .containsExactly(
            projectContributor1.getContributionOrganisationGroup().getName(),
            projectContributor2.getContributionOrganisationGroup().getName()
        );
  }

  @Test
  public void getProjectContributorsViewByDetail_projectContributorsDoesNotExist_assertNoGroupNames() {
    when(projectContributorRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        detail.getProject(), detail.getVersion()))
        .thenReturn(List.of());

    var projectContributorsView = projectContributorSummaryService.getProjectContributorsView(detail);

    assertThat(projectContributorsView)
        .extracting(ProjectContributorsView::getOrganisationGroupNames)
        .asList()
        .isEmpty();
  }

  @Test
  public void getProjectContributorsViewByProjectAndVersion_projectContributorsExist_assertGroupNames() {
    var projectContributor1 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 1);
    var projectContributor2 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 2);

    when(projectContributorRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        detail.getProject(), detail.getVersion()))
        .thenReturn(List.of(projectContributor1, projectContributor2));

    var projectContributorsView = projectContributorSummaryService.getProjectContributorsView(detail.getProject(),
        detail.getVersion());

    assertThat(projectContributorsView)
        .extracting(ProjectContributorsView::getOrganisationGroupNames)
        .asList()
        .containsExactly(
            projectContributor1.getContributionOrganisationGroup().getName(),
            projectContributor2.getContributionOrganisationGroup().getName()
        );
  }

  @Test
  public void getProjectContributorsViewByProjectAndVersion_projectContributorsDoesNotExist_assertNoGroupNames() {
    when(projectContributorRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        detail.getProject(), detail.getVersion()))
        .thenReturn(List.of());

    var projectContributorsView = projectContributorSummaryService.getProjectContributorsView(detail.getProject(),
        detail.getVersion());

    assertThat(projectContributorsView)
        .extracting(ProjectContributorsView::getOrganisationGroupNames)
        .asList()
        .isEmpty();
  }

  @Test
  public void getProjectContributorsView_assertContributorsNamesSorted() {
    var projectContributorA = ProjectContributorTestUtil.contributorWithGroupOrgIdAndName(detail, 3, "alpha");
    var projectContributorB = ProjectContributorTestUtil.contributorWithGroupOrgIdAndName(detail, 1, "beta");
    var projectContributorC = ProjectContributorTestUtil.contributorWithGroupOrgIdAndName(detail, 2, "charlie");

    when(projectContributorRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        detail.getProject(), detail.getVersion()))
        .thenReturn(List.of(projectContributorB, projectContributorC, projectContributorA));

    assertThat(projectContributorSummaryService.getProjectContributorsView(detail).getOrganisationGroupNames())
        .isSorted();
  }
}
