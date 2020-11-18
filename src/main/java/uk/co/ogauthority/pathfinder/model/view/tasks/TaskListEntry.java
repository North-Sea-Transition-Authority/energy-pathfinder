package uk.co.ogauthority.pathfinder.model.view.tasks;

/**
 * A single task within the task list.
 */
public class TaskListEntry {

  private final String taskName;
  private final String route;
  private final boolean completed;
  private final boolean useCompletedLabels;
  private final int displayOrder;

  public TaskListEntry(String taskName,
                       String route,
                       boolean completed,
                       boolean useCompletedLabels,
                       int displayOrder) {
    this.taskName = taskName;
    this.route = route;
    this.completed = completed;
    this.useCompletedLabels = useCompletedLabels;
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

  public boolean isUseCompletedLabels() {
    return useCompletedLabels;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }
}
