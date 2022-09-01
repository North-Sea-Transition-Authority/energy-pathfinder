package uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions;

import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

/**
 * Enum to store the two possible answers for any {@link TaskListSectionQuestion}. A list of these
 * is stored against the {@link uk.co.ogauthority.pathfinder.model.entity.project.tasks.ProjectTaskListSetup}
 * to keep track of what a user has asked to be included on their project.
 */
public enum TaskListSectionAnswer {
  UPCOMING_TENDERS_YES(StringDisplayUtil.YES),
  UPCOMING_TENDERS_NO(StringDisplayUtil.NO),
  AWARDED_CONTRACTS_YES(StringDisplayUtil.YES),
  AWARDED_CONTRACTS_NO(StringDisplayUtil.NO),
  COLLABORATION_OPPORTUNITIES_YES(StringDisplayUtil.YES),
  COLLABORATION_OPPORTUNITIES_NO(StringDisplayUtil.NO),
  WELLS_YES(StringDisplayUtil.YES),
  WELLS_NO(StringDisplayUtil.NO),
  PLATFORM_FPSO_YES(StringDisplayUtil.YES),
  PLATFORM_FPSO_NO(StringDisplayUtil.NO),
  INTEGRATED_RIGS_YES(StringDisplayUtil.YES),
  INTEGRATED_RIGS_NO(StringDisplayUtil.NO),
  SUBSEA_INFRASTRUCTURE_YES(StringDisplayUtil.YES),
  SUBSEA_INFRASTRUCTURE_NO(StringDisplayUtil.NO),
  PIPELINES_YES(StringDisplayUtil.YES),
  PIPELINES_NO(StringDisplayUtil.NO),
  CAMPAIGN_INFORMATION_YES(StringDisplayUtil.YES),
  CAMPAIGN_INFORMATION_NO(StringDisplayUtil.NO),
  COMMISSION_WELLS_YES(StringDisplayUtil.YES),
  COMMISSION_WELLS_NO(StringDisplayUtil.NO);

  private final String answerValue;

  TaskListSectionAnswer(String answerValue) {
    this.answerValue = answerValue;
  }

  public String getAnswerValue() {
    return answerValue;
  }
}
