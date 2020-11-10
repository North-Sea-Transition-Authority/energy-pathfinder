package uk.co.ogauthority.pathfinder.service.project.tasks;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;

/**
 * Define the methods common to all services which manage Project form pages which appear on the task list.
 */
public interface ProjectFormSectionService {

  boolean isComplete(ProjectDetail detail);

  /**
   * Used to show/hide the task list entry.
   * @return True if entry should be shown.
   */
  default boolean canShowInTaskList(ProjectDetail detail) {
    return true;
  }
}
