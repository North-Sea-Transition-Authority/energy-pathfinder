package uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions;

/**
 * Enum to store the two possible answers for any {@link TaskListSectionQuestion}. A list of these
 * is stored against the {@link uk.co.ogauthority.pathfinder.model.entity.project.tasks.ProjectTaskListSetup}
 * to keep track of what a user has asked to be included on their project.
 */
public enum TaskListSectionAnswer {
  UPCOMING_TENDERS_YES,
  UPCOMING_TENDERS_NO,
  AWARDED_CONTRACTS_YES,
  AWARDED_CONTRACTS_NO,
  COLLABORATION_OPPORTUNITIES_YES,
  COLLABORATION_OPPORTUNITIES_NO,
  WELLS_YES,
  WELLS_NO,
  PLATFORM_FPSO_YES,
  PLATFORM_FPSO_NO,
  SUBSEA_INFRASTRUCTURE_YES,
  SUBSEA_INFRASTRUCTURE_NO,
  INTEGRATED_RIGS_YES,
  INTEGRATED_RIGS_NO,
  PIPELINES_YES,
  PIPELINES_NO;
}
