package uk.co.ogauthority.pathfinder.controller.project;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@Controller
public class StartProjectController {


  @GetMapping("/start-project")
  public ModelAndView startPage() {
    return new ModelAndView("project/startPage")
        .addObject("startActionUrl", ReverseRouter.route(on(StartProjectController.class).startProject()));
  }

  @PostMapping("/start-project")
  public ModelAndView startProject() {
    //TODO PAT-114 create project entities
    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(1));
  }
}
