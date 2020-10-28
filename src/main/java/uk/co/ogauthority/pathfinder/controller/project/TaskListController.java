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
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.CollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.controller.project.decommissionedwell.DecommissionedWellController;
import uk.co.ogauthority.pathfinder.controller.project.integratedrig.IntegratedRigController;
import uk.co.ogauthority.pathfinder.controller.project.location.ProjectLocationController;
import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;
import uk.co.ogauthority.pathfinder.controller.project.projectinformation.ProjectInformationController;
import uk.co.ogauthority.pathfinder.controller.project.selectoperator.ChangeProjectOperatorController;
import uk.co.ogauthority.pathfinder.controller.project.submission.SubmitProjectController;
import uk.co.ogauthority.pathfinder.controller.project.subseainfrastructure.SubseaInfrastructureController;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.integratedrig.IntegratedRigService;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.subseainfrastructure.SubseaInfrastructureService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/tasks")
public class TaskListController {

  private final ProjectInformationService projectInformationService;
  private final ProjectLocationService projectLocationService;
  private final SelectOperatorService selectOperatorService;
  private final UpcomingTenderService upcomingTenderService;
  private final CollaborationOpportunitiesService collaborationOpportunitiesService;
  private final BreadcrumbService breadcrumbService;
  private final AwardedContractService awardedContractService;
  private final PlatformsFpsosService platformsFpsosService;
  private final SubseaInfrastructureService subseaInfrastructureService;
  private final IntegratedRigService integratedRigService;

  @Autowired
  public TaskListController(ProjectInformationService projectInformationService,
                            BreadcrumbService breadcrumbService,
                            ProjectLocationService projectLocationService,
                            SelectOperatorService selectOperatorService,
                            UpcomingTenderService upcomingTenderService,
                            CollaborationOpportunitiesService collaborationOpportunitiesService,
                            AwardedContractService awardedContractService,
                            PlatformsFpsosService platformsFpsosService,
                            SubseaInfrastructureService subseaInfrastructureService,
                            IntegratedRigService integratedRigService) {
    this.projectInformationService = projectInformationService;
    this.breadcrumbService = breadcrumbService;
    this.projectLocationService = projectLocationService;
    this.selectOperatorService = selectOperatorService;
    this.collaborationOpportunitiesService = collaborationOpportunitiesService;
    this.awardedContractService = awardedContractService;
    this.upcomingTenderService = upcomingTenderService;
    this.platformsFpsosService = platformsFpsosService;
    this.subseaInfrastructureService = subseaInfrastructureService;
    this.integratedRigService = integratedRigService;
  }

  @GetMapping
  public ModelAndView viewTaskList(@PathVariable("projectId") Integer projectId,
                                   ProjectContext projectContext) {

    var modelAndView = new ModelAndView("project/taskList");
    var projectDetails = projectContext.getProjectDetails();

    breadcrumbService.fromWorkArea(modelAndView, "Task list");

    modelAndView.addObject("changeOperatorUrl",
        ReverseRouter.route(on(ChangeProjectOperatorController.class).changeOperator(null, projectId, null)));
    modelAndView.addObject("changeOperatorName", ChangeProjectOperatorController.PAGE_NAME);
    modelAndView.addObject("changeOperatorCompleted", selectOperatorService.isComplete(projectDetails)
    );

    modelAndView.addObject("projectInformationUrl",
        ReverseRouter.route(on(ProjectInformationController.class).getProjectInformation(projectId, null))
    );
    modelAndView.addObject("projectInformationText", ProjectInformationController.PAGE_NAME);
    modelAndView.addObject("projectInformationCompleted", projectInformationService.isComplete(projectDetails));

    modelAndView.addObject("locationUrl",
        ReverseRouter.route(on(ProjectLocationController.class).getLocationDetails(projectId, null))
    );
    modelAndView.addObject("projectLocationText", ProjectLocationController.PAGE_NAME);
    modelAndView.addObject("projectLocationCompleted", projectLocationService.isComplete(projectDetails));

    modelAndView.addObject("upcomingTendersUrl",
        ReverseRouter.route(on(UpcomingTendersController.class).viewTenders(projectId, null))
    );
    modelAndView.addObject("upcomingTendersText", UpcomingTendersController.PAGE_NAME);
    modelAndView.addObject("upcomingTendersCompleted", upcomingTenderService.isComplete(
        projectContext.getProjectDetails()));

    modelAndView.addObject("awardedContractsUrl",
        ReverseRouter.route(on(AwardedContractController.class).viewAwardedContracts(projectId, null))
    );
    modelAndView.addObject("awardedContractsText", AwardedContractController.PAGE_NAME);
    modelAndView.addObject("awardedContractsCompleted", awardedContractService.isComplete(projectDetails));

    modelAndView.addObject("collaborationOpportunitiesUrl",
        ReverseRouter.route(on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(projectId, null))
    );
    modelAndView.addObject("collaborationOpportunitiesText", CollaborationOpportunitiesController.PAGE_NAME);
    modelAndView.addObject("collaborationOpportunitiesCompleted", collaborationOpportunitiesService.isComplete(
        projectContext.getProjectDetails()));

    modelAndView.addObject("decommissionedWellsUrl",
        ReverseRouter.route(on(DecommissionedWellController.class).viewWellsToBeDecommissioned(projectId, null))
    );
    modelAndView.addObject("decommissionedWellsText", DecommissionedWellController.SUMMARY_PAGE_NAME);
    modelAndView.addObject("decommissionedWellsCompleted", false);

    modelAndView.addObject("platformsFpsosUrl",
        ReverseRouter.route(on(PlatformsFpsosController.class).viewPlatformFpso(projectId, null))
    );
    modelAndView.addObject("platformsFpsosText", PlatformsFpsosController.SUMMARY_PAGE_NAME);
    modelAndView.addObject("platformsFpsosCompleted", platformsFpsosService.isComplete(projectDetails));

    modelAndView.addObject("subseaInfrastructureUrl",
        ReverseRouter.route(on(SubseaInfrastructureController.class).getSubseaStructures(projectId, null))
    );
    modelAndView.addObject("subseaInfrastructureText", SubseaInfrastructureController.SUMMARY_PAGE_NAME);
    modelAndView.addObject("subseaInfrastructureCompleted", subseaInfrastructureService.isComplete(
        projectDetails
    ));

    modelAndView.addObject("integratedRigUrl",
        ReverseRouter.route(on(IntegratedRigController.class).getIntegratedRigs(projectId, null))
    );
    modelAndView.addObject("integratedRigText", IntegratedRigController.SUMMARY_PAGE_NAME);
    modelAndView.addObject("integratedRigCompleted", integratedRigService.isComplete(
        projectDetails
    ));

    modelAndView.addObject("reviewAndSubmitLink",
        ReverseRouter.route(on(SubmitProjectController.class).getProjectSummary(projectId, null))
    );

    return modelAndView;
  }

}
