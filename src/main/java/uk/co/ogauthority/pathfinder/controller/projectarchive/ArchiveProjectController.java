package uk.co.ogauthority.pathfinder.controller.projectarchive;

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
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectDetailVersionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.projectarchive.ArchiveProjectForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectarchive.ArchiveProjectService;

@Controller
@ProjectStatusCheck(
    status = { ProjectStatus.QA, ProjectStatus.PUBLISHED },
    projectDetailVersionType = ProjectDetailVersionType.LATEST_SUBMITTED_VERSION
)
@ProjectFormPagePermissionCheck(permissions = {ProjectPermission.ARCHIVE})
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/archive")
public class ArchiveProjectController {

  public static final String ARCHIVE_PROJECT_PAGE_NAME_PREFIX = "Archive";

  private final ArchiveProjectService archiveProjectService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public ArchiveProjectController(ArchiveProjectService archiveProjectService,
                                  ControllerHelperService controllerHelperService) {
    this.archiveProjectService = archiveProjectService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getArchiveProject(@PathVariable("projectId") Integer projectId,
                                        ProjectContext projectContext,
                                        AuthenticatedUserAccount user) {
    return archiveProjectService.getArchiveProjectModelAndView(
        projectContext.getProjectDetails(),
        user,
        new ArchiveProjectForm()
    );
  }

  @PostMapping
  public ModelAndView archiveProject(@PathVariable("projectId") Integer projectId,
                                     @Valid @ModelAttribute("form") ArchiveProjectForm form,
                                     BindingResult bindingResult,
                                     ProjectContext projectContext,
                                     AuthenticatedUserAccount user) {
    bindingResult = archiveProjectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        archiveProjectService.getArchiveProjectModelAndView(projectContext.getProjectDetails(), user, form),
        form,
        () -> {
          archiveProjectService.archiveProject(projectContext.getProjectDetails(), user, form);
          return ReverseRouter.redirect(on(ManageProjectController.class).getProject(projectId, null, null, null));
        }
    );
  }
}
