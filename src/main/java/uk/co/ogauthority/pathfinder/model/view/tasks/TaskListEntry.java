package uk.co.ogauthority.pathfinder.model.view.tasks;

/**
 * A single task within the task list.
 */
public class TaskListEntry {

  private final String taskName;
  private final String route;
  private final boolean completed;
  private final boolean usingCompletedLabels;
  private final int displayOrder;

  public TaskListEntry(String taskName,
                       String route,
                       boolean completed,
                       boolean usingCompletedLabels,
                       int displayOrder) {
    this.taskName = taskName;
    this.route = route;
    this.completed = completed;
    this.usingCompletedLabels = usingCompletedLabels;
    this.displayOrder = displayOrder;
  }


  public TaskListEntry(String taskName,
                       String route,
                       boolean completed,
                       int displayOrder) {
    this(taskName, route, completed, true, displayOrder);
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

  public boolean isUsingCompletedLabels() {
    return usingCompletedLabels;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }
}
