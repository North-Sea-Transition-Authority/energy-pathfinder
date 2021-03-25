package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.StartProjectService;

public class ProjectUtil {

  public static final int PROJECT_ID = 1;
  public static final int PROJECT_DETAIL_ID = 2;
  public static final ProjectStatus STATUS = ProjectStatus.DRAFT;
  public static final Integer WUA = 1;

  public static ProjectDetail getProjectDetails() {
    return getProjectDetails(STATUS);
  }

  public static ProjectDetail getProjectDetails(ProjectStatus projectStatus) {
    return getProjectDetails(projectStatus, ProjectType.INFRASTRUCTURE);
  }

  public static ProjectDetail getProjectDetails(ProjectType projectType) {
    return getProjectDetails(STATUS, projectType);
  }

  public static ProjectDetail getProjectDetails(ProjectStatus projectStatus, ProjectType projectType) {
    var projectDetail = new ProjectDetail(
        getProject(),
        projectStatus,
        WUA,
        StartProjectService.FIRST_VERSION,
        StartProjectService.CURRENT_VERSION,
        projectType
    );
    projectDetail.setId(PROJECT_DETAIL_ID);
    return projectDetail;
  }

  public static Project getProject() {
    var project = new Project();
    project.setId(PROJECT_ID);
    return project;
  }
}
