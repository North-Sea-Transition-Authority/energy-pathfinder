package uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions;

/**
 * Enum to store the two possible answers for any {@link TaskListSectionQuestion}. A list of these
 * is stored against the {@link uk.co.ogauthority.pathfinder.model.entity.project.tasks.ProjectTaskListSetup}
 * to keep track of what a user has asked to be included on their project.
 */
public enum TaskListSectionAnswer {
  UPCOMING_TENDERS_YES("Yes"),
  UPCOMING_TENDERS_NO("No"),
  AWARDED_CONTRACTS_YES("Yes"),
  AWARDED_CONTRACTS_NO("No"),
  COLLABORATION_OPPORTUNITIES_YES("Yes"),
  COLLABORATION_OPPORTUNITIES_NO("No"),
  WELLS_YES("Yes"),
  WELLS_NO("No"),
  PLATFORM_FPSO_YES("Yes"),
  PLATFORM_FPSO_NO("No"),
  INTEGRATED_RIGS_YES("Yes"),
  INTEGRATED_RIGS_NO("No"),
  SUBSEA_INFRASTRUCTURE_YES("Yes"),
  SUBSEA_INFRASTRUCTURE_NO("No"),
  PIPELINES_YES("Yes"),
  PIPELINES_NO("No");

  private final String answerValue;

  TaskListSectionAnswer(String answerValue) {
    this.answerValue = answerValue;
  }



  public String getAnswerValue() {
    return answerValue;
  }
}
