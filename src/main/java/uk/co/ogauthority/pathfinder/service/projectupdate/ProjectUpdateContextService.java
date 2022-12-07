package uk.co.ogauthority.pathfinder.service.projectupdate;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectDetailVersionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;

@Service
public class ProjectUpdateContextService {

  private final ProjectContextService projectContextService;
  private final ProjectUpdateService projectUpdateService;

  @Autowired
  public ProjectUpdateContextService(ProjectContextService projectContextService,
                                     ProjectUpdateService projectUpdateService) {
    this.projectContextService = projectContextService;
    this.projectUpdateService = projectUpdateService;
  }

  public boolean canBuildContext(ProjectDetail detail,
                                 AuthenticatedUserAccount user,
                                 Class<?> controllerClass) {
    var statusCheck = projectContextService.getProjectStatusesForClass(controllerClass);
    var permissionCheck = projectContextService.getProjectPermissionsForClass(controllerClass);
    final var allowedProjectTypes = projectContextService.getProjectTypesForClass(controllerClass);
    var allowProjectContributors = projectContextService.getContributorsAllowedForClass(controllerClass);

    try {
      buildProjectUpdateContext(
          detail,
          user,
          statusCheck,
          permissionCheck,
          allowedProjectTypes,
          allowProjectContributors
      );
      return true;
    } catch (AccessDeniedException exception) {
      return false;
    }
  }

  public ProjectUpdateContext buildProjectUpdateContext(ProjectDetail detail,
                                                        AuthenticatedUserAccount user,
                                                        Set<ProjectStatus> statusCheck,
                                                        Set<ProjectPermission> permissionCheck,
                                                        Set<ProjectType> allowedProjectTypes,
                                                        boolean allowProjectContributors) {
    var project = detail.getProject();

    if (projectUpdateService.isUpdateInProgress(project)) {
      throw new AccessDeniedException(
          String.format(
              "Project id %s already has an update in progress",
              project.getId()
          )
      );
    }

    var projectContext = projectContextService.buildProjectContext(
        detail,
        user,
        statusCheck,
        permissionCheck,
        allowedProjectTypes,
        allowProjectContributors
    );

    return new ProjectUpdateContext(
        projectContext.getProjectDetails(),
        projectContext.getProjectPermissions(),
        projectContext.getUserAccount(),
        projectContext.getUserToProjectRelationships()
    );
  }

  public ProjectDetail getProjectDetailsOrError(Integer projectId, ProjectDetailVersionType projectDetailVersionType) {
    return projectContextService.getProjectDetailsOrError(projectId, projectDetailVersionType);
  }
}
