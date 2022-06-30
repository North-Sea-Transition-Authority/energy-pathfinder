package uk.co.ogauthority.pathfinder.mvc;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsConfigurationProperties;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsEventCategory;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsUtils;
import uk.co.ogauthority.pathfinder.auth.CurrentUserView;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.mvc.footer.FooterService;
import uk.co.ogauthority.pathfinder.service.FoxUrlService;
import uk.co.ogauthority.pathfinder.service.navigation.TopNavigationService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.SecurityUtil;

@ControllerAdvice
public class DefaultPageControllerAdvice {

  private final FoxUrlService foxUrlService;
  private final ServiceProperties serviceProperties;
  private final TopNavigationService topNavigationService;
  private final HttpServletRequest request;
  private final FooterService footerService;
  private final AnalyticsConfigurationProperties analyticsConfigurationProperties;
  private final String analyticsMeasurementUrl;

  @Autowired
  public DefaultPageControllerAdvice(FoxUrlService foxUrlService,
                                     ServiceProperties serviceProperties,
                                     TopNavigationService topNavigationService,
                                     HttpServletRequest request,
                                     FooterService footerService,
                                     AnalyticsConfigurationProperties analyticsConfigurationProperties) {
    this.foxUrlService = foxUrlService;
    this.serviceProperties = serviceProperties;
    this.topNavigationService = topNavigationService;
    this.request = request;
    this.footerService = footerService;
    this.analyticsConfigurationProperties = analyticsConfigurationProperties;
    this.analyticsMeasurementUrl = ControllerUtils.getAnalyticsMeasurementUrl();
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    // Trim whitespace from form fields
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }

  @ModelAttribute
  public void addCommonModelAttributes(Model model) {
    addCurrentUserView(model);
    addCommonUrls(model);
    addServiceSpecificAttributes(model);
    addTopNavigationItems(model, request);
    addAnalyticsItems(model);
  }

  private void addAnalyticsItems(Model model) {
    model.addAttribute("analytics", analyticsConfigurationProperties.getProperties());
    model.addAttribute("cookiePrefsUrl", ControllerUtils.getCookiesUrl());
    model.addAttribute("analyticsMeasurementUrl", analyticsMeasurementUrl);
    model.addAttribute("analyticsClientIdCookieName", AnalyticsUtils.GA_CLIENT_ID_COOKIE_NAME);
    model.addAttribute("showDiffsProjectEventCategory", AnalyticsEventCategory.SHOW_DIFFS_PROJECT.name());
  }

  private void addCurrentUserView(Model model) {
    SecurityUtil.getAuthenticatedUserFromSecurityContext()
        .ifPresent(user -> model.addAttribute("currentUserView", CurrentUserView.authenticated(user)));
  }

  private void addCommonUrls(Model model) {
    model.addAttribute("foxLogoutUrl", foxUrlService.getFoxLogoutUrl());
    model.addAttribute("feedbackUrl", ControllerUtils.getFeedbackUrl());
    model.addAttribute("serviceHomeUrl", ControllerUtils.getWorkAreaUrl());
    footerService.addFooterUrlsToModel(model);
  }

  private void addServiceSpecificAttributes(Model model) {
    model.addAttribute("service", serviceProperties);
  }

  private void addTopNavigationItems(Model model, HttpServletRequest request) {
    SecurityUtil.getAuthenticatedUserFromSecurityContext()
        .ifPresent(user -> {
          model.addAttribute("navigationItems", topNavigationService.getTopNavigationItems(user));
          model.addAttribute("currentEndPoint", request.getRequestURI());
        });
  }
}
