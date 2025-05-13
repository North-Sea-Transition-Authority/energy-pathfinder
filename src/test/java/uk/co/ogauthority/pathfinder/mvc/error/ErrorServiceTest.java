package uk.co.ogauthority.pathfinder.mvc.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsConfigurationProperties;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;
import uk.co.ogauthority.pathfinder.mvc.footer.FooterService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(MockitoJUnitRunner.class)
public class ErrorServiceTest {

  static final String ERROR_REF_ATTRIBUTE_NAME = "errorRef";
  static final String TECHNICAL_SUPPORT_CONTACT_ATTRIBUTE_NAME = "technicalSupportContact";
  static final String SERVICE_ATTRIBUTE_NAME = "service";
  static final String SERVICE_HOME_URLS_ATTRIBUTE_NAME = "serviceHomeUrl";
  static final String ANALYTICS_ATTRIBUTE_NAME = "analytics";
  static final String COOKIES_PREFS_URL_ATTRIBUTE_NAME = "cookiePrefsUrl";
  static final String ANALYTICS_MEASUREMENT_URL_ATTRIBUTE_NAME = "analyticsMeasurementUrl";
  static final String ANALYTICS_CLIENT_ID_COOKIE_NAME_ATTRIBUTE_NAME = "analyticsClientIdCookieName";
  static final String SHOW_DIFFS_PROJECT_EVENT_CATEGORY_ATTRIBUTE_NAME = "showDiffsProjectEventCategory";

  @Mock
  private ServiceProperties serviceProperties;

  @Mock
  private FooterService footerService;

  @Mock
  private AnalyticsConfigurationProperties analyticsConfigurationProperties;

  private ErrorService errorService;

  @Before
  public void setup() {
    errorService = new ErrorService(serviceProperties, footerService, analyticsConfigurationProperties);
  }

  @Test
  public void addErrorAttributesToModel_whenThrowableError_assertExpectedModelAttributes() {
    final var resultingModelMap =  errorService.addErrorAttributesToModel(
        new ModelAndView(),
        new NullPointerException()
    ).getModelMap();

    assertThat(resultingModelMap).containsOnlyKeys(
        ERROR_REF_ATTRIBUTE_NAME,
        TECHNICAL_SUPPORT_CONTACT_ATTRIBUTE_NAME,
        SERVICE_ATTRIBUTE_NAME,
        SERVICE_HOME_URLS_ATTRIBUTE_NAME,
        ANALYTICS_ATTRIBUTE_NAME,
        COOKIES_PREFS_URL_ATTRIBUTE_NAME,
        ANALYTICS_MEASUREMENT_URL_ATTRIBUTE_NAME,
        ANALYTICS_CLIENT_ID_COOKIE_NAME_ATTRIBUTE_NAME,
        SHOW_DIFFS_PROJECT_EVENT_CATEGORY_ATTRIBUTE_NAME
    );
    assertThat(resultingModelMap.get(ERROR_REF_ATTRIBUTE_NAME)).isNotNull();
    assertCommonModelProperties(resultingModelMap);
  }

  @Test
  public void addErrorAttributesToModel_whenNoThrowableError_assertExpectedModelAttributes() {
    final var resultingModelMap =  errorService.addErrorAttributesToModel(
        new ModelAndView(),
        null
    ).getModelMap();

    assertThat(resultingModelMap).containsOnlyKeys(
        TECHNICAL_SUPPORT_CONTACT_ATTRIBUTE_NAME,
        SERVICE_ATTRIBUTE_NAME,
        SERVICE_HOME_URLS_ATTRIBUTE_NAME,
        ANALYTICS_ATTRIBUTE_NAME,
        COOKIES_PREFS_URL_ATTRIBUTE_NAME,
        ANALYTICS_MEASUREMENT_URL_ATTRIBUTE_NAME,
        ANALYTICS_CLIENT_ID_COOKIE_NAME_ATTRIBUTE_NAME,
        SHOW_DIFFS_PROJECT_EVENT_CATEGORY_ATTRIBUTE_NAME
    );
    assertCommonModelProperties(resultingModelMap);
  }

  @Test
  public void addErrorAttributesToModel_whenClientException_assertExpectedModelAttributes() {
    var modelAndView = new ModelAndView();
    var throwable = new ResponseStatusException(HttpStatus.NOT_FOUND);

    assertThat(errorService.addErrorAttributesToModel(modelAndView, throwable).getModelMap())
        .doesNotContainKey(ERROR_REF_ATTRIBUTE_NAME);
  }

  private void assertCommonModelProperties(ModelMap modelMap) {
    assertThat(modelMap).containsEntry(TECHNICAL_SUPPORT_CONTACT_ATTRIBUTE_NAME, ServiceContactDetail.TECHNICAL_SUPPORT);
    assertThat(modelMap).containsEntry(SERVICE_ATTRIBUTE_NAME, serviceProperties);
    assertThat(modelMap).containsEntry(COOKIES_PREFS_URL_ATTRIBUTE_NAME, ControllerUtils.getCookiesUrl());
    assertThat(modelMap).containsEntry(ANALYTICS_MEASUREMENT_URL_ATTRIBUTE_NAME, ControllerUtils.getAnalyticsMeasurementUrl());
  }

}