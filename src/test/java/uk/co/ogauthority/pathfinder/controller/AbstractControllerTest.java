package uk.co.ogauthority.pathfinder.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsService;
import uk.co.ogauthority.pathfinder.analytics.EnableAnalyticsConfiguration;
import uk.co.ogauthority.pathfinder.config.ExternalApiAuthenticationEntryPoint;
import uk.co.ogauthority.pathfinder.config.ExternalApiConfiguration;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.config.WebSecurityConfig;
import uk.co.ogauthority.pathfinder.config.file.FileUploadProperties;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.UserSession;
import uk.co.ogauthority.pathfinder.mvc.error.ErrorService;
import uk.co.ogauthority.pathfinder.mvc.footer.FooterService;
import uk.co.ogauthority.pathfinder.service.FoxUrlService;
import uk.co.ogauthority.pathfinder.service.UserSessionService;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.file.FileUploadServiceTest;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.navigation.TopNavigationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.team.teammanagementcontext.TeamManagementContextService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationErrorOrderingService;

@EnableAnalyticsConfiguration
@Import({
    AbstractControllerTest.TestConfig.class,
    WebSecurityConfig.class
})
public abstract class AbstractControllerTest {

  protected MockMvc mockMvc;

  @Autowired
  protected WebApplicationContext context;

  @MockitoBean
  protected FoxUrlService foxUrlService;

  @MockitoBean
  protected TeamService teamService;

  @MockitoBean
  protected UserSessionService userSessionService;

  @MockitoBean
  protected TopNavigationService topNavigationService;

  @MockitoBean
  protected ProjectContextService projectContextService;

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
  protected ErrorService errorService;

  @MockitoBean
  protected FooterService footerService;

  @MockitoBean
  protected AnalyticsService analyticsService;

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

    doCallRealMethod().when(footerService).addFooterUrlsToModelAndView(any());
    doCallRealMethod().when(footerService).addFooterUrlsToModel(any());

  }

  @TestConfiguration
  @EnableConfigurationProperties(ExternalApiConfiguration.class)
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
    public ControllerHelperService controllerHelperService() { return new ControllerHelperService(validationErrorOrderingService()); }

    @Bean
    public ValidationErrorOrderingService validationErrorOrderingService() { return new ValidationErrorOrderingService(messageSource()); }

    @Bean
    public FileUploadProperties fileUploadProperties() {
      FileUploadProperties fileUploadProperties = new FileUploadProperties();
      fileUploadProperties.setMaxFileSize(FileUploadServiceTest.MAX_TEST_FILE_SIZE);
      fileUploadProperties.setAllowedExtensions(FileUploadServiceTest.ALLOWED_TEST_EXTENSIONS);
      return fileUploadProperties;
    }

    @Bean
    public ServiceProperties serviceProperties() {
      return new ServiceProperties(
          "service-name",
          "customer-mnemonic",
          "customer-name",
          false,
          "supply-chain-interface-url");
    }

    @Bean
    public ExternalApiAuthenticationEntryPoint externalApiAuthenticationEntryPoint() {
      return new ExternalApiAuthenticationEntryPoint(new ObjectMapper());
    }

  }

}
