package uk.co.ogauthority.pathfinder.mvc.argumentresolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContext;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;
import uk.co.ogauthority.pathfinder.util.ArgumentResolverUtil;

@Component
public class ProjectAssessmentContextArgumentResolver implements HandlerMethodArgumentResolver {

  private final ProjectContextService projectContextService;
  private final ProjectAssessmentContextService projectAssessmentContextService;

  @Autowired
  public ProjectAssessmentContextArgumentResolver(ProjectContextService projectContextService,
                                                  ProjectAssessmentContextService projectAssessmentContextService) {
    this.projectContextService = projectContextService;
    this.projectAssessmentContextService = projectAssessmentContextService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(ProjectAssessmentContext.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) throws Exception {
    var user = ArgumentResolverUtil.getAuthenticatedUser();
    var projectId = ArgumentResolverUtil.resolveIdFromRequest(webRequest, ArgumentResolverUtil.PROJECT_ID_PARAM);
    var detail = projectContextService.getProjectDetailsOrError(projectId);
    var statusCheck = ArgumentResolverUtil.getProjectStatusCheck(parameter);
    var permissionCheck = ArgumentResolverUtil.getProjectFormPagePermissionCheck(parameter);

    return projectAssessmentContextService.buildProjectAssessmentContext(
        detail,
        user,
        statusCheck,
        permissionCheck
    );

  }
}
