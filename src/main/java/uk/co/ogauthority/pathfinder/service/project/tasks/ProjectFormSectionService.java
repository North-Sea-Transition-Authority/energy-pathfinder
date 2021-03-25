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
    return false;
  }

  /**
   * Method to remove all section data. This method will be called when
   * submitting a project if the section associated is no longer an section
   * on the task list.
   * @param projectDetail the project detail to remove data from
   */
  default void removeSectionData(ProjectDetail projectDetail) {
    // default is to not do anything. If appropriate consumers can
    // implement and remove relevant data
  }

  void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail);
}
