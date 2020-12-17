package uk.co.ogauthority.pathfinder.controller.project;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@ProjectStatusCheck(status = {ProjectStatus.DRAFT})
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/cancel-draft")
public class CancelDraftProjectVersionController {

  private final CancelDraftProjectVersionService cancelDraftProjectVersionService;

  @Autowired
  public CancelDraftProjectVersionController(CancelDraftProjectVersionService cancelDraftProjectVersionService) {
    this.cancelDraftProjectVersionService = cancelDraftProjectVersionService;
  }

  @GetMapping
  public ModelAndView getCancelDraft(@PathVariable("projectId") Integer projectId,
                                     ProjectContext projectContext,
                                     AuthenticatedUserAccount user) {
    return cancelDraftProjectVersionService.getCancelDraftModelAndView(projectContext.getProjectDetails(), user);
  }

  @PostMapping
  public ModelAndView cancelDraft(@PathVariable("projectId") Integer projectId,
                                  ProjectContext projectContext,
                                  AuthenticatedUserAccount user) {
    cancelDraftProjectVersionService.cancelDraft(projectContext.getProjectDetails());
    return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(user, null));
  }
}
