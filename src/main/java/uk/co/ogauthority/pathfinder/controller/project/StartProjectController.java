package uk.co.ogauthority.pathfinder.controller.project;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.StartProjectService;

@Controller
public class StartProjectController {


  private final StartProjectService startProjectService;

  @Autowired
  public StartProjectController(StartProjectService startProjectService) {
    this.startProjectService = startProjectService;
  }

  @GetMapping("/start-project")
  public ModelAndView startPage(AuthenticatedUserAccount user) {
    return new ModelAndView("project/startPage")
        .addObject("startActionUrl", ReverseRouter.route(on(StartProjectController.class).startProject(user)));
  }

  @PostMapping("/start-project")
  public ModelAndView startProject(AuthenticatedUserAccount user) {
    var projectDetail = startProjectService.startProject(user);
    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectDetail.getProject().getId(), null));
  }
}
