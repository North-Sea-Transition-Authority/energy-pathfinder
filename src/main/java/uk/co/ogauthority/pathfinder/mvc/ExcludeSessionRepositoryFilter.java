package uk.co.ogauthority.pathfinder.mvc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for excluding URL patterns from Spring Session. This prevents hits to to database for unnecessary session
 * lookups. See https://github.com/spring-projects/spring-session/issues/244#issuecomment-296605144
 */
@Component
@Order(Integer.MIN_VALUE)
public class ExcludeSessionRepositoryFilter extends OncePerRequestFilter {

  private static final String ATTRIBUTE_NAME = SessionRepositoryFilter.class.getName().concat(ALREADY_FILTERED_SUFFIX);

  @Override
  protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                                  FilterChain filterChain) throws ServletException, IOException {
    var requestUri = httpRequest.getRequestURI();
    if (requestUri.contains("/assets/")) {
      httpRequest.setAttribute(ATTRIBUTE_NAME, Boolean.TRUE);
    }

    filterChain.doFilter(httpRequest, httpResponse);
  }
}
