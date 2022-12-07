package uk.co.ogauthority.pathfinder.model.view.tasks;

/**
 * A single task within the task list.
 */
public class TaskListEntry {

  private final String taskName;
  private final String route;
  private final boolean completed;
  private final boolean showTag;
  private final int displayOrder;

  public TaskListEntry(String taskName,
                       String route,
                       boolean completed,
                       boolean showTag,
                       int displayOrder) {
    this.taskName = taskName;
    this.route = route;
    this.completed = completed;
    this.showTag = showTag;
    this.displayOrder = displayOrder;
  }

  public String getTaskName() {
    return taskName;
  }

  public String getRoute() {
    return route;
  }

  public boolean isCompleted() {
    return completed;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }

  public boolean isShowTag() {
    return showTag;
  }
}
