package uk.co.ogauthority.pathfinder.service.project.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTaskGroup;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TaskListTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class TaskListGroupsServiceTest {

  @Mock
  private ProjectTaskService projectTaskService;

  @Mock
  private TaskListEntryCreatorService taskListEntryCreatorService;

  private TaskListGroupsService taskListGroupsService;

  private static final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    taskListGroupsService = new TaskListGroupsService(
        projectTaskService,
        taskListEntryCreatorService
    );
    when(projectTaskService.canShowTask(any(), any())).thenReturn(true);
    when(taskListEntryCreatorService.createTaskListEntry(any(), any())).thenReturn(TaskListTestUtil.getTaskListEntry());
  }

  @Test
  public void getTaskListGroups_whenInfrastructure_correctNumberOfGroups() {
    final var projectType = ProjectType.INFRASTRUCTURE;
    detail.setProjectType(projectType);
    assertThatCorrectProjectTaskGroupsAreReturned(projectType, detail);
  }

  @Test
  public void getTaskListGroups_whenForwardWorkPlan_correctNumberOfGroups() {
    final var projectType = ProjectType.FORWARD_WORK_PLAN;
    detail.setProjectType(projectType);
    assertThatCorrectProjectTaskGroupsAreReturned(projectType, detail);
  }

  private List<ProjectTaskGroup> getProjectTaskGroupsByProjectType(ProjectType projectType) {
    return ProjectTaskGroup.asList()
        .stream()
        .filter(projectTaskGroup -> projectTaskGroup.getRelatedProjectTypes().contains(projectType))
        .collect(Collectors.toList());
  }

  private void assertThatCorrectProjectTaskGroupsAreReturned(ProjectType projectType,
                                                             ProjectDetail projectDetail) {
    var taskListGroupsForProjectDetail = taskListGroupsService.getTaskListGroups(projectDetail);
    var expectedProjectTaskGroups = getProjectTaskGroupsByProjectType(projectType);
    assertThat(taskListGroupsForProjectDetail.size()).isEqualTo(expectedProjectTaskGroups.size() + 1); // +1 for review and submit
  }

  @Test
  public void getTaskListGroups_correctOrder() {
    var groups = taskListGroupsService.getTaskListGroups(detail);
    IntStream.range(0, groups.size())
        .forEach(index -> assertThat(groups.get(index).getDisplayOrder()).isEqualTo(index + 1));
  }

  @Test
  public void getTaskListGroups_groupNotReturnedWhenNoTasks() {
    when(projectTaskService.canShowTask(ProjectTask.PROJECT_OPERATOR, detail)).thenReturn(false);

    var groups = taskListGroupsService.getTaskListGroups(detail);
    assertThat(groups).noneMatch(g -> g.getGroupName().equals(ProjectTaskGroup.PROJECT_OPERATOR.getDisplayName()));
  }
}
