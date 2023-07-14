package uk.co.ogauthority.pathfinder.model.enums.project.tasks;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import java.util.stream.Stream;
import uk.co.ogauthority.pathfinder.controller.project.OverviewController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.infrastructure.AwardedContractController;
import uk.co.ogauthority.pathfinder.controller.project.campaigninformation.CampaignInformationController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.infrastructure.InfrastructureCollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.controller.project.commissionedwell.CommissionedWellController;
import uk.co.ogauthority.pathfinder.controller.project.decommissionedpipeline.DecommissionedPipelineController;
import uk.co.ogauthority.pathfinder.controller.project.decommissioningschedule.DecommissioningScheduleController;
import uk.co.ogauthority.pathfinder.controller.project.integratedrig.IntegratedRigController;
import uk.co.ogauthority.pathfinder.controller.project.location.ProjectLocationController;
import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;
import uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule.PlugAbandonmentScheduleController;
import uk.co.ogauthority.pathfinder.controller.project.projectcontributor.ProjectContributorsController;
import uk.co.ogauthority.pathfinder.controller.project.projectinformation.ProjectInformationController;
import uk.co.ogauthority.pathfinder.controller.project.selectoperator.ChangeProjectOperatorController;
import uk.co.ogauthority.pathfinder.controller.project.setup.ProjectSetupController;
import uk.co.ogauthority.pathfinder.controller.project.subseainfrastructure.SubseaInfrastructureController;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.controller.project.workplanprojectcontributor.ForwardWorkPlanProjectContributorsController;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.campaigninformation.CampaignInformationService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityModelService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.commissionedwell.CommissionedWellScheduleService;
import uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline.DecommissionedPipelineService;
import uk.co.ogauthority.pathfinder.service.project.decommissioningschedule.DecommissioningScheduleService;
import uk.co.ogauthority.pathfinder.service.project.integratedrig.IntegratedRigService;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.overview.OverviewFormSectionService;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosService;
import uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule.PlugAbandonmentScheduleService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsFormSectionService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.selectoperator.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.subseainfrastructure.SubseaInfrastructureService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.workoplanprojectcontribution.ForwardWorkPlanProjectContributorFormSectionService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderService;

/**
 * An enum to encapsulate a task list section, to be used when generating the task list for a given project.
 */
public enum ProjectTask implements GeneralPurposeProjectTask {

