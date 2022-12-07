package uk.co.ogauthority.pathfinder.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.HandlerMapping;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.communication.CommunicationJourney;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.controller.team.annotation.TeamManagementPermissionCheck;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectDetailVersionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyStage;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.team.teammanagementcontext.TeamManagementPermission;

public class ArgumentResolverUtil {

  public static final String PROJECT_ID_PARAM = "projectId";
  public static final String RESOURCE_ID_PARAM = "resId";
  public static final String COMMUNICATION_ID_PARAM = "communicationId";

  private ArgumentResolverUtil() {
    throw new IllegalStateException("ArgumentResolverUtil is a utility class and should not be instantiated");
  }

  private static Map<String, String> getPathVariables(NativeWebRequest nativeWebRequest) {

    @SuppressWarnings("unchecked")
    var pathVariables = (Map<String, String>) Objects.requireNonNull(
        nativeWebRequest.getNativeRequest(HttpServletRequest.class))
        .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

    return pathVariables;

  }

  /**
   * Return ID parameter in the URI, throw relevant exception if any issues encountered.
   */
  public static Integer resolveIdFromRequest(NativeWebRequest nativeWebRequest, String requestParam) {

    Map<String, String> pathVariables = getPathVariables(nativeWebRequest);

    if (pathVariables.get(requestParam) == null) {
      throw new NullPointerException(String.format("%s not found in URI", requestParam));
    }

    try {
      return Integer.parseInt(pathVariables.get(requestParam));
    } catch (NumberFormatException e) {
      throw new PathfinderEntityNotFoundException(String.format("Requests must have numeric IDs: %s - %s",
          requestParam, pathVariables.get(requestParam)));
    }
  }

  public static boolean doesRequestContainParam(NativeWebRequest nativeWebRequest, String requestParam) {
    Map<String, String> pathVariables = getPathVariables(nativeWebRequest);
    return (pathVariables.get(requestParam) != null);
  }

  public static AuthenticatedUserAccount getAuthenticatedUser() {
    return SecurityUtil.getAuthenticatedUserFromSecurityContext()
        .orElseThrow(
            () -> new RuntimeException("Failed to get AuthenticatedUserAccount from current authentication context"));
  }

  /**
   * Get method level status check or default to controller level if none specified.
   */
  public static Set<ProjectStatus> getProjectStatusCheck(MethodParameter methodParameter) {

    var methodLevelPermissions = Optional.ofNullable(
        methodParameter.getMethodAnnotation(ProjectStatusCheck.class))
        .map(p -> Arrays.stream(p.status()).collect(Collectors.toSet()))
        .orElse(Set.of());

    if (!methodLevelPermissions.isEmpty()) {
      return methodLevelPermissions;
    }

    return Optional.ofNullable(
        methodParameter.getContainingClass().getAnnotation(ProjectStatusCheck.class))
        .map(p -> Arrays.stream(p.status()).collect(Collectors.toSet()))
        .orElse(Set.of());

  }

  public static ProjectDetailVersionType getProjectStatusCheckProjectDetailVersionType(MethodParameter methodParameter) {
    var methodLevelProjectDetailVersionType = Optional.ofNullable(
        methodParameter.getMethodAnnotation(ProjectStatusCheck.class))
        .map(ProjectStatusCheck::projectDetailVersionType);

    return methodLevelProjectDetailVersionType.orElseGet(() -> Optional.ofNullable(
        methodParameter.getContainingClass().getAnnotation(ProjectStatusCheck.class))
        .map(ProjectStatusCheck::projectDetailVersionType)
        .orElseThrow(() -> new RuntimeException(
            String.format(
                "Unable to find ProjectStatusCheck annotation while calling %s within %s",
                methodParameter.toString(),
                methodParameter.getContainingClass().getName()
            ))
        ));
  }


  public static Set<ProjectPermission> getProjectFormPagePermissionCheck(MethodParameter methodParameter) {
    var methodLevelPermissions = Optional.ofNullable(
        methodParameter.getMethodAnnotation(ProjectFormPagePermissionCheck.class))
        .map(p -> Arrays.stream(p.permissions()).collect(Collectors.toSet()))
        .orElse(Set.of());

    if (!methodLevelPermissions.isEmpty()) {
      return methodLevelPermissions;
    }

    return Optional.ofNullable(
        methodParameter.getContainingClass().getAnnotation(ProjectFormPagePermissionCheck.class))
        .map(p -> Arrays.stream(p.permissions()).collect(Collectors.toSet()))
        .orElse(Set.of());
  }

  public static Set<TeamManagementPermission> getTeamManagementPermissionCheck(MethodParameter methodParameter) {
    var methodLevelPermissions = Optional.ofNullable(
        methodParameter.getMethodAnnotation(TeamManagementPermissionCheck.class))
        .map(permissionCheck -> Arrays.stream(permissionCheck.permissions()).collect(Collectors.toSet()))
        .orElse(Set.of());

    if (!methodLevelPermissions.isEmpty()) {
      return methodLevelPermissions;
    }

    return Optional.ofNullable(
        Objects.requireNonNull(methodParameter.getMethod()).getAnnotation(TeamManagementPermissionCheck.class))
        .map(permissionCheck -> Arrays.stream(permissionCheck.permissions()).collect(Collectors.toSet()))
        .orElse(Set.of());
  }

  public static CommunicationJourneyStage getCommunicationJourneyStage(MethodParameter methodParameter) {
    var methodLevelProjectDetailVersionType = Optional.ofNullable(
        methodParameter.getMethodAnnotation(CommunicationJourney.class))
        .map(CommunicationJourney::journeyStage);

    return methodLevelProjectDetailVersionType.orElseGet(() -> Optional.ofNullable(
        methodParameter.getContainingClass().getAnnotation(CommunicationJourney.class))
        .map(CommunicationJourney::journeyStage)
        .orElseThrow(() -> new RuntimeException(
            String.format(
                "Unable to find CommunicationJourney annotation while calling %s within %s",
                methodParameter.toString(),
                methodParameter.getContainingClass().getName()
            ))
        ));
  }

  public static Set<ProjectType> getProjectTypesCheck(MethodParameter methodParameter) {

    var methodLevelPermissions = Optional.ofNullable(
        methodParameter.getMethodAnnotation(ProjectTypeCheck.class)
    )
        .map(p -> Arrays.stream(p.types()).collect(Collectors.toSet()))
        .orElse(Set.of());

    if (!methodLevelPermissions.isEmpty()) {
      return methodLevelPermissions;
    }

    return Optional.ofNullable(
        methodParameter.getContainingClass().getAnnotation(ProjectTypeCheck.class)
    )
        .map(p -> Arrays.stream(p.types()).collect(Collectors.toSet()))
        .orElse(Set.of());
  }

  public static boolean allowProjectContributors(MethodParameter methodParameter) {
    return Optional.ofNullable(methodParameter.getContainingClass().getAnnotation(AllowProjectContributorAccess.class))
        .isPresent()
        || Optional.ofNullable(methodParameter.getMethodAnnotation(AllowProjectContributorAccess.class)).isPresent();
  }
}
