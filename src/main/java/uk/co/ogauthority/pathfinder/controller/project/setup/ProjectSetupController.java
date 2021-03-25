package uk.co.ogauthority.pathfinder.controller.project.setup;

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
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/project-setup")
public class ProjectSetupController extends ProjectFormPageController {
  public static final String PAGE_NAME = "Set up your project";

  private final ProjectSetupService projectSetupService;

  @Autowired
  public ProjectSetupController(ProjectSetupService projectSetupService,
                                BreadcrumbService breadcrumbService,
                                ControllerHelperService controllerHelperService) {
    super(breadcrumbService, controllerHelperService);
    this.projectSetupService = projectSetupService;
  }

  @GetMapping
  public ModelAndView getProjectSetup(@PathVariable("projectId") Integer projectId,
                                      ProjectContext projectContext) {
    return getProjectSetupModelAndView(
        projectContext.getProjectDetails(),
        projectId,
        projectSetupService.getForm(projectContext.getProjectDetails())
      );
  }

  @PostMapping
  public ModelAndView saveProjectSetup(@PathVariable("projectId") Integer projectId,
                                       @Valid @ModelAttribute("form") ProjectSetupForm form,
                                       BindingResult bindingResult,
                                       ValidationType validationType,
                                       ProjectContext projectContext) {
    bindingResult = projectSetupService.validate(
        form,
        bindingResult,
        validationType,
        projectContext.getProjectDetails()
    );

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getProjectSetupModelAndView(projectContext.getProjectDetails(), projectId, form),
        form,
        () -> {
          projectSetupService.createOrUpdateProjectTaskListSetup(
              projectContext.getProjectDetails(),
              form
          );
          AuditService.audit(
              AuditEvent.PROJECT_SETUP_UPDATED,
              String.format(
                  AuditEvent.PROJECT_SETUP_UPDATED.getMessage(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
        }
    );
  }

  private ModelAndView getProjectSetupModelAndView(ProjectDetail detail, Integer projectId, ProjectSetupForm form) {
    var modelAndView = projectSetupService.getProjectSetupModelAndView(detail, form);
    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }
}
