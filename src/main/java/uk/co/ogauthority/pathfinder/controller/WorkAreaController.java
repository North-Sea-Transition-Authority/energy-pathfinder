package uk.co.ogauthority.pathfinder.controller;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.form.dashboard.DashboardFilterForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.WorkAreaService;

@Controller
@SessionAttributes("dashboardFilter")
public class WorkAreaController {

  private final WorkAreaService workAreaService;

  @Autowired
  public WorkAreaController(WorkAreaService workAreaService) {
    this.workAreaService = workAreaService;
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
      @ModelAttribute("dashboardFilter") DashboardFilter filter
  ) {
    filter.setFromForm(form);
    return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null, null));
  }

  @GetMapping("/work-area/clear-filter")
  public ModelAndView getWorkAreaClearFilter(
      AuthenticatedUserAccount user,
      SessionStatus status
  ) {
    status.setComplete();//Allow cleanup of session attributes
    return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(user, getDefaultFilter()));
  }

  @ModelAttribute("dashboardFilter")
  private DashboardFilter getDefaultFilter() {
    return new DashboardFilter();
  }

}
