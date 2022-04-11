package uk.co.ogauthority.pathfinder.testutil;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.tasks.ProjectTaskListSetup;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionQuestion;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupForm;

/**
 * Util for getting test forms and ProjectTaskListSetup objects for tests.
 * The question and answer sets match each other for testing purposes.
 */
public class ProjectTaskListSetupTestUtil {

  public static final List<TaskListSectionQuestion> NON_DECOM_SECTIONS = List.of(
      TaskListSectionQuestion.PROJECT_CONTRIBUTORS,
      TaskListSectionQuestion.AWARDED_CONTRACTS,
      TaskListSectionQuestion.UPCOMING_TENDERS,
      TaskListSectionQuestion.CAMPAIGN_INFORMATION
  );

  public static final List<TaskListSectionAnswer> NON_DECOM_ANSWERS = List.of(
      TaskListSectionAnswer.PROJECT_CONTRIBUTORS_YES,
      TaskListSectionAnswer.AWARDED_CONTRACTS_YES,
      TaskListSectionAnswer.UPCOMING_TENDERS_YES,
      TaskListSectionAnswer.COLLABORATION_OPPORTUNITIES_NO,
      TaskListSectionAnswer.CAMPAIGN_INFORMATION_YES
  );

  public static final List<TaskListSectionQuestion> DECOM_SECTIONS = List.of(
      TaskListSectionQuestion.PROJECT_CONTRIBUTORS,
      TaskListSectionQuestion.UPCOMING_TENDERS,
      TaskListSectionQuestion.AWARDED_CONTRACTS,
      TaskListSectionQuestion.WELLS,
      TaskListSectionQuestion.CAMPAIGN_INFORMATION,
      TaskListSectionQuestion.SUBSEA_INFRASTRUCTURE
  );

  public static final List<TaskListSectionAnswer> DECOM_ANSWERS = List.of(
      TaskListSectionAnswer.PROJECT_CONTRIBUTORS_YES,
      TaskListSectionAnswer.UPCOMING_TENDERS_YES,
      TaskListSectionAnswer.AWARDED_CONTRACTS_YES,
      TaskListSectionAnswer.COLLABORATION_OPPORTUNITIES_NO,
      TaskListSectionAnswer.CAMPAIGN_INFORMATION_YES,
      TaskListSectionAnswer.WELLS_YES,
      TaskListSectionAnswer.PLATFORM_FPSO_NO,
      TaskListSectionAnswer.INTEGRATED_RIGS_NO,
      TaskListSectionAnswer.SUBSEA_INFRASTRUCTURE_YES
      // TaskListSectionAnswer.PIPELINES_NO // Pipelines disabled: PAT-457
  );

  public static final List<TaskListSectionAnswer> ONLY_DECOM_ANSWERS = List.of(
      TaskListSectionAnswer.WELLS_YES,
      TaskListSectionAnswer.PLATFORM_FPSO_NO,
      TaskListSectionAnswer.INTEGRATED_RIGS_NO,
      TaskListSectionAnswer.SUBSEA_INFRASTRUCTURE_YES
      // TaskListSectionAnswer.PIPELINES_NO // Pipelines disabled: PAT-457
  );

  public static final List<TaskListSectionQuestion> ONLY_DECOM_SECTIONS = List.of(
      TaskListSectionQuestion.WELLS,
      TaskListSectionQuestion.SUBSEA_INFRASTRUCTURE
  );


  public static ProjectTaskListSetup getProjectTaskListSetup_nonDecom(ProjectDetail detail) {
    var setup = new ProjectTaskListSetup(detail);
    setup.setTaskListAnswers(NON_DECOM_ANSWERS);
    setup.setTaskListSections(NON_DECOM_SECTIONS);
    return setup;
  }

  public static ProjectTaskListSetup getProjectTaskListSetup_decomSections(ProjectDetail detail) {
    var setup = new ProjectTaskListSetup(detail);
    setup.setTaskListAnswers(DECOM_ANSWERS);
    setup.setTaskListSections(DECOM_SECTIONS);
    return setup;
  }

  public static ProjectSetupForm getProjectSetupForm_nonDecom() {
    var form = new ProjectSetupForm();
    setCommonFields(form);
    return form;
  }

  public static ProjectSetupForm getProjectSetupForm_withDecomSections(){
    var form = getProjectSetupForm_nonDecom();
    form.setWellsIncluded(TaskListSectionAnswer.WELLS_YES);
    form.setPlatformsFpsosIncluded(TaskListSectionAnswer.PLATFORM_FPSO_NO);
    form.setSubseaInfrastructureIncluded(TaskListSectionAnswer.SUBSEA_INFRASTRUCTURE_YES);
    form.setIntegratedRigsIncluded(TaskListSectionAnswer.INTEGRATED_RIGS_NO);
    // form.setPipelinesIncluded(TaskListSectionAnswer.PIPELINES_NO); // Pipelines disabled: PAT-457
    return form;
  }

  private static void setCommonFields(ProjectSetupForm form) {
    form.setUpcomingTendersIncluded(TaskListSectionAnswer.UPCOMING_TENDERS_YES);
    form.setAwardedContractsIncluded(TaskListSectionAnswer.AWARDED_CONTRACTS_YES);
    form.setCollaborationOpportunitiesIncluded(TaskListSectionAnswer.COLLABORATION_OPPORTUNITIES_NO);
    form.setCampaignInformationIncluded(TaskListSectionAnswer.CAMPAIGN_INFORMATION_YES);
    form.setProjectContributorsIncluded(TaskListSectionAnswer.PROJECT_CONTRIBUTORS_YES);
  }
}
