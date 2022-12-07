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

  public static final List<TaskListSectionQuestion> FIELD_STAGE_INDEPENDENT_SECTIONS = List.of(
      TaskListSectionQuestion.PROJECT_CONTRIBUTORS,
      TaskListSectionQuestion.AWARDED_CONTRACTS,
      TaskListSectionQuestion.UPCOMING_TENDERS,
      TaskListSectionQuestion.CAMPAIGN_INFORMATION
  );

  public static final List<TaskListSectionAnswer> FIELD_STAGE_INDEPENDENT_SETUP_ANSWERS = List.of(
      TaskListSectionAnswer.PROJECT_CONTRIBUTORS_YES,
      TaskListSectionAnswer.AWARDED_CONTRACTS_YES,
      TaskListSectionAnswer.UPCOMING_TENDERS_YES,
      TaskListSectionAnswer.COLLABORATION_OPPORTUNITIES_NO,
      TaskListSectionAnswer.CAMPAIGN_INFORMATION_YES
  );

  public static final List<TaskListSectionQuestion> DECOMMISSIONING_FIELD_STAGE_SECTIONS = List.of(
      TaskListSectionQuestion.PROJECT_CONTRIBUTORS,
      TaskListSectionQuestion.UPCOMING_TENDERS,
      TaskListSectionQuestion.AWARDED_CONTRACTS,
      TaskListSectionQuestion.WELLS,
      TaskListSectionQuestion.CAMPAIGN_INFORMATION,
      TaskListSectionQuestion.SUBSEA_INFRASTRUCTURE
  );

  public static final List<TaskListSectionAnswer> DECOMMISSIONING_FIELD_STAGE_SETUP_ANSWERS = List.of(
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

  public static ProjectTaskListSetup getProjectTaskListSetupWithFieldStageIndependentSectionsAnswered(ProjectDetail detail) {
    var setup = new ProjectTaskListSetup(detail);
    setup.setTaskListAnswers(FIELD_STAGE_INDEPENDENT_SETUP_ANSWERS);
    setup.setTaskListSections(FIELD_STAGE_INDEPENDENT_SECTIONS);
    return setup;
  }

  public static ProjectTaskListSetup getProjectSetupWithDecommissioningSectionsAnswered(ProjectDetail detail) {
    var setup = new ProjectTaskListSetup(detail);
    setup.setTaskListAnswers(DECOMMISSIONING_FIELD_STAGE_SETUP_ANSWERS);
    setup.setTaskListSections(DECOMMISSIONING_FIELD_STAGE_SECTIONS);
    return setup;
  }

  public static ProjectSetupForm getProjectSetupFormWithFieldStageIndependentSectionsAnswered() {
    var form = new ProjectSetupForm();
    setCommonFields(form);
    return form;
  }

  public static ProjectSetupForm getProjectSetupFormWithDecommissioningSectionsAnswered() {
    var form = new ProjectSetupForm();
    setCommonFields(form);
    form.setWellsIncluded(TaskListSectionAnswer.WELLS_YES);
    form.setPlatformsFpsosIncluded(TaskListSectionAnswer.PLATFORM_FPSO_NO);
    form.setSubseaInfrastructureIncluded(TaskListSectionAnswer.SUBSEA_INFRASTRUCTURE_YES);
    form.setIntegratedRigsIncluded(TaskListSectionAnswer.INTEGRATED_RIGS_NO);
    // form.setPipelinesIncluded(TaskListSectionAnswer.PIPELINES_NO); // Pipelines disabled: PAT-457
    return form;
  }

  public static ProjectSetupForm getProjectSetupFormWithAllSectionsAnswered() {
    var form = new ProjectSetupForm();
    setCommonFields(form);
    form.setWellsIncluded(TaskListSectionAnswer.WELLS_YES);
    form.setPlatformsFpsosIncluded(TaskListSectionAnswer.PLATFORM_FPSO_NO);
    form.setSubseaInfrastructureIncluded(TaskListSectionAnswer.SUBSEA_INFRASTRUCTURE_YES);
    form.setIntegratedRigsIncluded(TaskListSectionAnswer.INTEGRATED_RIGS_NO);
    // form.setPipelinesIncluded(TaskListSectionAnswer.PIPELINES_NO); // Pipelines disabled: PAT-457
    form.setCommissionedWellsIncluded(TaskListSectionAnswer.COMMISSION_WELLS_YES);
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
