package uk.co.ogauthority.pathfinder.controller.projecttransfer;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
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
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectDetailVersionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projecttransfer.ProjectTransferModelService;
import uk.co.ogauthority.pathfinder.service.projecttransfer.ProjectTransferService;

@Controller
@ProjectStatusCheck(
    status = { ProjectStatus.QA, ProjectStatus.PUBLISHED },
    projectDetailVersionType = ProjectDetailVersionType.LATEST_SUBMITTED_VERSION
)
@ProjectFormPagePermissionCheck(permissions = ProjectPermission.TRANSFER)
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/change-operator")
public class ProjectTransferController extends ProjectFormPageController {

  public static final String PAGE_NAME = "Change project operator/developer";

  private final ProjectTransferService projectTransferService;
  private final ProjectTransferModelService projectTransferModelService;

  @Autowired
  public ProjectTransferController(BreadcrumbService breadcrumbService,
                                   ControllerHelperService controllerHelperService,
                                   ProjectTransferService projectTransferService,
                                   ProjectTransferModelService projectTransferModelService) {
    super(breadcrumbService, controllerHelperService);
    this.projectTransferService = projectTransferService;
    this.projectTransferModelService = projectTransferModelService;
  }

  @GetMapping
  public ModelAndView getTransferProject(@PathVariable("projectId") Integer projectId,
                                         ProjectContext projectContext,
                                         AuthenticatedUserAccount user) {
    return projectTransferModelService.getTransferProjectModelAndView(
        projectContext.getProjectDetails(),
        user,
        new ProjectTransferForm()
    );
  }

  @PostMapping
  public ModelAndView transferProject(@PathVariable("projectId") Integer projectId,
                                      @Valid @ModelAttribute("form") ProjectTransferForm form,
                                      BindingResult bindingResult,
                                      ProjectContext projectContext,
                                      AuthenticatedUserAccount user) {
    bindingResult = projectTransferService.validate(form, bindingResult, projectContext.getProjectDetails());
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        projectTransferModelService.getTransferProjectModelAndView(projectContext.getProjectDetails(), user, form),
        form,
        () -> {
          projectTransferService.transferProject(projectContext.getProjectDetails(), user, form);
          return ReverseRouter.redirect(on(ManageProjectController.class).getProject(projectId, null, null, null));
        }
    );
  }
}
