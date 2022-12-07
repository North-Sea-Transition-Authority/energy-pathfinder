package uk.co.ogauthority.pathfinder.controller.project.workplanprojectcontributor;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.workoplanprojectcontribution.ForwardWorkPlanProjectContributorsForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.workoplanprojectcontribution.ForwardWorkPlanProjectContributorManagementService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/work-plan-project-contributors")
public class ForwardWorkPlanProjectContributorsController {

  public static final String PAGE_NAME = "Contributors";
  public static final String TASK_LIST_NAME = "Contributors";

  private final ForwardWorkPlanProjectContributorManagementService forwardWorkPlanProjectContributorManagementService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public ForwardWorkPlanProjectContributorsController(
      ForwardWorkPlanProjectContributorManagementService forwardWorkPlanProjectContributorManagementService,
      ControllerHelperService controllerHelperService) {
    this.forwardWorkPlanProjectContributorManagementService = forwardWorkPlanProjectContributorManagementService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView renderProjectContributors(@PathVariable("projectId") Integer projectId,
                                                ProjectContext projectContext) {
    return forwardWorkPlanProjectContributorManagementService.getProjectContributorsFormModelAndView(
        forwardWorkPlanProjectContributorManagementService.getForm(projectContext.getProjectDetails()),
        projectContext.getProjectDetails(),
        List.of()
    );
  }

  @PostMapping
  public ModelAndView saveProjectContributors(@PathVariable("projectId") Integer projectId,
                                              @ModelAttribute("form") ForwardWorkPlanProjectContributorsForm form,
                                              BindingResult bindingResult,
                                              ValidationType validationType,
                                              ProjectContext projectContext) {
    bindingResult = forwardWorkPlanProjectContributorManagementService.validate(
        form,
        bindingResult,
        validationType,
        projectContext.getProjectDetails()
    );
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        forwardWorkPlanProjectContributorManagementService.getProjectContributorsFormModelAndView(
            form,
            projectContext.getProjectDetails(),
            bindingResult.getFieldErrors()
        ),
        form,
        () -> {
          forwardWorkPlanProjectContributorManagementService.saveForwardWorkPlanProjectContributors(
              form,
              projectContext.getProjectDetails()
          );
          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
        }
    );
  }
}
