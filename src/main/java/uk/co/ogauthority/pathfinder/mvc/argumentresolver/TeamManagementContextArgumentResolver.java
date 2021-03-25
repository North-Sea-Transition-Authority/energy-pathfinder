package uk.co.ogauthority.pathfinder.mvc.argumentresolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.co.ogauthority.pathfinder.service.team.teammanagementcontext.TeamManagementContext;
import uk.co.ogauthority.pathfinder.service.team.teammanagementcontext.TeamManagementContextService;
import uk.co.ogauthority.pathfinder.util.ArgumentResolverUtil;

@Component
public class TeamManagementContextArgumentResolver implements HandlerMethodArgumentResolver {

  private final TeamManagementContextService teamManagementContextService;

  @Autowired
  public TeamManagementContextArgumentResolver(TeamManagementContextService teamManagementContextService) {
    this.teamManagementContextService = teamManagementContextService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(TeamManagementContext.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) {
    var user = ArgumentResolverUtil.getAuthenticatedUser();
    var permissionCheck = ArgumentResolverUtil.getTeamManagementPermissionCheck(parameter);

    Integer resourceId = null;

    if (ArgumentResolverUtil.doesRequestContainParam(webRequest, ArgumentResolverUtil.RESOURCE_ID_PARAM)) {
      resourceId = ArgumentResolverUtil.resolveIdFromRequest(webRequest, ArgumentResolverUtil.RESOURCE_ID_PARAM);
    }

    return teamManagementContextService.buildTeamManagementContext(permissionCheck, resourceId, user);
  }
}
