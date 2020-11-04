package uk.co.ogauthority.pathfinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
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
    return workAreaService.getWorkAreaModelAndViewForUser(user);
  }

  @PostMapping("/work-area")
  public ModelAndView getWorkAreaFiltered(AuthenticatedUserAccount user) {
    //TODO PAT-116 filter projects
    return workAreaService.getWorkAreaModelAndViewForUser(user);
  }

}
