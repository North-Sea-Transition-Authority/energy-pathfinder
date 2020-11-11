package uk.co.ogauthority.pathfinder.model.enums.project.tasks;

/**
 * Enum to store the two possible answers for any {@link TaskListSectionQuestion}. A list of these
 * is stored against the {@link uk.co.ogauthority.pathfinder.model.entity.project.tasks.ProjectTaskListSetup}
 * to keep track of what a user has asked to be included on their project.
 */
public enum TaskListSectionAnswer {
  UPCOMING_TENDERS_YES,
      //(TaskListSectionQuestion.UPCOMING_TENDERS),
  UPCOMING_TENDERS_NO,
      //(TaskListSectionQuestion.UPCOMING_TENDERS),
  AWARDED_CONTRACTS_YES,
      //(TaskListSectionQuestion.AWARDED_CONTRACTS),
  AWARDED_CONTRACTS_NO,
      //(TaskListSectionQuestion.AWARDED_CONTRACTS),
  COLLABORATION_OPPORTUNITIES_YES,
      //(TaskListSectionQuestion.COLLABORATION_OPPORTUNITIES),
  COLLABORATION_OPPORTUNITIES_NO,
      //(TaskListSectionQuestion.COLLABORATION_OPPORTUNITIES),
  WELLS_YES,
      //(TaskListSectionQuestion.WELLS),
  WELLS_NO,
      //(TaskListSectionQuestion.WELLS),
  PLATFORM_FPSO_YES,
      //(TaskListSectionQuestion.PLATFORM_FPSO),
  PLATFORM_FPSO_NO,
      //(TaskListSectionQuestion.PLATFORM_FPSO),
  SUBSEA_INFRASTRUCTURE_YES,
      //(TaskListSectionQuestion.SUBSEA_INFRASTRUCTURE),
  SUBSEA_INFRASTRUCTURE_NO,
      //(TaskListSectionQuestion.SUBSEA_INFRASTRUCTURE),
  INTEGRATED_RIGS_YES,
      //(TaskListSectionQuestion.INTEGRATED_RIGS),
  INTEGRATED_RIGS_NO,
      //(TaskListSectionQuestion.INTEGRATED_RIGS),
  PIPELINES_YES,
      //(TaskListSectionQuestion.PIPELINES),
  PIPELINES_NO;
      //(TaskListSectionQuestion.PIPELINES);

//  private final TaskListSectionQuestion question;

//  TaskListSectionAnswer(TaskListSectionQuestion question) {
//    this.question = question;
//  }

//  public TaskListSectionQuestion getQuestion() {
//    return question;
//  }
}
