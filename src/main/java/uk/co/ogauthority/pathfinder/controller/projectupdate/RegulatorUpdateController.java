package uk.co.ogauthority.pathfinder.controller.projectupdate;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.RequestUpdateForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContext;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorProjectUpdateService;

@Controller
@ProjectStatusCheck(status = {ProjectStatus.QA, ProjectStatus.PUBLISHED})
@ProjectFormPagePermissionCheck(permissions = {ProjectPermission.REQUEST_UPDATE})
@RequestMapping("/project/{projectId}")
public class RegulatorUpdateController {

  public static final String REQUEST_UPDATE_PAGE_NAME = "Request update";

  private final RegulatorProjectUpdateService regulatorProjectUpdateService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public RegulatorUpdateController(RegulatorProjectUpdateService regulatorProjectUpdateService,
                                   ControllerHelperService controllerHelperService) {
    this.regulatorProjectUpdateService = regulatorProjectUpdateService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping("/request-update")
  public ModelAndView getRequestUpdate(@PathVariable("projectId") Integer projectId,
                                       ProjectUpdateContext projectUpdateContext,
                                       AuthenticatedUserAccount user) {
    return regulatorProjectUpdateService.getRequestUpdateModelAndView(
        projectUpdateContext.getProjectDetails(),
        user,
        new RequestUpdateForm()
    );
  }

  @PostMapping("/request-update")
  public ModelAndView requestUpdate(@PathVariable("projectId") Integer projectId,
                                    @Valid @ModelAttribute("form") RequestUpdateForm form,
                                    BindingResult bindingResult,
                                    ProjectUpdateContext projectUpdateContext,
                                    AuthenticatedUserAccount user) {
    bindingResult = regulatorProjectUpdateService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        regulatorProjectUpdateService.getRequestUpdateModelAndView(projectUpdateContext.getProjectDetails(), user, form),
        form,
        () -> {
          regulatorProjectUpdateService.startRegulatorRequestedUpdate(projectUpdateContext.getProjectDetails(), form, user);
          return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null, null));
        }
    );
  }
}
