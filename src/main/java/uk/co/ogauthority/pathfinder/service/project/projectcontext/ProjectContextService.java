package uk.co.ogauthority.pathfinder.service.project.projectcontext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
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
   *
   * @param detail          Detail to build the context for.
   * @param user            User accessing the project.
   * @param statusCheck     What status the project should be in.
   *                        (accessed via {@link uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck} annotation).
   * @param permissionCheck what permissions the user should have to view the detail
   *                        (accessed via
   *                        {@link uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck} annotation).
   * @return the project context if checks pass else throw AccessDeniedException
   */
  public ProjectContext buildProjectContext(ProjectDetail detail,
                                            AuthenticatedUserAccount user,
                                            Set<ProjectStatus> statusCheck,
                                            Set<ProjectPermission> permissionCheck) {
    if (!projectOperatorService.isUserInProjectTeamOrRegulator(detail, user)) {
      throw new AccessDeniedException(
          String.format(
              "User with wua: %d does not have access to view projectDetail with id: %d",
              user.getWuaId(),
              detail.getProject().getId())
      );
    }

    if (!projectStatusMatches(detail, statusCheck)) {
      throw new AccessDeniedException(
          String.format(
              "Project with status: %s does not match required statuses: %s",
              detail.getStatus().getDisplayName(),
              statusCheck.stream().map(ProjectStatus::getDisplayName).collect(Collectors.joining(",")))
      );
    }

    var userPermissions = getUserProjectPermissions(user);


    if (userPermissions.stream().noneMatch(permissionCheck::contains)) {
      throw new AccessDeniedException(
          String.format(
              "User does not have the required permissions: %s",
              permissionCheck.stream().map(Enum::name).collect(Collectors.joining(",")))
      );
    }

    return getProjectContext(detail, user, userPermissions);
  }

  public ProjectContext getProjectContext(ProjectDetail detail,
                                          AuthenticatedUserAccount user,
                                          Set<ProjectPermission> projectPermissions) {
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
                                      Set<ProjectStatus> status) {
    return status.contains(detail.getStatus());
  }

  public Set<ProjectPermission> getUserProjectPermissions(AuthenticatedUserAccount user) {
    return user.getUserPrivileges().stream()
        .flatMap(userPrivilege -> Arrays.stream(ProjectPermission.values()).filter(p -> p.hasPrivilege(userPrivilege)))
        .collect(Collectors.toSet());
  }
}