  OVERVIEW(
      OverviewController.PAGE_NAME,
      OverviewController.class,
      OverviewFormSectionService.class,
      Set.of(ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN),
      5,
      Set.of(UserToProjectRelationship.CONTRIBUTOR),
      false
  ),
  PROJECT_OPERATOR(
      ChangeProjectOperatorController.PAGE_NAME,
      ChangeProjectOperatorController.class,
      SelectOperatorService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      10,
      Set.of(UserToProjectRelationship.OPERATOR)),
  PROJECT_INFORMATION(
      ProjectInformationController.PAGE_NAME,
      ProjectInformationController.class,
      ProjectInformationService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      20,
      Set.of(UserToProjectRelationship.OPERATOR)),
  PROJECT_LOCATION(
      ProjectLocationController.PAGE_NAME,
      ProjectLocationController.class,
      ProjectLocationService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      30,
      Set.of(UserToProjectRelationship.OPERATOR)),
  PROJECT_SETUP(
      ProjectSetupController.PAGE_NAME,
      ProjectSetupController.class,
      ProjectSetupService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      40,
      Set.of(UserToProjectRelationship.OPERATOR)),
  PROJECT_CONTRIBUTORS(
      ProjectContributorsController.PAGE_NAME,
      ProjectContributorsController.class,
      ProjectContributorsFormSectionService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      45,
      Set.of(UserToProjectRelationship.OPERATOR)),
  UPCOMING_TENDERS(
      UpcomingTendersController.PAGE_NAME,
      UpcomingTendersController.class,
      UpcomingTenderService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      50,
      Set.of(UserToProjectRelationship.OPERATOR, UserToProjectRelationship.CONTRIBUTOR)),
  AWARDED_CONTRACTS(
      AwardedContractController.PAGE_NAME,
      AwardedContractController.class,
      AwardedContractService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      60,
      Set.of(UserToProjectRelationship.OPERATOR, UserToProjectRelationship.CONTRIBUTOR)),
  COLLABORATION_OPPORTUNITIES(
      InfrastructureCollaborationOpportunitiesController.PAGE_NAME,
      InfrastructureCollaborationOpportunitiesController.class,
      InfrastructureCollaborationOpportunitiesService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      70,
      Set.of(UserToProjectRelationship.OPERATOR, UserToProjectRelationship.CONTRIBUTOR)),
  CAMPAIGN_INFORMATION(
      CampaignInformationController.PAGE_NAME,
      CampaignInformationController.class,
      CampaignInformationService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      75,
      Set.of(UserToProjectRelationship.OPERATOR)),
  DECOMMISSIONING_SCHEDULE(
      DecommissioningScheduleController.PAGE_NAME,
      DecommissioningScheduleController.class,
      DecommissioningScheduleService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      80,
      Set.of(UserToProjectRelationship.OPERATOR)),
  WELLS(
      PlugAbandonmentScheduleController.TASK_LIST_NAME,
      PlugAbandonmentScheduleController.class,
      PlugAbandonmentScheduleService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      90,
      Set.of(UserToProjectRelationship.OPERATOR)),
  PLATFORM_FPSO(
      PlatformsFpsosController.TASK_LIST_NAME,
      PlatformsFpsosController.class,
      PlatformsFpsosService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      100,
      Set.of(UserToProjectRelationship.OPERATOR)),
  INTEGRATED_RIGS(
      IntegratedRigController.TASK_LIST_NAME,
      IntegratedRigController.class,
      IntegratedRigService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      110,
      Set.of(UserToProjectRelationship.OPERATOR)),
  SUBSEA_INFRASTRUCTURE(
      SubseaInfrastructureController.TASK_LIST_NAME,
      SubseaInfrastructureController.class,
      SubseaInfrastructureService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      120,
      Set.of(UserToProjectRelationship.OPERATOR)),
  PIPELINES(
      DecommissionedPipelineController.TASK_LIST_NAME,
      DecommissionedPipelineController.class,
      DecommissionedPipelineService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      130,
      Set.of(UserToProjectRelationship.OPERATOR)),
  WORK_PLAN_PROJECT_CONTRIBUTORS(
      ForwardWorkPlanProjectContributorsController.PAGE_NAME,
      ForwardWorkPlanProjectContributorsController.class,
      ForwardWorkPlanProjectContributorFormSectionService.class,
      Set.of(ProjectType.FORWARD_WORK_PLAN),
      10,
      Set.of(UserToProjectRelationship.OPERATOR)),
  COMMISSIONED_WELLS(
      CommissionedWellController.TASK_LIST_NAME,
      CommissionedWellController.class,
      CommissionedWellScheduleService.class,
      Set.of(ProjectType.INFRASTRUCTURE),
      140,
      Set.of(UserToProjectRelationship.OPERATOR)
  ),
  WORK_PLAN_UPCOMING_TENDERS(
      ForwardWorkPlanUpcomingTenderController.PAGE_NAME,
      ForwardWorkPlanUpcomingTenderController.class,
      ForwardWorkPlanUpcomingTenderService.class,
      Set.of(ProjectType.FORWARD_WORK_PLAN),
      20,
      Set.of(UserToProjectRelationship.OPERATOR, UserToProjectRelationship.CONTRIBUTOR)),
  WORK_PLAN_COLLABORATION_OPPORTUNITIES(
      ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME,
      ForwardWorkPlanCollaborationOpportunityController.class,
      ForwardWorkPlanCollaborationOpportunityService.class,
      Set.of(ProjectType.FORWARD_WORK_PLAN),
      30,
      Set.of(UserToProjectRelationship.OPERATOR, UserToProjectRelationship.CONTRIBUTOR)),
  WORK_PLAN_AWARDED_CONTRACTS(
      ForwardWorkPlanAwardedContractSetupController.PAGE_NAME,
      ForwardWorkPlanAwardedContractSetupController.class,
      ForwardWorkPlanAwardedContractService.class,
      Set.of(ProjectType.FORWARD_WORK_PLAN),
      25,
      Set.of(UserToProjectRelationship.OPERATOR, UserToProjectRelationship.CONTRIBUTOR))
  ;

  private final String displayName;
  private final Class controllerClass;
  private final Class<? extends ProjectFormSectionService> serviceClass;
  private final Set<ProjectType> relatedProjectTypes;
  private final int displayOrder;
  private final Set<UserToProjectRelationship> permittedUserRelationships;
  private final boolean showTag;

