package uk.co.ogauthority.pathfinder.controller;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.project.StartProjectController;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@Controller
public class WorkAreaController {

  @GetMapping("/work-area")
  public ModelAndView getWorkArea(AuthenticatedUserAccount user) {
    return new ModelAndView("workArea")
        .addObject("showStartProject", user.getUserPrivileges().contains(UserPrivilege.PATHFINDER_PROJECT_CREATE))
        .addObject("startProjectUrl", ReverseRouter.route(on(StartProjectController.class).startProject(null)));
  }
}
