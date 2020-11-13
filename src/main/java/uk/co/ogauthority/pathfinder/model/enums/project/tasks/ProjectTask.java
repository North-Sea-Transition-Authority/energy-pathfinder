package uk.co.ogauthority.pathfinder.model.enums.project.tasks;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.stream.Stream;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardedContractController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.CollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.controller.project.decommissionedpipeline.DecommissionedPipelineController;
import uk.co.ogauthority.pathfinder.controller.project.decommissionedwell.DecommissionedWellController;
import uk.co.ogauthority.pathfinder.controller.project.integratedrig.IntegratedRigController;
import uk.co.ogauthority.pathfinder.controller.project.location.ProjectLocationController;
import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;
import uk.co.ogauthority.pathfinder.controller.project.projectinformation.ProjectInformationController;
import uk.co.ogauthority.pathfinder.controller.project.selectoperator.SelectProjectOperatorController;
import uk.co.ogauthority.pathfinder.controller.project.setup.ProjectSetupController;
import uk.co.ogauthority.pathfinder.controller.project.subseainfrastructure.SubseaInfrastructureController;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline.DecommissionedPipelineService;
import uk.co.ogauthority.pathfinder.service.project.decommissionedwell.DecommissionedWellService;
import uk.co.ogauthority.pathfinder.service.project.integratedrig.IntegratedRigService;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.selectoperator.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.subseainfrastructure.SubseaInfrastructureService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;

/**
 * An enum to encapsulate a task list section, to be used when generating the task list for a given project.
 */
public enum ProjectTask implements GeneralPurposeProjectTask {

  PROJECT_OPERATOR(
      "Project operator",
      SelectProjectOperatorController.class,
      SelectOperatorService.class,
      1
  ),
  PROJECT_INFORMATION(
      ProjectInformationController.PAGE_NAME,
      ProjectInformationController.class,
      ProjectInformationService.class,
      20
  ),
  PROJECT_LOCATION(
      ProjectLocationController.PAGE_NAME,
      ProjectLocationController.class,
      ProjectLocationService.class,
      30
  ),
  PROJECT_SETUP(
      ProjectSetupController.PAGE_NAME,
      ProjectSetupController.class,
      ProjectSetupService.class,
      40
  ),
  UPCOMING_TENDERS(
      UpcomingTendersController.PAGE_NAME,
      UpcomingTendersController.class,
      UpcomingTenderService.class,
      50
  ),
  AWARDED_CONTRACTS(
    AwardedContractController.PAGE_NAME,
    AwardedContractController.class,
    AwardedContractService.class,
    60
  ),
  COLLABORATION_OPPORTUNITIES(
      CollaborationOpportunitiesController.PAGE_NAME,
      CollaborationOpportunitiesController.class,
      CollaborationOpportunitiesService.class,
      70
  ),
  WELLS(
      DecommissionedWellController.FORM_PAGE_NAME,
      DecommissionedWellController.class,
      DecommissionedWellService.class,
      80
  ),
  PLATFORM_FPSO(
      PlatformsFpsosController.SUMMARY_PAGE_NAME,
      PlatformsFpsosController.class,
      PlatformsFpsosService.class,
      90
  ),
  SUBSEA_INFRASTRUCTURE(
      SubseaInfrastructureController.SUMMARY_PAGE_NAME,
      SubseaInfrastructureController.class,
      SubseaInfrastructureService.class,
      100
  ),
  INTEGRATED_RIGS(
      IntegratedRigController.SUMMARY_PAGE_NAME,
      IntegratedRigController.class,
      IntegratedRigService.class,
      110
  ),
  PIPELINES(
      DecommissionedPipelineController.TASK_LIST_NAME,
      DecommissionedPipelineController.class,
      DecommissionedPipelineService.class,
      120
  );

  private final String displayName;
  private final Class<?> controllerClass;
  private final Class<? extends ProjectFormSectionService> serviceClass;
  private final int displayOrder;

  ProjectTask(String displayName, Class<?> controllerClass,
              Class<? extends ProjectFormSectionService> serviceClass, int displayOrder) {
    this.displayName = displayName;
    this.controllerClass = controllerClass;
    this.serviceClass = serviceClass;
    this.displayOrder = displayOrder;
  }

  public static Stream<ProjectTask> stream() {
    return Stream.of(ProjectTask.values());
  }

  @Override
  public Class<? extends ProjectFormSectionService> getServiceClass() {
    return serviceClass;
  }

  @Override
  public Class getControllerClass() {
    return controllerClass;
  }

  @Override
  public int getDisplayOrder() {
    return displayOrder;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String getTaskLandingPageRoute(Project project) {
    var projectId = project.getId();
    switch (this) {
      case PROJECT_OPERATOR:
        return ReverseRouter.route(on(SelectProjectOperatorController.class).selectOperator(null));
      case PROJECT_INFORMATION:
        return ReverseRouter.route(on(ProjectInformationController.class).getProjectInformation(projectId, null));
      case PROJECT_LOCATION:
        return ReverseRouter.route(on(ProjectLocationController.class).getLocationDetails(projectId, null));
      default: //TODO PAT-298 fill out
        return "";
    }
  }
}
