package uk.co.ogauthority.pathfinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.form.dashboard.DashboardFilterForm;
import uk.co.ogauthority.pathfinder.service.WorkAreaService;

@Controller
public class WorkAreaController {

  private final WorkAreaService workAreaService;

  @Autowired
  public WorkAreaController(WorkAreaService workAreaService) {
    this.workAreaService = workAreaService;
  }

  @GetMapping("/work-area")
  public ModelAndView getWorkArea(AuthenticatedUserAccount user) {
    var filter = new DashboardFilter();//TODO PAT-343 get from form / session
    return workAreaService.getWorkAreaModelAndViewForUser(user, filter, new DashboardFilterForm());
  }

  @PostMapping("/work-area")
  public ModelAndView getWorkAreaFiltered(
      AuthenticatedUserAccount user,
      @ModelAttribute("form") DashboardFilterForm form
  ) {
    var filter = new DashboardFilter(form);//TODO PAT-343 get from form / session
    return workAreaService.getWorkAreaModelAndViewForUser(user, filter, form);
  }

}
