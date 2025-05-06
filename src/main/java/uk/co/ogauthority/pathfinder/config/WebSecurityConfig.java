package uk.co.ogauthority.pathfinder.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import uk.co.ogauthority.pathfinder.auth.FoxLoginCallbackFilter;
import uk.co.ogauthority.pathfinder.auth.FoxSessionFilter;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.service.FoxUrlService;
import uk.co.ogauthority.pathfinder.service.UserSessionService;

@Configuration
public class WebSecurityConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

  private final UserSessionService userSessionService;
  private final FoxLoginCallbackFilter foxLoginCallbackFilter;
  private final FoxUrlService foxUrlService;
  private final SystemAccessService systemAccessService;

  @Autowired
  public WebSecurityConfig(UserSessionService userSessionService,
                           FoxLoginCallbackFilter foxLoginCallbackFilter,
                           FoxUrlService foxUrlService,
                           SystemAccessService systemAccessService) {
    this.userSessionService = userSessionService;
    this.foxLoginCallbackFilter = foxLoginCallbackFilter;
    this.foxUrlService = foxUrlService;
    this.systemAccessService = systemAccessService;
  }

  @Bean
  @Order(2)
  SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
            .requestMatchers("/work-area")
            .hasAnyAuthority(systemAccessService.getWorkAreaGrantedAuthorities())

            .requestMatchers("/manage-teams", "/team-management", "/team-management/**")
            .hasAnyAuthority(systemAccessService.getViewTeamGrantedAuthorities())

            .requestMatchers("/infrastructure/start-project", "/project-operator-select")
            .hasAnyAuthority(systemAccessService.getCreateProjectGrantedAuthorities())

            .requestMatchers("/api/**")
            .hasAnyAuthority(systemAccessService.getWorkAreaGrantedAuthorities())

            .requestMatchers("/quarterly-statistics/**")
            .hasAnyAuthority(systemAccessService.getQuarterlyStatisticsGrantedAuthorities())

            .requestMatchers("/communications/**")
            .hasAnyAuthority(systemAccessService.getCommunicationsGrantedAuthorities())

            .requestMatchers(
                "/actuator/health",
                "/session-info",
                "/notify/callback",
                "/contact",
                "/subscribe",
                "/subscription/**",
                "/unsubscribe/**",
                "/accessibility-statement",
                "/assets/**",
                "/error",
                "/public/file",
                "/pathfinder-api/**"
            )
            .permitAll()

            .anyRequest()
            .authenticated()
        )
      .csrf(csrf -> csrf
          .ignoringRequestMatchers(
              "/notify/callback",
              "/subscribe",
              "/unsubscribe/**",
              "/subscription/**",
              "/analytics/collect"
          )
      )
      .exceptionHandling(exceptionHandling -> exceptionHandling
          .authenticationEntryPoint((request, response, authException) -> {
            LOGGER.warn(
                "Unauthenticated user attempted to access authenticated resource: '{}' Redirecting to login screen...",
                request.getRequestURI()
            );
            response.sendRedirect(foxUrlService.getFoxLoginUrl());
          })
      )
        .addFilterBefore(
            new FoxSessionFilter(userSessionService, () -> httpSecurity.getSharedObject(SecurityContextRepository.class)),
            RequestCacheAwareFilter.class
        )
        // The FoxLoginCallbackFilter must be hit before the FoxSessionFilter, otherwise the saved request is wiped
        // when the session is cleared
        .addFilterBefore(foxLoginCallbackFilter, FoxSessionFilter.class);

    return httpSecurity.build();
  }
}
