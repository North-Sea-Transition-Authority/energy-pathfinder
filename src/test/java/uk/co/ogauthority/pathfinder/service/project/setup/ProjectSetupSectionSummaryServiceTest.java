package uk.co.ogauthority.pathfinder.service.project.setup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.tasks.ProjectTaskListSetup;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionQuestion;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.ProjectTaskListSetupTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSetupSectionSummaryServiceTest {

  @Mock
  private ProjectService projectService;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  private ProjectSetupSectionSummaryService projectSetupSectionSummaryService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private final ProjectTaskListSetup setup = ProjectTaskListSetupTestUtil.getProjectTaskListSetup_nonDecom(details);

  private final ProjectTaskListSetup decomSetup = ProjectTaskListSetupTestUtil.getProjectTaskListSetup_decomSections(details);

  @Before
  public void setUp() throws Exception {
    projectSetupSectionSummaryService = new ProjectSetupSectionSummaryService(
        projectService,
        projectSetupService,
        differenceService,
        projectSectionSummaryCommonModelService
    );
  }

  @Test
  public void getSummary() {
    when(projectSetupService.getProjectTaskListSetup(details)).thenReturn(Optional.empty());
    when(projectSetupService.isDecomRelated(details)).thenReturn(false);
    var summary = projectSetupSectionSummaryService.getSummary(details);
    var modelMap = summary.getTemplateModel();

    assertThat(summary.getTemplatePath()).isEqualTo(ProjectSetupSectionSummaryService.TEMPLATE_PATH);
    assertThat(summary.getDisplayOrder()).isEqualTo(ProjectSetupSectionSummaryService.DISPLAY_ORDER);
    assertThat(summary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectSetupSectionSummaryService.SECTION_LINK));

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        details,
        ProjectSetupSectionSummaryService.PAGE_NAME,
        ProjectSetupSectionSummaryService.SECTION_ID
    );

    assertThat(modelMap).containsOnlyKeys("projectSetupDiffModel");

    verify(differenceService, times(1)).differentiateComplexLists(
        any(),
        any(),
        eq(Set.of("question")),
        any(),
        any()
    );
  }

  @Test
  public void getSummaryItems_noQuestionAnswered_nonDecom() {
    when(projectSetupService.getProjectTaskListSetup(details)).thenReturn(Optional.empty());
    when(projectSetupService.isDecomRelated(details)).thenReturn(false);
    var summaryItems = projectSetupSectionSummaryService.getSummaryItems(details);

    assertThat(summaryItems.size()).isEqualTo(TaskListSectionQuestion.getNonDecommissioningRelatedValues().size());

    summaryItems.forEach(si -> assertThat(si.getAnswerValue()).isNull());
  }

  @Test
  public void getSummaryItems_correctQuestionAnswered_nonDecom() {
    when(projectSetupService.getProjectTaskListSetup(details)).thenReturn(Optional.of(setup));
    when(projectSetupService.isDecomRelated(details)).thenReturn(false);
    var summaryItems = projectSetupSectionSummaryService.getSummaryItems(details);

    assertThat(summaryItems.size()).isEqualTo(TaskListSectionQuestion.getNonDecommissioningRelatedValues().size());

    assertThat(summaryItems.get(0).getAnswerValue()).isEqualTo(setup.getTaskListAnswers().get(0).getAnswerValue());
    assertThat(summaryItems.get(1).getAnswerValue()).isEqualTo(setup.getTaskListAnswers().get(1).getAnswerValue());
    assertThat(summaryItems.get(2).getAnswerValue()).isEqualTo(setup.getTaskListAnswers().get(2).getAnswerValue());
  }

  @Test
  public void getSummaryItems_noQuestionAnswered_decom() {
    when(projectSetupService.getProjectTaskListSetup(details)).thenReturn(Optional.empty());
    when(projectSetupService.isDecomRelated(details)).thenReturn(true);
    var summaryItems = projectSetupSectionSummaryService.getSummaryItems(details);

    assertThat(summaryItems.size()).isEqualTo(TaskListSectionQuestion.getAllValues().size());

    summaryItems.forEach(si -> assertThat(si.getAnswerValue()).isNull());
  }

  @Test
  public void getSummaryItems_correctQuestionAnswered_decom() {
    when(projectSetupService.getProjectTaskListSetup(details)).thenReturn(Optional.of(decomSetup));
    when(projectSetupService.isDecomRelated(details)).thenReturn(true);
    var summaryItems = projectSetupSectionSummaryService.getSummaryItems(details);

    assertThat(summaryItems.size()).isEqualTo(TaskListSectionQuestion.getAllValues().size());

    assertThat(summaryItems.get(0).getAnswerValue()).isEqualTo(decomSetup.getTaskListAnswers().get(0).getAnswerValue());
    assertThat(summaryItems.get(1).getAnswerValue()).isEqualTo(decomSetup.getTaskListAnswers().get(1).getAnswerValue());
    assertThat(summaryItems.get(2).getAnswerValue()).isEqualTo(decomSetup.getTaskListAnswers().get(2).getAnswerValue());
    assertThat(summaryItems.get(3).getAnswerValue()).isEqualTo(decomSetup.getTaskListAnswers().get(3).getAnswerValue());
    assertThat(summaryItems.get(4).getAnswerValue()).isEqualTo(decomSetup.getTaskListAnswers().get(4).getAnswerValue());
    assertThat(summaryItems.get(5).getAnswerValue()).isEqualTo(decomSetup.getTaskListAnswers().get(5).getAnswerValue());
    // Subsea infrastructure disabled: PAT-495
    // assertThat(summaryItems.get(6).getAnswerValue()).isEqualTo(decomSetup.getTaskListAnswers().get(6).getAnswerValue());
    // Pipelines disabled: PAT-457
    // assertThat(summaryItems.get(7).getAnswerValue()).isEqualTo(decomSetup.getTaskListAnswers().get(7).getAnswerValue());
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(projectSetupService.canShowInTaskList(details)).thenReturn(true);

    assertThat(projectSetupSectionSummaryService.canShowSection(details)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(projectSetupService.canShowInTaskList(details)).thenReturn(false);

    assertThat(projectSetupSectionSummaryService.canShowSection(details)).isFalse();
  }
}
