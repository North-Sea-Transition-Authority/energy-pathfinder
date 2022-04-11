package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectcontributor.ProjectContributorsView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContributorsSectionSummaryServiceTest {

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Mock
  private ProjectContributorsFormSectionService projectContributorsFormSectionService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Mock
  private ProjectContributorSummaryService projectContributorSummaryService;

  @Mock
  private DifferenceService differenceService;

  private ProjectContributorsSectionSummaryService projectContributorsSectionSummaryService;

  @Before
  public void setup() {
    projectContributorsSectionSummaryService = new ProjectContributorsSectionSummaryService(
        projectContributorsFormSectionService,
        projectSectionSummaryCommonModelService,
        projectContributorSummaryService,
        differenceService);
  }

  @Test
  public void canShowInSection_whenCanShow_assertTrue() {
    when(projectContributorsFormSectionService.canShowInTaskList(detail)).thenReturn(true);
    assertThat(projectContributorsSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowInSection_whenCannotShow_assertFalse() {
    when(projectContributorsFormSectionService.canShowInTaskList(detail)).thenReturn(false);
    assertThat(projectContributorsSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @Test
  public void getSummary_previousContributorsExist_verifyMethodCalls() {
    var listOfOrgNames = List.of("Org1", "Org2");
    var currentProjectContributorView = new ProjectContributorsView(listOfOrgNames);
    var previousProjectContributorView = new ProjectContributorsView(listOfOrgNames);

    when(projectContributorSummaryService.getProjectContributorsView(detail)).thenReturn(currentProjectContributorView);
    when(projectContributorSummaryService.getProjectContributorsView(detail.getProject(), detail.getVersion() - 1))
        .thenReturn(previousProjectContributorView);

    var sectionSummary = projectContributorsSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary, detail);
    assertInteractions(currentProjectContributorView, previousProjectContributorView);
  }

  @Test
  public void getSummary_noPreviousContributorsExist_verifyMethodCalls() {
    var listOfOrgNames = List.of("Org1", "Org2");
    var currentProjectContributorView = new ProjectContributorsView(listOfOrgNames);
    var previousProjectContributorView = new ProjectContributorsView(List.of());

    when(projectContributorSummaryService.getProjectContributorsView(detail)).thenReturn(currentProjectContributorView);
    when(projectContributorSummaryService.getProjectContributorsView(detail.getProject(), detail.getVersion() - 1))
        .thenReturn(previousProjectContributorView);

    var sectionSummary = projectContributorsSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary, detail);
    assertInteractions(currentProjectContributorView, previousProjectContributorView);
  }

  private void assertModelProperties(ProjectSectionSummary sectionSummary, ProjectDetail projectDetail) {

    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(ProjectContributorsSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(
        List.of(ProjectContributorsSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(ProjectContributorsSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        ProjectContributorsSectionSummaryService.PAGE_NAME,
        ProjectContributorsSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys("projectContributorDiffModel");
  }

  private void assertInteractions(ProjectContributorsView projectContributorsView,
                                  ProjectContributorsView previousProjectContributorView) {
    verify(differenceService, times(1)).differentiate(projectContributorsView, previousProjectContributorView);
  }
}