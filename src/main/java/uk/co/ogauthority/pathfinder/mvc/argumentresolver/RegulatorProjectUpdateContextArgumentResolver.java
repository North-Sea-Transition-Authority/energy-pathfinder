package uk.co.ogauthority.pathfinder.mvc.argumentresolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorProjectUpdateContext;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.util.ArgumentResolverUtil;

@Component
public class RegulatorProjectUpdateContextArgumentResolver implements HandlerMethodArgumentResolver {

  private final RegulatorProjectUpdateContextService regulatorProjectUpdateContextService;

  @Autowired
  public RegulatorProjectUpdateContextArgumentResolver(
      RegulatorProjectUpdateContextService regulatorProjectUpdateContextService) {
    this.regulatorProjectUpdateContextService = regulatorProjectUpdateContextService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(RegulatorProjectUpdateContext.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) throws Exception {
    var user = ArgumentResolverUtil.getAuthenticatedUser();
    var projectId = ArgumentResolverUtil.resolveIdFromRequest(webRequest, ArgumentResolverUtil.PROJECT_ID_PARAM);
    var detail = regulatorProjectUpdateContextService.getProjectDetailsOrError(
        projectId,
        ArgumentResolverUtil.getProjectStatusCheckProjectDetailVersionType(parameter)
    );
    var statusCheck = ArgumentResolverUtil.getProjectStatusCheck(parameter);
    var permissionCheck = ArgumentResolverUtil.getProjectFormPagePermissionCheck(parameter);

    return regulatorProjectUpdateContextService.buildProjectUpdateContext(
        detail,
        user,
        statusCheck,
        permissionCheck
    );
  }
}
