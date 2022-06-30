package uk.co.ogauthority.pathfinder.testutil;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTaskGroup;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListEntry;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListGroup;

public class TaskListTestUtil {

  public static final ProjectTaskGroup DEFAULT_PROJECT_GROUP = ProjectTaskGroup.COMMERCIAL_INFORMATION;
  public static final ProjectTask DEFAULT_PROJECT_TASK = ProjectTask.AWARDED_CONTRACTS;
  public static final boolean IS_COMPLETE = true;
  public static final ProjectDetail PROJECT_DETAIL = ProjectUtil.getProjectDetails();
  public static final boolean SHOW_COMPLETION_TAG = true;

  public static TaskListEntry getTaskListEntry() {
    return new TaskListEntry(
        DEFAULT_PROJECT_TASK.getDisplayName(),
        DEFAULT_PROJECT_TASK.getTaskLandingPageRoute(PROJECT_DETAIL.getProject()),
        IS_COMPLETE,
        SHOW_COMPLETION_TAG,
        DEFAULT_PROJECT_TASK.getDisplayOrder()
    );
  }

  public static TaskListEntry getTaskListEntry(ProjectDetail detail) {
    return new TaskListEntry(
        DEFAULT_PROJECT_TASK.getDisplayName(),
        DEFAULT_PROJECT_TASK.getTaskLandingPageRoute(detail.getProject()),
        IS_COMPLETE,
        SHOW_COMPLETION_TAG,
        DEFAULT_PROJECT_TASK.getDisplayOrder()
    );
  }

  public static TaskListEntry getTaskListEntry(boolean useNotCompletedLabels) {
    return new TaskListEntry(
        DEFAULT_PROJECT_TASK.getDisplayName(),
        DEFAULT_PROJECT_TASK.getTaskLandingPageRoute(PROJECT_DETAIL.getProject()),
        IS_COMPLETE,
        useNotCompletedLabels,
        DEFAULT_PROJECT_TASK.getDisplayOrder()
    );
  }

  public static TaskListGroup getTaskListGroup() {
    return new TaskListGroup(
        DEFAULT_PROJECT_GROUP.getDisplayName(),
        DEFAULT_PROJECT_GROUP.getDisplayOrder(),
        List.of(
            getTaskListEntry()
        )
    );
  }

  public static TaskListGroup getTaskListGroup(ProjectTaskGroup group) {
    return new TaskListGroup(
        group.getDisplayName(),
        group.getDisplayOrder(),
        List.of(
            getTaskListEntry()
        )
    );
  }

}
