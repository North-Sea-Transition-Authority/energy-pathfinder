package uk.co.ogauthority.pathfinder.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.co.ogauthority.pathfinder.mvc.ResponseBufferSizeHandlerInterceptor;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.AuthenticatedUserAccountArgumentResolver;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ProjectContextArgumentResolver;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final ProjectContextArgumentResolver projectContextArgumentResolver;

  @Autowired
  public WebMvcConfig(ProjectContextArgumentResolver projectContextArgumentResolver) {
    this.projectContextArgumentResolver = projectContextArgumentResolver;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new AuthenticatedUserAccountArgumentResolver());
    resolvers.add(new ValidationTypeArgumentResolver());
    resolvers.add(projectContextArgumentResolver);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new ResponseBufferSizeHandlerInterceptor())
        .excludePathPatterns("/assets/**");
  }

}
