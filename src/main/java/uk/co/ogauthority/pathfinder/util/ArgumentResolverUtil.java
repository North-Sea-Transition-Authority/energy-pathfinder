package uk.co.ogauthority.pathfinder.util;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.HandlerMapping;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;

public class ArgumentResolverUtil {

  public static final String PROJECT_ID_PARAM = "projectId";

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

  public static AuthenticatedUserAccount getAuthenticatedUser() {
    return SecurityUtil.getAuthenticatedUserFromSecurityContext()
        .orElseThrow(
            () -> new RuntimeException("Failed to get AuthenticatedUserAccount from current authentication context"));
  }

  /**
   * Get method level status check or default to controller level if none specified.
   */
  public static ProjectStatus getProjectStatusCheck(MethodParameter methodParameter) {

    var methodLevelStatus = Optional.ofNullable(methodParameter.getMethodAnnotation(ProjectStatusCheck.class))
        .map(ProjectStatusCheck::status);

    return methodLevelStatus.orElseGet(
        () -> Optional.ofNullable(methodParameter.getContainingClass().getAnnotation(ProjectStatusCheck.class))
            .map(ProjectStatusCheck::status)
            .orElse(null));

  }
}
