package uk.co.ogauthority.pathfinder.controller;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.StartProjectController;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@Controller
public class WorkAreaController {

  @GetMapping("/work-area")
  public ModelAndView getWorkArea() {
    return new ModelAndView("workArea")
        .addObject("startProjectUrl", ReverseRouter.route(on(StartProjectController.class).startProject()));
  }
}
