package uk.co.ogauthority.pathfinder.controller.project.projectassessment;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.projectassessment.ProjectAssessmentForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectassessment.ProjectAssessmentContext;
import uk.co.ogauthority.pathfinder.service.project.projectassessment.ProjectAssessmentService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;

@Controller
@ProjectStatusCheck(status = ProjectStatus.QA)
@ProjectFormPagePermissionCheck(permissions = {ProjectPermission.PROVIDE_ASSESSMENT})
@RequestMapping("/project/{projectId}/project-assessment")
public class ProjectAssessmentController extends ProjectFormPageController {

  public static final String PAGE_NAME = "Project assessment";

  private final ProjectAssessmentService projectAssessmentService;

  public ProjectAssessmentController(BreadcrumbService breadcrumbService,
                                     ControllerHelperService controllerHelperService,
                                     ProjectAssessmentService projectAssessmentService) {
    super(breadcrumbService, controllerHelperService);
    this.projectAssessmentService = projectAssessmentService;
  }

  @GetMapping
  public ModelAndView getProjectAssessment(@PathVariable("projectId") Integer projectId,
                                           ProjectAssessmentContext projectAssessmentContext) {
    return projectAssessmentService.getProjectAssessmentModelAndView(projectId, new ProjectAssessmentForm());
  }

  @PostMapping
  public ModelAndView createProjectAssessment(@PathVariable("projectId") Integer projectId,
                                              @Valid @ModelAttribute("form") ProjectAssessmentForm form,
                                              BindingResult bindingResult,
                                              ProjectAssessmentContext projectAssessmentContext,
                                              AuthenticatedUserAccount user) {
    bindingResult = projectAssessmentService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        projectAssessmentService.getProjectAssessmentModelAndView(projectId, form),
        form,
        () -> {
          projectAssessmentService.createProjectAssessment(projectAssessmentContext.getProjectDetails(), user, form);

          return ReverseRouter.redirect(on(ManageProjectController.class).getProject(projectId, null, null));
        });
  }
}
