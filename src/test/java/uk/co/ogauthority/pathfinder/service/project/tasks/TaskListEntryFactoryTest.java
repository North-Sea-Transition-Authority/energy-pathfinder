package uk.co.ogauthority.pathfinder.service.project.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.GeneralPurposeProjectTask;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListEntry;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TaskListTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class TaskListEntryFactoryTest {

  @Mock
  private ProjectTaskService projectTaskService;

  private TaskListEntryCreatorService taskListEntryCreatorService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final TestGeneralPurposeProjectTask testGeneralPurposeProjectTask = new TestGeneralPurposeProjectTask();

  @Before
  public void setUp() throws Exception {
    taskListEntryCreatorService = new TaskListEntryCreatorService(projectTaskService);
  }

  @Test
  public void createReviewAndSubmitTask() {
    var task = TaskListEntryCreatorService.createReviewAndSubmitTask(detail);
    assertThat(task.getTaskName()).isEqualTo(TaskListEntryCreatorService.REVIEW_AND_SUBMIT_GROUP_TITLE);
    assertThat(task.getDisplayOrder()).isEqualTo(TaskListEntryCreatorService.REVIEW_AND_SUBMIT_DISPLAY_ORDER);
    assertThat(task.isCompleted()).isFalse();
    assertThat(task.isUsingCompletedLabels()).isFalse();
  }


  @Test
  public void createTaskListEntry_isComplete() {
    when(projectTaskService.isTaskComplete(testGeneralPurposeProjectTask, detail)).thenReturn(true);
    var taskListEntry = taskListEntryCreatorService.createTaskListEntry(detail, testGeneralPurposeProjectTask);
    assertThat(taskListEntry.isCompleted()).isTrue();
    checkCommonFields(taskListEntry);
  }

  @Test
  public void createTaskListEntry_isNotComplete() {
    when(projectTaskService.isTaskComplete(testGeneralPurposeProjectTask, detail)).thenReturn(false);
    var taskListEntry = taskListEntryCreatorService.createTaskListEntry(detail, testGeneralPurposeProjectTask);
    assertThat(taskListEntry.isCompleted()).isFalse();
    checkCommonFields(taskListEntry);
  }

  //Check common fields against the TestGeneralPurposeProjectTask
  private void checkCommonFields(TaskListEntry taskListEntry) {
    assertThat(taskListEntry.isUsingCompletedLabels()).isTrue(); // our default
    assertThat(taskListEntry.getDisplayOrder()).isEqualTo(testGeneralPurposeProjectTask.getDisplayOrder());
    assertThat(taskListEntry.getRoute()).isEqualTo(testGeneralPurposeProjectTask.getTaskLandingPageRoute(detail.getProject()));
  }

  /**
   * class for use in tests only which implements the generic interface required by class under test
   */
  private static class TestGeneralPurposeProjectTask implements GeneralPurposeProjectTask{
    @Override
    public Class<? extends ProjectFormSectionService> getServiceClass() {
      return ProjectFormSectionService.class;
    }

    @Override
    public Class getControllerClass() {
      return null;
    }

    @Override
    public int getDisplayOrder() {
      return TaskListTestUtil.DEFAULT_PROJECT_TASK.getDisplayOrder();
    }

    @Override
    public String getDisplayName() {
      return TaskListTestUtil.DEFAULT_PROJECT_TASK.getDisplayName();
    }

    @Override
    public String getTaskLandingPageRoute(Project project) {
      return TaskListTestUtil.DEFAULT_PROJECT_TASK.getTaskLandingPageRoute(project);
    }
  }
}
