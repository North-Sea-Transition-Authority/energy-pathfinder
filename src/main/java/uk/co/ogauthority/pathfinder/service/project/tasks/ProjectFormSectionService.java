package uk.co.ogauthority.pathfinder.service.project.tasks;

import java.util.Set;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;

/**
 * Define the methods common to all services which manage Project form pages which appear on the task list.
 */
public interface ProjectFormSectionService {

  boolean isComplete(ProjectDetail detail);

  /**
   * Used to show/hide the task list entry.
   * @param detail The detail of the project used to evaluate if the task should be shown
   * @param userToProjectRelationships The relation(s) between the user to the project
   * @return True if entry should be shown.
   */
  default boolean canShowInTaskList(ProjectDetail detail, Set<UserToProjectRelationship> userToProjectRelationships) {
    return false;
  }

  /**
   * Used to determine if a task is valid for a projectDetail.
   *
   * @return true if section is valid
   */
  default boolean isTaskValidForProjectDetail(ProjectDetail detail) {
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

  /**
   * Method to remove section data that is not relevant (e.g. values for conditional questions that are not shown).
   * This method will be called when submitting a project if the section associated is on the task list.
   * @param projectDetail the project detail to remove data from
   */
  default void removeSectionDataIfNotRelevant(ProjectDetail projectDetail) {
    // default is to not do anything. If appropriate consumers can
    // implement and remove relevant data
  }

  void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail);

  Set<ProjectType> getSupportedProjectTypes();

  /**
   * Method to determine if the section data should always be copied.
   * @param projectDetail the project detail being processed
   * @return true if we should always copy this sections data, false otherwise
   */
  default boolean alwaysCopySectionData(ProjectDetail projectDetail) {
    return false;
  }

  /**
   * Method to indicate if the section allows its data to be removed if the section
   * is no longer relevant to project at submission time.
   * @param projectDetail the project detail being processed
   * @return true if the section is allowed to have its data cleaned up, false otherwise
   */
  default boolean allowSectionDataCleanUp(ProjectDetail projectDetail) {
    return true;
  }
}
