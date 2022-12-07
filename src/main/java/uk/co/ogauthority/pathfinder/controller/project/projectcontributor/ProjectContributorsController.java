package uk.co.ogauthority.pathfinder.controller.project.projectcontributor;

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
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsManagementService;


@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@Controller
@RequestMapping("/project/{projectId}/project-contributors")
public class ProjectContributorsController {

  public static final String PAGE_NAME = "Project contributors";
  public static final String FORM_PAGE_NAME = "Project contributors";
  public static final String TASK_LIST_NAME = "Contributors";

  private final ProjectContributorsManagementService projectContributorsManagementService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public ProjectContributorsController(
      ProjectContributorsManagementService projectContributorsManagementService,
      ControllerHelperService controllerHelperService) {
    this.projectContributorsManagementService = projectContributorsManagementService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView renderProjectContributorsForm(@PathVariable("projectId") Integer projectId,
                                                    ProjectContext projectContext) {
    return projectContributorsManagementService.getProjectContributorsFormModelAndView(
        projectContributorsManagementService.getForm(projectContext.getProjectDetails()),
        projectContext.getProjectDetails(),
        List.of()
    );
  }

  @PostMapping
  public ModelAndView saveProjectContributors(@PathVariable("projectId") Integer projectId,
                                              @ModelAttribute("form") ProjectContributorsForm form,
                                              BindingResult bindingResult,
                                              ValidationType validationType,
                                              ProjectContext projectContext) {
    bindingResult = projectContributorsManagementService.validate(
        form,
        bindingResult,
        validationType,
        projectContext.getProjectDetails()
    );
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        projectContributorsManagementService.getProjectContributorsFormModelAndView(
            form,
            projectContext.getProjectDetails(),
            bindingResult.getFieldErrors()
        ),
        form,
        () -> {
          projectContributorsManagementService.saveProjectContributors(form, projectContext.getProjectDetails());
          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
        }
    );
  }
}
