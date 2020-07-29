package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetails;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;

public class ProjectUtil {

  public static ProjectDetails getProjectDetails() {
    return new ProjectDetails(
        getProject(),
        ProjectStatus.DRAFT,
        1
    );
  }

  public static Project getProject() {
    return new Project();
  }
}
