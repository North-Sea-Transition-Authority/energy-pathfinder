package uk.co.ogauthority.pathfinder.controller;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsEventCategory;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsService;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsUtils;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.form.dashboard.DashboardFilterForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.WorkAreaService;

@Controller
@SessionAttributes("dashboardFilter")
public class WorkAreaController {

  private final WorkAreaService workAreaService;
  private final AnalyticsService analyticsService;

  @Autowired
  public WorkAreaController(WorkAreaService workAreaService,
                            AnalyticsService analyticsService) {
    this.workAreaService = workAreaService;
    this.analyticsService = analyticsService;
  }

  @GetMapping("/work-area")
  public ModelAndView getWorkArea(
      AuthenticatedUserAccount user,
      @ModelAttribute("dashboardFilter") DashboardFilter filter
  ) {
    return workAreaService.getWorkAreaModelAndViewForUser(user, filter, new DashboardFilterForm(filter));
  }

  @PostMapping("/work-area")
  public ModelAndView getWorkAreaFiltered(
      AuthenticatedUserAccount user,
      @ModelAttribute("form") DashboardFilterForm form,
      @ModelAttribute("dashboardFilter") DashboardFilter filter,
      @CookieValue(name = AnalyticsUtils.GA_CLIENT_ID_COOKIE_NAME, required = false) Optional<String> analyticsClientId
  ) {
    filter.setFromForm(form);
    var analyticsParamMap = AnalyticsUtils.getFiltersUsedParamMap(filter);
    analyticsService.sendAnalyticsEvent(analyticsClientId, AnalyticsEventCategory.WORK_AREA_FILTERED, analyticsParamMap);
    return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null, null));
  }

  @GetMapping("/work-area/clear-filter")
  public ModelAndView getWorkAreaClearFilter(
      AuthenticatedUserAccount user,
      @ModelAttribute("dashboardFilter") DashboardFilter filter
  ) {
    filter.clearFilter();
    return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(user, null));
  }

  @ModelAttribute("dashboardFilter")
  private DashboardFilter getDefaultFilter(AuthenticatedUserAccount user) {
    return workAreaService.getDefaultFilterForUser(user);
  }

}
