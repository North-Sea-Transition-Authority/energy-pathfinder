package uk.co.ogauthority.pathfinder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WorkAreaController {

  @GetMapping("/work-area")
  public ModelAndView getWorkArea() {
    return new ModelAndView("workArea");
  }
}
