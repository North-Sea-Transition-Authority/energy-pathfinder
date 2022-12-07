package uk.co.ogauthority.pathfinder.service.project.workoplanprojectcontribution;

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
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.workplanprojectcontributor.ForwardWorkPlanProjectContributorsView;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanProjectContributorSectionSummaryServiceTest {

  private final ProjectDetail detail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  @Mock
  private ForwardWorkPlanProjectContributorFormSectionService forwardWorkPlanProjectContributorFormSectionService;

  @Mock
  private ForwardWorkPlanProjectContributorSummaryService forwardWorkPlanProjectContributorSummaryService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Mock
  private DifferenceService differenceService;

  private ForwardWorkPlanProjectContributorSectionSummaryService forwardWorkPlanProjectContributorSectionSummaryService;

  @Before
  public void setup() {
    forwardWorkPlanProjectContributorSectionSummaryService = new ForwardWorkPlanProjectContributorSectionSummaryService(
        forwardWorkPlanProjectContributorFormSectionService,
        forwardWorkPlanProjectContributorSummaryService,
        projectSectionSummaryCommonModelService,
        differenceService
    );
  }

  @Test
  public void canShowInSection_whenCanShow_assertTrue() {
    when(forwardWorkPlanProjectContributorFormSectionService.isTaskValidForProjectDetail(detail)).thenReturn(true);
    assertThat(forwardWorkPlanProjectContributorSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowInSection_whenCannotShow_assertFalse() {
    when(forwardWorkPlanProjectContributorFormSectionService.isTaskValidForProjectDetail(detail)).thenReturn(false);
    assertThat(forwardWorkPlanProjectContributorSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @Test
  public void getSummary_previousContributorsExist_verifyMethodCalls() {
    var listOfOrgNames = List.of("Org1", "Org2");
    var currentProjectContributorView = new ForwardWorkPlanProjectContributorsView(listOfOrgNames, true);
    var previousProjectContributorView = new ForwardWorkPlanProjectContributorsView(listOfOrgNames, true);

    when(forwardWorkPlanProjectContributorSummaryService.getForwardWorkPlanProjectContributorsView(detail)).thenReturn(
        currentProjectContributorView);
    when(forwardWorkPlanProjectContributorSummaryService.getForwardWorkPlanProjectContributorsView(detail.getProject(),
        detail.getVersion() - 1))
        .thenReturn(previousProjectContributorView);

    var sectionSummary = forwardWorkPlanProjectContributorSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary, detail);
    assertInteractions(currentProjectContributorView, previousProjectContributorView);
  }

  @Test
  public void getSummary_noPreviousContributorsExist_verifyMethodCalls() {
    var listOfOrgNames = List.of("Org1", "Org2");
    var currentProjectContributorView = new ForwardWorkPlanProjectContributorsView(listOfOrgNames, true);
    var previousProjectContributorView = new ForwardWorkPlanProjectContributorsView(List.of(), true);

    when(forwardWorkPlanProjectContributorSummaryService.getForwardWorkPlanProjectContributorsView(detail)).thenReturn(
        currentProjectContributorView);
    when(forwardWorkPlanProjectContributorSummaryService.getForwardWorkPlanProjectContributorsView(detail.getProject(),
        detail.getVersion() - 1))
        .thenReturn(previousProjectContributorView);

    var sectionSummary = forwardWorkPlanProjectContributorSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary, detail);
    assertInteractions(currentProjectContributorView, previousProjectContributorView);
  }

  private void assertModelProperties(ProjectSectionSummary sectionSummary, ProjectDetail projectDetail) {

    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(
        ForwardWorkPlanProjectContributorSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(
        List.of(ForwardWorkPlanProjectContributorSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(
        ForwardWorkPlanProjectContributorSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        ForwardWorkPlanProjectContributorSectionSummaryService.PAGE_NAME,
        ForwardWorkPlanProjectContributorSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys("projectContributorDiffModel", "showContributorsList");
  }

  private void assertInteractions(ForwardWorkPlanProjectContributorsView projectContributorsView,
                                  ForwardWorkPlanProjectContributorsView previousProjectContributorView) {
    verify(differenceService, times(1)).differentiate(projectContributorsView, previousProjectContributorView);
  }
}