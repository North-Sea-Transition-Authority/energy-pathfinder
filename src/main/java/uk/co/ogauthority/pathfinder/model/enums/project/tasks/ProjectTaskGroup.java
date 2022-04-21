package uk.co.ogauthority.pathfinder.model.enums.project.tasks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public enum ProjectTaskGroup {

  PROJECT_OPERATOR(
      ProjectTask.PROJECT_OPERATOR.getDisplayName(),
      List.of(ProjectTask.PROJECT_OPERATOR),
      Set.of(ProjectType.INFRASTRUCTURE),
      10
  ),
  PREPARE_PROJECT(
      "Prepare project",
      List.of(
          ProjectTask.PROJECT_INFORMATION,
          ProjectTask.PROJECT_LOCATION,
          ProjectTask.PROJECT_SETUP
      ),
      Set.of(ProjectType.INFRASTRUCTURE),
      20
  ),
  PROJECT_CONTRIBUTORS(
      "Project contributors",
      List.of(
          ProjectTask.PROJECT_CONTRIBUTORS
      ),
      Set.of(ProjectType.INFRASTRUCTURE),
      25
  ),
  COMMERCIAL_INFORMATION(
    "Commercial information",
      List.of(
          ProjectTask.UPCOMING_TENDERS,
          ProjectTask.AWARDED_CONTRACTS,
          ProjectTask.COLLABORATION_OPPORTUNITIES,
          ProjectTask.CAMPAIGN_INFORMATION
      ),
      Set.of(ProjectType.INFRASTRUCTURE),
      30
  ),
  DECOMMISSIONING_SCHEDULES(
      "Scope & schedule information",
      List.of(
          ProjectTask.DECOMMISSIONING_SCHEDULE,
          ProjectTask.WELLS,
          ProjectTask.PLATFORM_FPSO,
          ProjectTask.INTEGRATED_RIGS,
          ProjectTask.SUBSEA_INFRASTRUCTURE,
          ProjectTask.PIPELINES
      ),
      Set.of(ProjectType.INFRASTRUCTURE),
      40
  ),
  WORK_PLAN_PROJECT_CONTRIBUTORS(
      "Contributors",
      List.of(
          ProjectTask.WORK_PLAN_PROJECT_CONTRIBUTORS
      ),
      Set.of(ProjectType.FORWARD_WORK_PLAN),
      10
  ),
  WORK_PLAN_COMMERCIAL_INFORMATION(
      "Commercial information",
      List.of(
          ProjectTask.WORK_PLAN_UPCOMING_TENDERS,
          ProjectTask.WORK_PLAN_COLLABORATION_OPPORTUNITIES
      ),
      Set.of(ProjectType.FORWARD_WORK_PLAN),
      20
  );


  private final String displayName;
  private final List<ProjectTask> tasks;
  private final Set<ProjectType> relatedProjectTypes;
  private final int displayOrder;

  ProjectTaskGroup(String displayName,
                   List<ProjectTask> tasks,
                   Set<ProjectType> relatedProjectTypes,
                   int displayOrder) {
    this.displayName = displayName;
    this.tasks = tasks;
    this.relatedProjectTypes = relatedProjectTypes;
    this.displayOrder = displayOrder;
  }

  public String getDisplayName() {
    return displayName;
  }

  public List<ProjectTask> getTasks() {
    return tasks;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }

  public static List<ProjectTaskGroup> asList() {
    return Arrays.stream(ProjectTaskGroup.values())
        .collect(Collectors.toList());
  }

  public Set<ProjectTask> getProjectTaskSet() {
    return new HashSet<>(tasks);
  }

  public Set<ProjectType> getRelatedProjectTypes() {
    return relatedProjectTypes;
  }
}
