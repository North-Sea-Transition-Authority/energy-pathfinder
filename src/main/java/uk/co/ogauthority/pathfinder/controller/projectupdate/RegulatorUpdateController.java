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
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectDetailVersionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.RequestUpdateForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorProjectUpdateContext;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;

@Controller
@ProjectStatusCheck(
    status = { ProjectStatus.QA, ProjectStatus.PUBLISHED },
    projectDetailVersionType = ProjectDetailVersionType.LATEST_SUBMITTED_VERSION
)
@ProjectFormPagePermissionCheck(permissions = ProjectPermission.REQUEST_UPDATE)
@ProjectTypeCheck(types = { ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN })
@RequestMapping("/project/{projectId}")
public class RegulatorUpdateController {

  public static final String REQUEST_UPDATE_PAGE_NAME = "Request update";

  private final RegulatorUpdateRequestService regulatorUpdateRequestService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public RegulatorUpdateController(RegulatorUpdateRequestService regulatorUpdateRequestService,
                                   ControllerHelperService controllerHelperService) {
    this.regulatorUpdateRequestService = regulatorUpdateRequestService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping("/request-update")
  public ModelAndView getRequestUpdate(@PathVariable("projectId") Integer projectId,
                                       RegulatorProjectUpdateContext regulatorProjectUpdateContext,
                                       AuthenticatedUserAccount user) {
    return regulatorUpdateRequestService.getRequestUpdateModelAndView(
        regulatorProjectUpdateContext.getProjectDetails(),
        user,
        new RequestUpdateForm()
    );
  }

  @PostMapping("/request-update")
  public ModelAndView requestUpdate(@PathVariable("projectId") Integer projectId,
                                    @Valid @ModelAttribute("form") RequestUpdateForm form,
                                    BindingResult bindingResult,
                                    RegulatorProjectUpdateContext regulatorProjectUpdateContext,
                                    AuthenticatedUserAccount user) {
    bindingResult = regulatorUpdateRequestService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        regulatorUpdateRequestService.getRequestUpdateModelAndView(regulatorProjectUpdateContext.getProjectDetails(), user, form),
        form,
        () -> {
          regulatorUpdateRequestService.requestUpdate(regulatorProjectUpdateContext.getProjectDetails(), form, user);
          return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null, null));
        }
    );
  }
}
