package uk.co.ogauthority.pathfinder.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.entity.UserSession;
import uk.co.ogauthority.pathfinder.service.FoxUrlService;
import uk.co.ogauthority.pathfinder.service.UserSessionService;
import uk.co.ogauthority.pathfinder.service.navigation.TopNavigationService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.team.teammanagementcontext.TeamManagementContextService;

@Import({AbstractControllerTest.TestConfig.class, TeamManagementContextAbstractControllerTest.TestConfig.class})
public abstract class TeamManagementContextAbstractControllerTest {

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

  @MockBean
  protected ProjectService projectService;

  @MockBean
  protected ProjectOperatorService projectOperatorService;

  @MockBean
  protected ProjectAssessmentContextService projectAssessmentContextService;

  @MockBean
  protected OperatorProjectUpdateContextService operatorProjectUpdateContextService;

  @MockBean
  protected RegulatorProjectUpdateContextService regulatorProjectUpdateContextService;

  @Autowired
  protected TeamManagementContextService teamManagementContextService;

  @Before
  public void  projectContextAbstractControllerTestSetUp() {
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

    // for controllers using session scoped attributes
    @Bean
    public CustomScopeConfigurer customScopeConfigurer() {
      CustomScopeConfigurer configurer = new CustomScopeConfigurer();
      configurer.addScope("session", new SimpleThreadScope());
      return configurer;
    }
  }
}
