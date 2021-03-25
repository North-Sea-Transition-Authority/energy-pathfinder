package uk.co.ogauthority.pathfinder.config;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import uk.co.ogauthority.pathfinder.mvc.ResponseBufferSizeHandlerInterceptor;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.AuthenticatedUserAccountArgumentResolver;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.CommunicationContextArgumentResolver;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.OperatorProjectUpdateContextArgumentResolver;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ProjectAssessmentContextArgumentResolver;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ProjectContextArgumentResolver;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.RegulatorProjectUpdateContextArgumentResolver;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.TeamManagementContextArgumentResolver;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final ProjectContextArgumentResolver projectContextArgumentResolver;
  private final ProjectAssessmentContextArgumentResolver projectAssessmentContextArgumentResolver;
  private final OperatorProjectUpdateContextArgumentResolver operatorProjectUpdateContextArgumentResolver;
  private final RegulatorProjectUpdateContextArgumentResolver regulatorProjectUpdateContextArgumentResolver;
  private final TeamManagementContextArgumentResolver teamManagementContextArgumentResolver;
  private final CommunicationContextArgumentResolver communicationContextArgumentResolver;

  @Autowired
  public WebMvcConfig(ProjectContextArgumentResolver projectContextArgumentResolver,
                      ProjectAssessmentContextArgumentResolver projectAssessmentContextArgumentResolver,
                      OperatorProjectUpdateContextArgumentResolver operatorProjectUpdateContextArgumentResolver,
                      RegulatorProjectUpdateContextArgumentResolver regulatorProjectUpdateContextArgumentResolver,
                      TeamManagementContextArgumentResolver teamManagementContextArgumentResolver,
                      CommunicationContextArgumentResolver communicationContextArgumentResolver) {
    this.projectContextArgumentResolver = projectContextArgumentResolver;
    this.projectAssessmentContextArgumentResolver = projectAssessmentContextArgumentResolver;
    this.operatorProjectUpdateContextArgumentResolver = operatorProjectUpdateContextArgumentResolver;
    this.regulatorProjectUpdateContextArgumentResolver = regulatorProjectUpdateContextArgumentResolver;
    this.teamManagementContextArgumentResolver = teamManagementContextArgumentResolver;
    this.communicationContextArgumentResolver = communicationContextArgumentResolver;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new AuthenticatedUserAccountArgumentResolver());
    resolvers.add(new ValidationTypeArgumentResolver());
    resolvers.add(projectContextArgumentResolver);
    resolvers.add(projectAssessmentContextArgumentResolver);
    resolvers.add(operatorProjectUpdateContextArgumentResolver);
    resolvers.add(regulatorProjectUpdateContextArgumentResolver);
    resolvers.add(teamManagementContextArgumentResolver);
    resolvers.add(communicationContextArgumentResolver);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/assets/**")
        .addResourceLocations("classpath:/public/assets/")
        .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
        .resourceChain(false)
        .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new ResponseBufferSizeHandlerInterceptor())
        .excludePathPatterns("/assets/**");
  }

}
