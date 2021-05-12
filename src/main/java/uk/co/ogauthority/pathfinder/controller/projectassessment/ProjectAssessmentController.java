package uk.co.ogauthority.pathfinder.controller.projectassessment;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
import org.apache.commons.lang3.BooleanUtils;
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
import uk.co.ogauthority.pathfinder.controller.projectupdate.RegulatorUpdateController;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectDetailVersionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.projectassessment.ProjectAssessmentForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContext;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;

@Controller
@ProjectStatusCheck(
    status = ProjectStatus.QA,
    projectDetailVersionType = ProjectDetailVersionType.LATEST_SUBMITTED_VERSION
)
@ProjectFormPagePermissionCheck(permissions = ProjectPermission.PROVIDE_ASSESSMENT)
@ProjectTypeCheck(types = { ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN })
@RequestMapping("/project/{projectId}/project-assessment")
public class ProjectAssessmentController extends ProjectFormPageController {

  private final ProjectAssessmentService projectAssessmentService;
  private final RegulatorUpdateRequestService regulatorUpdateRequestService;

  public ProjectAssessmentController(BreadcrumbService breadcrumbService,
                                     ControllerHelperService controllerHelperService,
                                     ProjectAssessmentService projectAssessmentService,
                                     RegulatorUpdateRequestService regulatorUpdateRequestService) {
    super(breadcrumbService, controllerHelperService);
    this.projectAssessmentService = projectAssessmentService;
    this.regulatorUpdateRequestService = regulatorUpdateRequestService;
  }

  @GetMapping
  public ModelAndView getProjectAssessment(@PathVariable("projectId") Integer projectId,
                                           ProjectAssessmentContext projectAssessmentContext,
                                           AuthenticatedUserAccount user) {
    return projectAssessmentService.getProjectAssessmentModelAndView(
        projectAssessmentContext.getProjectDetails(),
        user,
        new ProjectAssessmentForm()
    );
  }

  @PostMapping
  public ModelAndView createProjectAssessment(@PathVariable("projectId") Integer projectId,
                                              @Valid @ModelAttribute("form") ProjectAssessmentForm form,
                                              BindingResult bindingResult,
                                              ProjectAssessmentContext projectAssessmentContext,
                                              AuthenticatedUserAccount user) {
    var projectDetail = projectAssessmentContext.getProjectDetails();
    bindingResult = projectAssessmentService.validate(form, bindingResult, projectDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        projectAssessmentService.getProjectAssessmentModelAndView(projectDetail, user, form),
        form,
        () -> {
          projectAssessmentService.createProjectAssessment(projectDetail, user, form);

          if ((!BooleanUtils.isTrue(form.getReadyToBePublished()) || BooleanUtils.isTrue(form.getUpdateRequired()))
              && regulatorUpdateRequestService.canRequestUpdate(projectDetail)) {
            return ReverseRouter.redirect(on(RegulatorUpdateController.class).getRequestUpdate(projectId, null, null));
          }
          return ReverseRouter.redirect(on(ManageProjectController.class).getProject(projectId, null, null, null));
        });
  }
}
