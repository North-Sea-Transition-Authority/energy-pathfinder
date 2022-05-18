package uk.co.ogauthority.pathfinder.service.project.projectcontext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectDetailVersionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsCommonService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Service
public class ProjectContextService {

  private final ProjectService projectService;
  private final ProjectOperatorService projectOperatorService;
  private final ProjectContributorsCommonService projectContributorsCommonService;
  private final TeamService teamService;

  @Autowired
  public ProjectContextService(ProjectService projectService,
                               ProjectOperatorService projectOperatorService,
                               ProjectContributorsCommonService projectContributorsCommonService,
                               TeamService teamService) {
    this.projectService = projectService;
    this.projectOperatorService = projectOperatorService;
    this.projectContributorsCommonService = projectContributorsCommonService;
    this.teamService = teamService;
  }

  public boolean canBuildContext(ProjectDetail detail,
                                 AuthenticatedUserAccount user,
                                 Class<?> controllerClass) {
    var statusCheck = getProjectStatusesForClass(controllerClass);
    var permissionCheck = getProjectPermissionsForClass(controllerClass);
    final var allowedProjectTypes = getProjectTypesForClass(controllerClass);
    var allowProjectContributors = getContributorsAllowedForClass(controllerClass);

    try {
      buildProjectContext(detail, user, statusCheck, permissionCheck, allowedProjectTypes, allowProjectContributors);
      return true;
    } catch (AccessDeniedException exception) {
      return false;
    }
  }

  /**
   * Do some preliminary access checks and return the project context if the checks pass.
   *
   * @param detail Detail to build the context for.
   * @param user User accessing the project.
   * @param statusCheck What status the project should be in.
   *                    (accessed via {@link uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck})
   * @param permissionCheck what permissions the user should have to view the detail
   *                        (accessed via {@link uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck})
   * @param allowedProjectTypes the set of allow project types
   *                            (accessed via {@link uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck})
   * @return the project context if checks pass else throw AccessDeniedException
   */
  public ProjectContext buildProjectContext(ProjectDetail detail,
                                            AuthenticatedUserAccount user,
                                            Set<ProjectStatus> statusCheck,
                                            Set<ProjectPermission> permissionCheck,
                                            Set<ProjectType> allowedProjectTypes,
                                            boolean allowProjectContributors) {
    var isOperatorOrRegulator = projectOperatorService.isUserInProjectTeamOrRegulator(detail, user);
    var canAccessAsContributor = allowProjectContributors && userBelongsAsContributor(user, detail);
    if (!isOperatorOrRegulator && !canAccessAsContributor) {
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
              (detail.getStatus() != null) ? detail.getStatus().getDisplayName() : "null",
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

    if (!projectTypeMatches(detail, allowedProjectTypes)) {
      throw new AccessDeniedException(
          String.format(
              "Project with type: %s does not match required types: %s",
              (detail.getProjectType() != null) ? detail.getProjectType() : "null",
              allowedProjectTypes.stream().map(ProjectType::name).collect(Collectors.joining(","))
          )
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

  public ProjectDetail getProjectDetailsOrError(Integer projectId, ProjectDetailVersionType projectDetailVersionType) {
    if (projectDetailVersionType.equals(ProjectDetailVersionType.CURRENT_VERSION)) {
      return projectService.getLatestDetailOrError(projectId);
    } else if (projectDetailVersionType.equals(ProjectDetailVersionType.LATEST_SUBMITTED_VERSION)) {
      return projectService.getLatestSubmittedDetailOrError(projectId);
    } else {
      throw new IllegalStateException(String.format("Unsupported ProjectDetailVersionType %s", projectDetailVersionType));
    }
  }

  private boolean projectStatusMatches(ProjectDetail detail,
                                      Set<ProjectStatus> allowedProjectStatuses) {
    final var projectStatus = detail.getStatus();
    return projectStatus != null && allowedProjectStatuses.contains(projectStatus);
  }

  private boolean projectTypeMatches(ProjectDetail projectDetail,
                                     Set<ProjectType> allowedProjectTypes) {
    final var projectType = projectDetail.getProjectType();
    return projectType != null && allowedProjectTypes.contains(projectType);
  }

  public Set<ProjectPermission> getUserProjectPermissions(AuthenticatedUserAccount user) {
    return user.getUserPrivileges().stream()
        .flatMap(userPrivilege -> Arrays.stream(ProjectPermission.values()).filter(p -> p.hasPrivilege(userPrivilege)))
        .collect(Collectors.toSet());
  }

  public Set<ProjectStatus> getProjectStatusesForClass(Class<?> clazz) {
    return Arrays.stream(clazz.getAnnotations())
        .filter(annotation -> annotation.annotationType().equals(ProjectStatusCheck.class))
        .map(annotation -> Arrays.stream(((ProjectStatusCheck) annotation).status()).collect(Collectors.toSet()))
        .findFirst()
        .orElse(Set.of());
  }

  public Set<ProjectPermission> getProjectPermissionsForClass(Class<?> clazz) {
    return Arrays.stream(clazz.getAnnotations())
        .filter(annotation -> annotation.annotationType().equals(ProjectFormPagePermissionCheck.class))
        .map(annotation -> Arrays.stream(((ProjectFormPagePermissionCheck) annotation).permissions()).collect(Collectors.toSet()))
        .findFirst()
        .orElse(Set.of());
  }

  public Set<ProjectType> getProjectTypesForClass(Class<?> clazz) {
    return Arrays.stream(clazz.getAnnotations())
        .filter(annotation -> annotation.annotationType().equals(ProjectTypeCheck.class))
        .map(annotation -> Arrays.stream(((ProjectTypeCheck) annotation).types()).collect(Collectors.toSet()))
        .findFirst()
        .orElse(Set.of());
  }

  public boolean getContributorsAllowedForClass(Class<?> clazz) {
    return Arrays.stream(clazz.getAnnotations())
        .anyMatch(annotation -> annotation.annotationType().equals(AllowProjectContributorAccess.class));
  }

  private boolean userBelongsAsContributor(AuthenticatedUserAccount user, ProjectDetail detail) {
    var projectContributorsIdList = projectContributorsCommonService.getProjectContributorsForDetail(detail)
        .stream()
        .map(projectContributor -> projectContributor.getContributionOrganisationGroup().getOrgGrpId())
        .collect(Collectors.toList());

    var userOrganisationsIdList = teamService.getOrganisationTeamsPersonIsMemberOf(user.getLinkedPerson())
        .stream()
        .map(organisationTeam -> organisationTeam.getPortalOrganisationGroup().getOrgGrpId())
        .collect(Collectors.toList());

    return projectContributorsIdList
        .stream()
        .anyMatch(userOrganisationsIdList::contains);
  }
}