  ProjectTask(String displayName,
              Class controllerClass,
              Class<? extends ProjectFormSectionService> serviceClass,
              Set<ProjectType> relatedProjectTypes,
              int displayOrder,
              Set<UserToProjectRelationship> permittedUserRelationships) {
    this(
        displayName,
        controllerClass,
        serviceClass,
        relatedProjectTypes,
        displayOrder,
        permittedUserRelationships,
        true
    );
  }

  ProjectTask(String displayName,
              Class controllerClass,
              Class<? extends ProjectFormSectionService> serviceClass,
              Set<ProjectType> relatedProjectTypes,
              int displayOrder,
              Set<UserToProjectRelationship> permittedUserRelationships,
              boolean showTag) {
    this.displayName = displayName;
    this.controllerClass = controllerClass;
    this.serviceClass = serviceClass;
    this.relatedProjectTypes = relatedProjectTypes;
    this.displayOrder = displayOrder;
    this.permittedUserRelationships = permittedUserRelationships;
    this.showTag = showTag;
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
  public Set<ProjectType> getRelatedProjectTypes() {
    return relatedProjectTypes;
  }

  @Override
  public boolean getShowTag() {
    return showTag;
  }

  @Override
  public String getTaskLandingPageRoute(Project project) {
    var projectId = project.getId();
    switch (this) {
      case PROJECT_OPERATOR:
        return ReverseRouter.route(on(ChangeProjectOperatorController.class).changeOperator(null, projectId, null));
      case PROJECT_INFORMATION:
        return ReverseRouter.route(on(ProjectInformationController.class).getProjectInformation(projectId, null));
      case PROJECT_LOCATION:
        return ReverseRouter.route(on(ProjectLocationController.class).getLocationDetails(projectId, null));
      case PROJECT_SETUP:
        return ReverseRouter.route(on(ProjectSetupController.class).getProjectSetup(projectId, null));
      case UPCOMING_TENDERS:
        return ReverseRouter.route(on(UpcomingTendersController.class).viewUpcomingTenders(projectId, null));
      case AWARDED_CONTRACTS:
        return ReverseRouter.route(on(AwardedContractController.class).viewAwardedContracts(projectId, null));
      case COLLABORATION_OPPORTUNITIES:
        return ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
            .viewCollaborationOpportunities(projectId, null));
      case CAMPAIGN_INFORMATION:
        return ReverseRouter.route(on(CampaignInformationController.class).getCampaignInformation(projectId, null));
      case DECOMMISSIONING_SCHEDULE:
        return ReverseRouter.route(on(DecommissioningScheduleController.class).getDecommissioningSchedule(projectId, null));
      case WELLS:
        return ReverseRouter.route(on(PlugAbandonmentScheduleController.class).viewPlugAbandonmentSchedules(projectId, null));
      case PLATFORM_FPSO:
        return ReverseRouter.route(on(PlatformsFpsosController.class).viewPlatformsFpsos(projectId, null));
      case INTEGRATED_RIGS:
        return ReverseRouter.route(on(IntegratedRigController.class).viewIntegratedRigs(projectId, null));
      case SUBSEA_INFRASTRUCTURE:
        return ReverseRouter.route(on(SubseaInfrastructureController.class).viewSubseaStructures(projectId, null));
      case PIPELINES:
        return ReverseRouter.route(on(DecommissionedPipelineController.class).viewPipelines(projectId, null));
      case WORK_PLAN_UPCOMING_TENDERS:
        return ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).getUpcomingTenderSetup(
            projectId,
            null,
            null
        ));
      case WORK_PLAN_COLLABORATION_OPPORTUNITIES:
        return ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class).getCollaborationOpportunitySetup(
            projectId,
            null,
            null
        ));
      case COMMISSIONED_WELLS:
        return ReverseRouter.route(on(CommissionedWellController.class).viewWellsToCommission(projectId, null));
      case PROJECT_CONTRIBUTORS:
        return ReverseRouter.route(on(ProjectContributorsController.class).renderProjectContributorsForm(projectId, null));
      case WORK_PLAN_PROJECT_CONTRIBUTORS:
        return ReverseRouter.route(on(ForwardWorkPlanProjectContributorsController.class).renderProjectContributors(projectId, null));
      case OVERVIEW:
        return ReverseRouter.route(on(OverviewController.class).getOverview(projectId, null));
      case WORK_PLAN_AWARDED_CONTRACTS:
        return ReverseRouter.route(on(ForwardWorkPlanAwardedContractSetupController.class).getAwardedContractSetup(projectId, null, null));
      default:
        return "";
    }
  }

  public Set<UserToProjectRelationship> getPermittedUserRelationships() {
    return permittedUserRelationships;
  }
}
