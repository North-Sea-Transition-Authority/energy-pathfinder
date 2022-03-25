package uk.co.ogauthority.pathfinder.mvc;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsService;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsUtils;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;

@Component
public class ProjectRouteInterceptor implements HandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRouteInterceptor.class);

  private final AnalyticsService analyticsService;

  public ProjectRouteInterceptor(AnalyticsService analyticsService) {
    this.analyticsService = analyticsService;
  }

  @Override
  @Async
  public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception ex) throws Exception {

    try {

      // if we're POSTing and triggering some validation, log this to analytics (i.e. full or partial)
      if (Objects.equals(request.getMethod(), "POST")) {

        var resolvedValidationTypes = Stream.of(ValidationType.values())
            .filter(validationType -> validationType.getAnalyticsEventCategory().isPresent())
            .filter(validationType -> request.getParameter(validationType.getButtonText()) != null)
            .collect(Collectors.toList());

        if (resolvedValidationTypes.isEmpty()) {
          return;
        }

        var analyticsClientIdOpt = Arrays.stream(request.getCookies())
            .filter(cookie -> Objects.equals(cookie.getName(), AnalyticsUtils.GA_CLIENT_ID_COOKIE_NAME))
            .map(Cookie::getValue)
            .findFirst();

        // transform "/engedudev1/cw/project/361/infrastructure/collaboration-opportunities/collaboration-opportunity/21/edit"
        // into "/infrastructure/collaboration-opportunities/collaboration-opportunity//edit"
        var endpointIdString = StringUtils.substringAfter(request.getRequestURI().replaceAll("[0-9*]", ""), "project/");

        resolvedValidationTypes.forEach(validationType -> analyticsService
            .sendAnalyticsEvent(
                analyticsClientIdOpt,
                // we will never attempt this for a validation type that doesn't have a category due to the filter above
                validationType.getAnalyticsEventCategory().orElseThrow(),
                Map.of("endpoint", endpointIdString)));

      }

    } catch (Exception e) {
      LOGGER.error("Error trying to intercept Pathfinder project route: {}", request.getRequestURI(), e);
    }

  }

}