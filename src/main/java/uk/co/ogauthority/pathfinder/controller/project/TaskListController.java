package uk.co.ogauthority.pathfinder.controller.project;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardedContractController;
import uk.co.ogauthority.pathfinder.controller.project.location.ProjectLocationController;
import uk.co.ogauthority.pathfinder.controller.project.projectinformation.ProjectInformationController;
import uk.co.ogauthority.pathfinder.controller.project.selectoperator.ChangeProjectOperatorController;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/tasks")
public class TaskListController {

  private final ProjectInformationService projectInformationService;
  private final ProjectLocationService projectLocationService;
  private final SelectOperatorService selectOperatorService;
  private final BreadcrumbService breadcrumbService;

  @Autowired
  public TaskListController(ProjectInformationService projectInformationService,
                            BreadcrumbService breadcrumbService,
                            ProjectLocationService projectLocationService,
                            SelectOperatorService selectOperatorService) {
    this.projectInformationService = projectInformationService;
    this.breadcrumbService = breadcrumbService;
    this.projectLocationService = projectLocationService;
    this.selectOperatorService = selectOperatorService;
  }

  @GetMapping
  public ModelAndView viewTaskList(@PathVariable("projectId") Integer projectId,
                                   ProjectContext projectContext) {

    var modelAndView = new ModelAndView("project/taskList");

    breadcrumbService.fromWorkArea(modelAndView, "Task list");

    modelAndView.addObject("changeOperatorUrl",
        ReverseRouter.route(on(ChangeProjectOperatorController.class).changeOperator(null, projectId, null)));
    modelAndView.addObject("changeOperatorName", ChangeProjectOperatorController.PAGE_NAME);
    modelAndView.addObject("changeOperatorCompleted", selectOperatorService.isComplete(
        projectContext.getProjectDetails())
    );

    modelAndView.addObject("projectInformationUrl",
        ReverseRouter.route(on(ProjectInformationController.class).getProjectInformation(projectId, null))
    );
    modelAndView.addObject("projectInformationText", ProjectInformationController.PAGE_NAME);
    modelAndView.addObject("projectInformationCompleted", projectInformationService.isComplete(projectContext.getProjectDetails()));

    modelAndView.addObject("locationUrl",
        ReverseRouter.route(on(ProjectLocationController.class).getLocationDetails(projectId, null))
    );
    modelAndView.addObject("projectLocationText", ProjectLocationController.PAGE_NAME);
    modelAndView.addObject("projectLocationCompleted", projectLocationService.isComplete(projectContext.getProjectDetails()));

    modelAndView.addObject("upcomingTendersUrl",
        ReverseRouter.route(on(UpcomingTendersController.class).viewTenders(projectId, null))
    );
    modelAndView.addObject("upcomingTendersText", UpcomingTendersController.PAGE_NAME);

    modelAndView.addObject("awardedContractsUrl",
        ReverseRouter.route(on(AwardedContractController.class).viewAwardedContracts(projectId, null))
    );
    modelAndView.addObject("awardedContractsText", AwardedContractController.PAGE_NAME);

    return modelAndView;
  }

}
