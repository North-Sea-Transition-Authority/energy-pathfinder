package uk.co.ogauthority.pathfinder.model.enums.project.tasks;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.model.enums.DisplayableEnum;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

/**
 * An enum to manage which {@see ProjectTask}s are conditionally shown
 * each question links to a task which will appear if it has been answered.
 */
public enum TaskListSectionQuestion implements DisplayableEnum {

  //TODO when working out in the ProjectSetUpService if a page should appear we can fetch the
  // ProjectTaskListSetUp entity and stream it's list of TaskListSectionQuestions to see if they contain the
  // matching ProjectTask then we'll know
  // if the user has answered the corresponding question
  UPCOMING_TENDERS(
      ProjectTask.UPCOMING_TENDERS,
      ProjectTask.UPCOMING_TENDERS.getDisplayName(),
      false),
  AWARDED_CONTRACTS(
      ProjectTask.AWARDED_CONTRACTS,
      ProjectTask.AWARDED_CONTRACTS.getDisplayName(),
      false
  ),
  COLLABORATION_OPPORTUNITIES(
      ProjectTask.COLLABORATION_OPPORTUNITIES,
      ProjectTask.COLLABORATION_OPPORTUNITIES.getDisplayName(),
      false
  ),
  WELLS(
      ProjectTask.WELLS,
      ProjectTask.WELLS.getDisplayName(),
      true
  ),
  PLATFORM_FPSO(
      ProjectTask.PLATFORM_FPSO,
      ProjectTask.PLATFORM_FPSO.getDisplayName(),
      true
  ),
  SUBSEA_INFRASTRUCTURE(
      ProjectTask.SUBSEA_INFRASTRUCTURE,
      ProjectTask.SUBSEA_INFRASTRUCTURE.getDisplayName(),
      true
  ),
  INTEGRATED_RIGS(
      ProjectTask.INTEGRATED_RIGS,
      ProjectTask.INTEGRATED_RIGS.getDisplayName(),
      true
  ),
  PIPELINES(
      ProjectTask.PIPELINES,
      ProjectTask.PIPELINES.getDisplayName(),
      true
  );

  private final ProjectTask projectTask;
  private final String displayName;
  private final boolean decommissioningRelated;

  TaskListSectionQuestion(ProjectTask projectTask, String displayName, boolean decommissioningRelated) {
    this.projectTask = projectTask;
    this.displayName = displayName;
    this.decommissioningRelated = decommissioningRelated;
  }

  public ProjectTask getProjectTask() {
    return projectTask;
  }

  public String getDisplayName() {
    return displayName;
  }

  public boolean isDecommissioningRelated() {
    return decommissioningRelated;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, TaskListSectionQuestion::getDisplayName));
  }

  public static Map<String, String> getNonDecommissioningRelatedAsMap() {
    return Arrays.stream(values())
        .filter(tlq -> !tlq.isDecommissioningRelated())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, TaskListSectionQuestion::getDisplayName));
  }
}
