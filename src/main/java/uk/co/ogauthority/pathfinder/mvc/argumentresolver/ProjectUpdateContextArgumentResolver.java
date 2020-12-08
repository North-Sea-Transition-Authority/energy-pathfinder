package uk.co.ogauthority.pathfinder.mvc.argumentresolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContext;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.util.ArgumentResolverUtil;

@Component
public class ProjectUpdateContextArgumentResolver implements HandlerMethodArgumentResolver {

  private final ProjectUpdateContextService projectUpdateContextService;

  @Autowired
  public ProjectUpdateContextArgumentResolver(
      ProjectUpdateContextService projectUpdateContextService) {
    this.projectUpdateContextService = projectUpdateContextService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(ProjectUpdateContext.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) throws Exception {
    var user = ArgumentResolverUtil.getAuthenticatedUser();
    var projectId = ArgumentResolverUtil.resolveIdFromRequest(webRequest, ArgumentResolverUtil.PROJECT_ID_PARAM);
    var detail = projectUpdateContextService.getProjectDetailsOrError(projectId);
    var statusCheck = ArgumentResolverUtil.getProjectStatusCheck(parameter);
    var permissionCheck = ArgumentResolverUtil.getProjectFormPagePermissionCheck(parameter);

    return projectUpdateContextService.buildProjectUpdateContext(
        detail,
        user,
        statusCheck,
        permissionCheck
    );
  }
}
