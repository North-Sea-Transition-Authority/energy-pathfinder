package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.StartProjectService;

public class ProjectUtil {

  public static final ProjectStatus STATUS = ProjectStatus.DRAFT;
  public static final Integer WUA = 1;

  public static ProjectDetail getProjectDetails() {
    return new ProjectDetail(
        getProject(),
        STATUS,
        WUA,
        StartProjectService.FIRST_VERSION,
        StartProjectService.CURRENT_VERSION
    );
  }

  public static Project getProject() {
    return new Project();
  }
}
