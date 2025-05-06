package uk.co.ogauthority.pathfinder.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsService;
import uk.co.ogauthority.pathfinder.analytics.EnableAnalyticsConfiguration;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.config.WebSecurityConfig;
import uk.co.ogauthority.pathfinder.model.entity.UserSession;
import uk.co.ogauthority.pathfinder.mvc.footer.FooterService;
import uk.co.ogauthority.pathfinder.service.FoxUrlService;
import uk.co.ogauthority.pathfinder.service.UserSessionService;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyService;
import uk.co.ogauthority.pathfinder.service.navigation.TopNavigationService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsCommonService;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.team.teammanagementcontext.TeamManagementContextService;

@EnableAnalyticsConfiguration
@Import({
    AbstractControllerTest.TestConfig.class,
    ProjectContextAbstractControllerTest.TestConfig.class,
    WebSecurityConfig.class
})
public abstract class ProjectContextAbstractControllerTest {

  protected MockMvc mockMvc;

  @Autowired
  protected WebApplicationContext context;

  @MockitoBean
  private FoxUrlService foxUrlService;

  @MockitoBean
  protected TeamService teamService;

  @MockitoBean
  protected UserSessionService userSessionService;

  @MockitoBean
  protected ServiceProperties serviceProperties;

  @MockitoBean
  protected TopNavigationService topNavigationService;

  @Autowired
  protected ProjectContextService projectContextService;

  @MockitoBean
  protected ProjectService projectService;

  @MockitoBean
  protected ProjectOperatorService projectOperatorService;

  @MockitoBean
  protected ProjectAssessmentContextService projectAssessmentContextService;

  @MockitoBean
  protected OperatorProjectUpdateContextService operatorProjectUpdateContextService;

  @MockitoBean
  protected RegulatorProjectUpdateContextService regulatorProjectUpdateContextService;

  @MockitoBean
  protected TeamManagementContextService teamManagementContextService;

  @MockitoBean
  protected CommunicationJourneyService communicationJourneyService;

  @MockitoBean
  protected FooterService footerService;

  @MockitoBean
  protected AnalyticsService analyticsService;

  @MockitoBean
  protected ProjectContributorsCommonService projectContributorsCommonService;

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

    when(serviceProperties.getServiceName()).thenReturn("service-name");
    when(serviceProperties.getCustomerMnemonic()).thenReturn("customer-mnemonic");
    when(serviceProperties.getCustomerName()).thenReturn("customer-name");
    when(serviceProperties.getStackTraceEnabled()).thenReturn(false);

    doCallRealMethod().when(footerService).addFooterUrlsToModelAndView(any());
    doCallRealMethod().when(footerService).addFooterUrlsToModel(any());

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
