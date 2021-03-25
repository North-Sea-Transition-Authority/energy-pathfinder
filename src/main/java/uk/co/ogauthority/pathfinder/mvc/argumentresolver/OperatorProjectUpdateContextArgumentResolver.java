package uk.co.ogauthority.pathfinder.mvc.argumentresolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateContext;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.util.ArgumentResolverUtil;

@Component
public class OperatorProjectUpdateContextArgumentResolver implements HandlerMethodArgumentResolver {

  private final OperatorProjectUpdateContextService operatorProjectUpdateContextService;

  @Autowired
  public OperatorProjectUpdateContextArgumentResolver(
      OperatorProjectUpdateContextService operatorProjectUpdateContextService) {
    this.operatorProjectUpdateContextService = operatorProjectUpdateContextService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(OperatorProjectUpdateContext.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) throws Exception {
    var user = ArgumentResolverUtil.getAuthenticatedUser();
    var projectId = ArgumentResolverUtil.resolveIdFromRequest(webRequest, ArgumentResolverUtil.PROJECT_ID_PARAM);
    var detail = operatorProjectUpdateContextService.getProjectDetailsOrError(
        projectId,
        ArgumentResolverUtil.getProjectStatusCheckProjectDetailVersionType(parameter)
    );
    var statusCheck = ArgumentResolverUtil.getProjectStatusCheck(parameter);
    var permissionCheck = ArgumentResolverUtil.getProjectFormPagePermissionCheck(parameter);

    final var allowedProjectTypes = ArgumentResolverUtil.getProjectTypesCheck(parameter);

    return operatorProjectUpdateContextService.buildProjectUpdateContext(
        detail,
        user,
        statusCheck,
        permissionCheck,
        allowedProjectTypes
    );
  }
}
