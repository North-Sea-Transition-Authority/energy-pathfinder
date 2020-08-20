package uk.co.ogauthority.pathfinder.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import uk.co.ogauthority.pathfinder.auth.FoxLoginCallbackFilter;
import uk.co.ogauthority.pathfinder.auth.FoxSessionFilter;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.service.FoxUrlService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

  private final FoxSessionFilter foxSessionFilter;
  private final FoxLoginCallbackFilter foxLoginCallbackFilter;
  private final FoxUrlService foxUrlService;
  private final SystemAccessService systemAccessService;

  @Autowired
  public WebSecurityConfig(FoxSessionFilter foxSessionFilter,
                           FoxLoginCallbackFilter foxLoginCallbackFilter,
                           FoxUrlService foxUrlService,
                           SystemAccessService systemAccessService) {
    this.foxSessionFilter = foxSessionFilter;
    this.foxLoginCallbackFilter = foxLoginCallbackFilter;
    this.foxUrlService = foxUrlService;
    this.systemAccessService = systemAccessService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
      .authorizeRequests()

        .mvcMatchers("/work-area")
          .hasAnyAuthority(systemAccessService.getWorkAreaGrantedAuthorities())

        .mvcMatchers("/manage-teams", "/team-management", "/team-management/**")
          .hasAnyAuthority(systemAccessService.getViewTeamGrantedAuthorities())

        .mvcMatchers("/start-project", "/project-operator-select")
          .hasAnyAuthority(systemAccessService.getCreateProjectGrantedAuthorities())

        .mvcMatchers("/session-info")
          .permitAll()

        .mvcMatchers("/api/**")
          .hasAnyAuthority(systemAccessService.getWorkAreaGrantedAuthorities())

        .anyRequest()
          .authenticated();

    try {
      // Redirect to FOX for login if the request is unauthenticated.
      http.exceptionHandling()
              .authenticationEntryPoint((request, response, authException) -> {
                LOGGER.warn("Unauthenticated user attempted to access authenticated resource: '{}' Redirecting to login screen...",
                        request.getRequestURI());
                response.sendRedirect(foxUrlService.getFoxLoginUrl());
              });

      http.addFilterBefore(foxSessionFilter, RequestCacheAwareFilter.class);

      // The FoxLoginCallbackFilter must be hit before the FoxSessionFilter, otherwise the saved request is wiped
      // when the session is cleared
      http.addFilterBefore(foxLoginCallbackFilter, FoxSessionFilter.class);

    } catch (Exception e) {
      throw new RuntimeException("Failed to configure HttpSecurity", e);
    }
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/assets/**", "/error");
  }


}
