package uk.co.ogauthority.pathfinder.service.project.setup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
  public void getSummaryItems_whenNoSetupQuestionsAnswered_thenAllSummaryResponsesNull() {

    var taskListSectionsQuestions = Arrays.asList(TaskListSectionQuestion.values());

    when(projectSetupService.getSectionQuestionsForProjectDetail(details))
        .thenReturn(taskListSectionsQuestions);

    var resultingSummaryItems = projectSetupSectionSummaryService.getSummaryItems(details);

    resultingSummaryItems.forEach(summaryItem -> {
      var taskListSectionQuestion = taskListSectionsQuestions
          .stream()
          .filter(sectionQuestion -> sectionQuestion.equals(summaryItem.getQuestion()))
          .findFirst()
          .orElseThrow(() -> new AssertionError(String.format(
              "Could not find TaskListSectionQuestion with name %s",
              summaryItem.getQuestion().name()
          )));

      assertThat(summaryItem.getQuestion()).isEqualTo(taskListSectionQuestion);
      assertThat(summaryItem.getPrompt()).isEqualTo(taskListSectionQuestion.getPrompt());
      assertThat(summaryItem.getAnswerValue()).isNull();
    });
  }

  @Test
  public void getSummaryItems_whenSetupQuestionsAnswered_thenAllSummaryItemsAnswersPopulated() {

    var taskListSectionsQuestions = Arrays.asList(TaskListSectionQuestion.values());

    var expectedTaskListSectionAnswers = taskListSectionsQuestions
        .stream()
        .map(TaskListSectionQuestion::getYesAnswer)
        .collect(Collectors.toUnmodifiableList());

    when(projectSetupService.getSectionQuestionsForProjectDetail(details))
        .thenReturn(Arrays.asList(TaskListSectionQuestion.values()));

    var projectTaskListSetup = new ProjectTaskListSetup(details);
    projectTaskListSetup.setTaskListSections(taskListSectionsQuestions);
    projectTaskListSetup.setTaskListAnswers(expectedTaskListSectionAnswers);

    when(projectSetupService.getProjectTaskListSetup(details))
        .thenReturn(Optional.of(projectTaskListSetup));

    var resultingSummaryItems = projectSetupSectionSummaryService.getSummaryItems(details);

    resultingSummaryItems.forEach(summaryItem -> {
      var taskListSectionQuestion = taskListSectionsQuestions
          .stream()
          .filter(sectionQuestion -> sectionQuestion.equals(summaryItem.getQuestion()))
          .findFirst()
          .orElseThrow(() -> new AssertionError(String.format(
              "Could not find TaskListSectionQuestion with name %s",
              summaryItem.getQuestion().name()
          )));

      assertThat(summaryItem.getQuestion()).isEqualTo(taskListSectionQuestion);
      assertThat(summaryItem.getPrompt()).isEqualTo(taskListSectionQuestion.getPrompt());
      assertThat(summaryItem.getAnswerValue()).isEqualTo(taskListSectionQuestion.getYesAnswer().getAnswerValue());
    });
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(projectSetupService.isTaskValidForProjectDetail(details)).thenReturn(true);

    assertThat(projectSetupSectionSummaryService.canShowSection(details)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(projectSetupService.isTaskValidForProjectDetail(details)).thenReturn(false);

    assertThat(projectSetupSectionSummaryService.canShowSection(details)).isFalse();
  }
}
