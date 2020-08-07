package uk.co.ogauthority.pathfinder.mvc.argumentresolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.util.ArgumentResolverUtil;

@Component
public class ProjectContextArgumentResolver implements HandlerMethodArgumentResolver {

  private final ProjectContextService projectContextService;

  @Autowired
  public ProjectContextArgumentResolver(ProjectContextService projectContextService) {
    this.projectContextService = projectContextService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(ProjectContext.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    var user = ArgumentResolverUtil.getAuthenticatedUser();
    var projectId = ArgumentResolverUtil.resolveIdFromRequest(webRequest, ArgumentResolverUtil.PROJECT_ID_PARAM);
    var detail = projectContextService.getProjectDetailsOrError(projectId);
    var statusCheck = ArgumentResolverUtil.getProjectStatusCheck(parameter);

    if (!projectContextService.userIsInProjectTeamOrRegulator(detail, user)) {
      throw new AccessDeniedException(String.format("User with wua: %d does not have permission to view projectDetail with id: %d", user.getWuaId(), projectId));
    }

    if (!projectContextService.projectStatusMatches(detail, statusCheck)) {
      throw new AccessDeniedException(String.format("Project with status: %s does not match required status: %s", detail.getStatus().getDisplayName(), statusCheck.getDisplayName()));
    }

    //TODO condsider: call into method in projectContextService which does permission check then calls lambda to get context if
    //permissions check passes else throw exception? Similar to ControllerHelperService???
    return projectContextService.getProjectContext(detail, user);

  }
}
