package uk.co.ogauthority.pathfinder.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.UserSession;
import uk.co.ogauthority.pathfinder.service.FoxUrlService;
import uk.co.ogauthority.pathfinder.service.UserSessionService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.navigation.TopNavigationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Import(AbstractControllerTest.TestConfig.class)
public abstract class AbstractControllerTest {

  protected MockMvc mockMvc;

  @Autowired
  protected WebApplicationContext context;

  @MockBean
  private FoxUrlService foxUrlService;

  @MockBean
  protected TeamService teamService;

  @MockBean
  protected UserSessionService userSessionService;

  @MockBean
  protected ServiceProperties serviceProperties;

  @MockBean
  protected TopNavigationService topNavigationService;

  @MockBean
  protected ProjectContextService projectContextService;

  @Before
  public void abstractControllerTestSetup() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(SecurityMockMvcConfigurers.springSecurity())
        .build();

    when(foxUrlService.getFoxLoginUrl()).thenReturn("test-login-url");
    when(foxUrlService.getFoxLogoutUrl()).thenReturn("test-logout-url");
    when(foxUrlService.getFoxRegistrationUrl()).thenReturn("test-registration-url");

    when(userSessionService.getAndValidateSession(any(), anyBoolean())).thenReturn(Optional.of(new UserSession()));

  }

  @TestConfiguration
  public static class TestConfig {

    @Bean
    public SystemAccessService systemAreaAccessService() {
      return new SystemAccessService();
    }

    @Bean
    public BreadcrumbService breadcrumbService() { return new BreadcrumbService(); }

    @Bean("messageSource")
    public MessageSource messageSource() {
      ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
      messageSource.setBasename("messages");
      messageSource.setDefaultEncoding("UTF-8");
      return messageSource;
    }

    @Bean
    public ControllerHelperService controllerHelperService() { return new ControllerHelperService(messageSource()); }
  }

}
