package uk.co.ogauthority.pathfinder.service.project.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTaskGroup;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListGroup;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TaskListTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class TaskListServiceTest {


  @Mock
  private ProjectTaskService projectTaskService;

  @Mock
  private TaskListEntryCreatorService taskListEntryCreatorService;

  private TaskListService taskListService;

  private static final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() throws Exception {
    taskListService = new TaskListService(
        projectTaskService,
        taskListEntryCreatorService
    );
    when(projectTaskService.canShowTask(any(), any())).thenReturn(true);
    when(taskListEntryCreatorService.createTaskListEntry(any(), any())).thenReturn(TaskListTestUtil.getTaskListEntry());
  }

  @Test
  public void getTaskListModelAndView_correctViewPath() {
    var modelAndView = taskListService.getTaskListModelAndView(detail);
    assertThat(modelAndView.getViewName()).isEqualTo(TaskListService.TASK_LIST_TEMPLATE_PATH);
  }

  @Test
  public void getTaskListModelAndView_correctNumberOfGroups() {
    var modelAndView = taskListService.getTaskListModelAndView(detail);
    var modelMap = modelAndView.getModel();
    List<TaskListGroup> groups = (List<TaskListGroup>) modelMap.get("groups");

    assertThat(groups.size()).isEqualTo(ProjectTaskGroup.asList().size() + 1 ); //+1 for review and submit
  }

  @Test
  public void getTaskListGroups_correctNumberOfGroups() {
    var groups = taskListService.getTaskListGroups(detail);
    assertThat(groups.size()).isEqualTo(ProjectTaskGroup.asList().size() + 1); //+1 for review and submit
  }

  @Test
  public void getTaskListGroups_correctOrder() {
    var groups = taskListService.getTaskListGroups(detail);
    IntStream.range(0, groups.size())
        .forEach(index -> {
          assertThat(groups.get(index).getDisplayOrder()).isEqualTo(index + 1);
        });
  }

  @Test
  public void getTaskListGroups_groupNotReturnedWhenNoTasks() {
    when(projectTaskService.canShowTask(ProjectTask.PROJECT_OPERATOR, detail)).thenReturn(false);

    var groups = taskListService.getTaskListGroups(detail);
    assertThat(groups).noneMatch(g -> g.getGroupName().equals(ProjectTaskGroup.PROJECT_OPERATOR.getDisplayName()));
  }
}
