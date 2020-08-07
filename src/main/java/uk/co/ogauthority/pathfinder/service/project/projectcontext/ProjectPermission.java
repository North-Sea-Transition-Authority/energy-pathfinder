package uk.co.ogauthority.pathfinder.service.project.projectcontext;

/**
 * Actions a user can perform on a project.
 * Which of these a user has is decided by the {@link ProjectContextService}.
 */
public enum ProjectPermission {
  EDIT,
  SUBMIT,
  VIEW,
  VIEW_TASK_LIST;
}
