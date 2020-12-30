package uk.co.ogauthority.pathfinder.controller.project.submission;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.SubmitProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/submit")
public class SubmitProjectController {

  public static final String PAGE_NAME = "Review and submit";

  private final SubmitProjectService submitProjectService;

  @Autowired
  public SubmitProjectController(SubmitProjectService submitProjectService) {
    this.submitProjectService = submitProjectService;
  }

  @GetMapping
  public ModelAndView getProjectSummary(@PathVariable("projectId") Integer projectId,
                                        ProjectContext projectContext) {
    return submitProjectService.getProjectSubmitSummaryModelAndView(projectContext.getProjectDetails());
  }

  @PostMapping
  public ModelAndView submitProject(@PathVariable("projectId") Integer projectId,
                                    ProjectContext projectContext) {

    final var projectDetail = projectContext.getProjectDetails();
    final var isProjectValid = submitProjectService.isProjectValid(projectDetail);

    return isProjectValid
        ? submitProjectAndRedirectToConfirmation(projectDetail, projectContext.getUserAccount())
        : submitProjectService.getProjectSubmitSummaryModelAndView(projectDetail, false);
  }

  @GetMapping("/confirmation")
  @ProjectStatusCheck(status = ProjectStatus.QA)
  public ModelAndView submitProjectConfirmation(@PathVariable("projectId") Integer projectId,
                                                ProjectContext projectContext) {
    return submitProjectService.getProjectSubmitConfirmationModelAndView(projectContext.getProjectDetails());
  }

  private ModelAndView submitProjectAndRedirectToConfirmation(ProjectDetail projectDetail,
                                                              AuthenticatedUserAccount userAccount) {
    submitProjectService.submitProject(projectDetail, userAccount);
    final var projectId = projectDetail.getProject().getId();
    return ReverseRouter.redirect(on(SubmitProjectController.class).submitProjectConfirmation(projectId, null));
  }
}
