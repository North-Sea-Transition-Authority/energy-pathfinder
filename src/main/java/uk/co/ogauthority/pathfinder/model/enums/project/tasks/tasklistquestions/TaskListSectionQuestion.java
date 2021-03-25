package uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;

/**
 * An enum to manage which {@see ProjectTask}s are conditionally shown
 * each question links to a task which will appear if it has been answered.
 * Each question has a yes or no answer in the form of a {@link TaskListSectionAnswer} this
 * is used to set out the form answer values for each individual question
 */
public enum TaskListSectionQuestion {

  UPCOMING_TENDERS(
      ProjectTask.UPCOMING_TENDERS,
      "Do you have any upcoming tenders on this project?",
      "Upcoming tenders",
      "",
      "form.upcomingTendersIncluded",
      TaskListSectionAnswer.UPCOMING_TENDERS_YES,
      TaskListSectionAnswer.UPCOMING_TENDERS_NO,
      false
  ),
  AWARDED_CONTRACTS(
      ProjectTask.AWARDED_CONTRACTS,
      "Do you have any awarded contracts on this project?",
      "Awarded contracts",
      "",
      "form.awardedContractsIncluded",
      TaskListSectionAnswer.AWARDED_CONTRACTS_YES,
      TaskListSectionAnswer.AWARDED_CONTRACTS_NO,
       false
  ),
  COLLABORATION_OPPORTUNITIES(
      ProjectTask.COLLABORATION_OPPORTUNITIES,
      "Do you have any collaboration opportunities on this project?",
      "Collaboration opportunities",
      "This area is for operators to use when seeking engagement with the supply chain to offer solutions to aid project delivery",
      "form.collaborationOpportunitiesIncluded",
      TaskListSectionAnswer.COLLABORATION_OPPORTUNITIES_YES,
      TaskListSectionAnswer.COLLABORATION_OPPORTUNITIES_NO,
      false
  ),
  WELLS(
      ProjectTask.WELLS,
      "Are wells being decommissioned on this project?",
      "Wells to be decommissioned",
      "",
      "form.wellsIncluded",
      TaskListSectionAnswer.WELLS_YES,
      TaskListSectionAnswer.WELLS_NO,
      true
  ),
  PLATFORM_FPSO(
      ProjectTask.PLATFORM_FPSO,
      "Are platforms or floating units such as FPSOs being decommissioned on this project?",
      "Platforms or floating production storage and offloading units to be decommissioned",
      "",
      "form.platformsFpsosIncluded",
      TaskListSectionAnswer.PLATFORM_FPSO_YES,
      TaskListSectionAnswer.PLATFORM_FPSO_NO,
      true
  ),
  INTEGRATED_RIGS(
      ProjectTask.INTEGRATED_RIGS,
      "Do any of the platforms on this project have an integrated (in-built/permanent) drilling rig?",
      "Integrated rigs to be decommissioned",
      "",
      "form.integratedRigsIncluded",
      TaskListSectionAnswer.INTEGRATED_RIGS_YES,
      TaskListSectionAnswer.INTEGRATED_RIGS_NO,
      true
  );
  // Subsea infrastructure disabled: PAT-495
  // SUBSEA_INFRASTRUCTURE(
  //     ProjectTask.SUBSEA_INFRASTRUCTURE,
  //     "Is subsea infrastructure being decommissioned on this project?",
  //     "Subsea infrastructure to be decommissioned",
  //     "",
  //     "form.subseaInfrastructureIncluded",
  //     TaskListSectionAnswer.SUBSEA_INFRASTRUCTURE_YES,
  //     TaskListSectionAnswer.SUBSEA_INFRASTRUCTURE_NO,
  //     true
  // );
  // Pipelines disabled: PAT-457
  // PIPELINES(
  //     ProjectTask.PIPELINES,
  //     "Are pipelines being decommissioned on this project?",
  //     "Pipelines to be decommissioned",
  //     "",
  //     "form.pipelinesIncluded",
  //     TaskListSectionAnswer.PIPELINES_YES,
  //     TaskListSectionAnswer.PIPELINES_NO,
  //     true
  // );

  private final ProjectTask projectTask;
  private final String displayName;
  private final String prompt;
  private final String guidance;
  private final String formField;
  private final TaskListSectionAnswer yesAnswer;
  private final TaskListSectionAnswer noAnswer;
  private final boolean decommissioningRelated;

  TaskListSectionQuestion(ProjectTask projectTask,
                          String displayName,
                          String prompt,
                          String guidance,
                          String formField,
                          TaskListSectionAnswer yesAnswer,
                          TaskListSectionAnswer noAnswer,
                          boolean decommissioningRelated) {
    this.projectTask = projectTask;
    this.displayName = displayName;
    this.prompt = prompt;
    this.guidance = guidance;
    this.formField = formField;
    this.yesAnswer = yesAnswer;
    this.noAnswer = noAnswer;
    this.decommissioningRelated = decommissioningRelated;
  }

  public ProjectTask getProjectTask() {
    return projectTask;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getGuidance() {
    return guidance;
  }

  public String getFormField() {
    return formField;
  }

  public TaskListSectionAnswer getYesAnswer() {
    return yesAnswer;
  }

  public TaskListSectionAnswer getNoAnswer() {
    return noAnswer;
  }

  public boolean isDecommissioningRelated() {
    return decommissioningRelated;
  }

  public static List<TaskListSectionQuestion> getAllValues() {
    var questions = Arrays.asList(values());
    sort(questions);
    return questions;
  }

  public static List<TaskListSectionQuestion> getNonDecommissioningRelatedValues() {
    var questions = Arrays.stream(values())
        .filter(tlq -> !tlq.isDecommissioningRelated())
        .collect(Collectors.toList());
    sort(questions);
    return questions;
  }

  private static void sort(List<TaskListSectionQuestion> questions) {
    questions.sort(Comparator.comparing(q -> q.getProjectTask().getDisplayOrder()));
  }
}
