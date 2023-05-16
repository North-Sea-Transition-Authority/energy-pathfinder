package uk.co.ogauthority.pathfinder.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import uk.co.ogauthority.pathfinder.externalapi.ProjectDtoController;

@Configuration
@EnableWebSecurity
@Order(1)
class ExternalApiWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final String preSharedKey;
  private final ExternalApiAuthenticationEntryPoint externalApiAuthenticationEntryPoint;
  private static final String ENERGY_PORTAL_API_PATH_MATCHER =
      ProjectDtoController.ENERGY_PORTAL_API_BASE_PATH + "/**";

  @Autowired
  public ExternalApiWebSecurityConfiguration(ExternalApiConfiguration externalApiConfiguration,
                                             ExternalApiAuthenticationEntryPoint externalApiAuthenticationEntryPoint) {
    this.preSharedKey = externalApiConfiguration.getPreSharedKey();
    this.externalApiAuthenticationEntryPoint = externalApiAuthenticationEntryPoint;
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    var filter = new PreSharedKeyAuthFilter("Authorization");
    filter.setAuthenticationManager(new ExternalApiSecurityAuthManager(preSharedKey));
    httpSecurity
        .mvcMatcher(ENERGY_PORTAL_API_PATH_MATCHER)
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(filter).authorizeRequests()
          .anyRequest().authenticated()
        .and()
        .exceptionHandling()
          .authenticationEntryPoint(externalApiAuthenticationEntryPoint);

    httpSecurity.csrf().ignoringAntMatchers(ENERGY_PORTAL_API_PATH_MATCHER);
  }
}
