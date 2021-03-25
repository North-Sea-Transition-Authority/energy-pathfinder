package uk.co.ogauthority.pathfinder.model.view.tasks;

import java.util.List;

/**
 * A group of tasks which are within a single logical grouping.
 */
public class TaskListGroup {

  private String groupName;
  private int displayOrder;
  private List<TaskListEntry> taskListEntries;

  public TaskListGroup(String groupName,
                       int displayOrder,
                       List<TaskListEntry> taskListEntries) {
    this.groupName = groupName;
    this.displayOrder = displayOrder;
    this.taskListEntries = taskListEntries;
  }

  public String getGroupName() {
    return groupName;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }

  public List<TaskListEntry> getTaskListEntries() {
    return taskListEntries;
  }

  public void setDisplayOrder(int displayOrder) {
    this.displayOrder = displayOrder;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public void setTaskListEntries(List<TaskListEntry> taskListEntries) {
    this.taskListEntries = taskListEntries;
  }
}
