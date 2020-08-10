package uk.co.ogauthority.pathfinder.service.project.projectcontext;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;

@Service
public class ProjectContextService {

  private final ProjectService projectService;
  private final ProjectOperatorService projectOperatorService;

  @Autowired
  public ProjectContextService(ProjectService projectService,
                               ProjectOperatorService projectOperatorService) {
    this.projectService = projectService;
    this.projectOperatorService = projectOperatorService;
  }

  /**
   * Do some preliminary access checks and return the project context if the checks pass.
   * @param detail Detail to build the context for.
   * @param user User accessing the project.
   * @param statusCheck What status the project should be in.
   *     (accessed via {@link uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck} annotation).
   * @return the project context if checks pass else throw AccessDeniedException
   */
  public ProjectContext buildProjectContext(ProjectDetail detail,
                                            AuthenticatedUserAccount user,
                                            ProjectStatus statusCheck) {
    if (!projectOperatorService.isUserInProjectTeamOrRegulator(detail, user)) {
      throw new AccessDeniedException(
          String.format(
              "User with wua: %d does not have permission to view projectDetail with id: %d",
              user.getWuaId(),
              detail.getProject().getId())
      );
    }

    if (!projectStatusMatches(detail, statusCheck)) {
      throw new AccessDeniedException(
          String.format(
              "Project with status: %s does not match required status: %s",
              detail.getStatus().getDisplayName(),
              statusCheck.getDisplayName())
      );
    }

    return getProjectContext(detail, user);
  }

  public ProjectContext getProjectContext(ProjectDetail detail, AuthenticatedUserAccount user) {
    Set<ProjectPermission> projectPermissions = new java.util.HashSet<>();

    if (detail.getStatus().equals(ProjectStatus.DRAFT)) {
      projectPermissions.add(ProjectPermission.VIEW_TASK_LIST);
    }

    if (user.hasPrivilege(UserPrivilege.PATHFINDER_PROJECT_CREATE)) {
      projectPermissions.add(ProjectPermission.EDIT);
      projectPermissions.add(ProjectPermission.SUBMIT);
    }

    //TODO PAT-82 Operator view privilege

    return new ProjectContext(
        detail,
        projectPermissions,
        user
    );
  }

  public ProjectDetail getProjectDetailsOrError(Integer projectId) {
    return projectService.getLatestDetail(projectId).orElseThrow(() -> new PathfinderEntityNotFoundException(
        String.format("Unable to find project detail for project id  %d", projectId))
    );
  }

  public boolean projectStatusMatches(ProjectDetail detail,
                                      ProjectStatus status) {
    return detail.getStatus().equals(status);
  }
}
