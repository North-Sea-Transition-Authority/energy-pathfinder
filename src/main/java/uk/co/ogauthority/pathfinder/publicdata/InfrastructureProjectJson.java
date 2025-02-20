package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.InfrastructureAwardedContract;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignProject;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissioningschedule.DecommissioningSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.integratedrig.IntegratedRig;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentWell;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTenderFileLink;

record InfrastructureProjectJson(
    Integer id,
    InfrastructureProjectDetailsJson details,
    ContactJson contact,
    QuarterYearJson firstProductionDate,
    InfrastructureProjectLocationJson location,
    Set<InfrastructureProjectUpcomingTenderJson> upcomingTenders,
    Set<AwardedContractJson> awardedContracts,
    Set<CollaborationOpportunityJson> collaborationOpportunities,
    InfrastructureProjectCampaignJson campaign,
    Set<InfrastructureProjectWellScheduleJson> wellCommissioningSchedules,
    InfrastructureProjectDecommissioningScheduleJson decommissioningSchedule,
    Set<InfrastructureProjectWellScheduleJson> wellDecommissioningSchedules,
    Set<InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson> platformOrFpsosToBeDecommissioned,
    Set<InfrastructureProjectIntegratedRigToBeDecommissionedJson> integratedRigsToBeDecommissioned,
    Set<InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson> subseaInfrastructuresToBeDecommissioned,
    Set<InfrastructureProjectPipelineToBeDecommissionedJson> pipelinesToBeDecommissioned,
    LocalDateTime submittedOn
) {

  static InfrastructureProjectJson from(
      ProjectDetail projectDetail,
      ProjectOperator projectOperator,
      ProjectInformation projectInformation,
      ProjectLocation projectLocation,
      Collection<ProjectLocationBlock> projectLocationBlocks,
      Map<UpcomingTender, UpcomingTenderFileLink> upcomingTenderToFileLink,
      Collection<InfrastructureAwardedContract> infrastructureAwardedContracts,
      Map<InfrastructureCollaborationOpportunity, InfrastructureCollaborationOpportunityFileLink>
          infrastructureCollaborationOpportunityToFileLink,
      CampaignInformation campaignInformation,
      Collection<CampaignProject> campaignProjects,
      Map<CommissionedWellSchedule, Collection<CommissionedWell>> commissionedWellScheduleToWells,
      DecommissioningSchedule decommissioningSchedule,
      Map<PlugAbandonmentSchedule, Collection<PlugAbandonmentWell>> plugAbandonmentScheduleToWells,
      Collection<PlatformFpso> platformFpsos,
      Collection<IntegratedRig> integratedRigs,
      Collection<SubseaInfrastructure> subseaInfrastructures,
      Collection<DecommissionedPipeline> decommissionedPipelines
  ) {
    var id = projectDetail.getProject().getId();

    var details = InfrastructureProjectDetailsJson.from(projectOperator, projectInformation);

    var contact = ContactJson.from(projectInformation);

    var firstProductionDate =
        projectInformation.getFirstProductionDateQuarter() != null && projectInformation.getFirstProductionDateYear() != null
            ? QuarterYearJson.from(projectInformation.getFirstProductionDateQuarter(), projectInformation.getFirstProductionDateYear())
            : null;

    var location = projectLocation != null ? InfrastructureProjectLocationJson.from(projectLocation, projectLocationBlocks) : null;

    var upcomingTenders = upcomingTenderToFileLink != null
        ? upcomingTenderToFileLink.entrySet().stream()
            .map(entry -> InfrastructureProjectUpcomingTenderJson.from(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet())
        : null;

    var awardedContracts = infrastructureAwardedContracts != null
        ? infrastructureAwardedContracts.stream().map(AwardedContractJson::from).collect(Collectors.toSet())
        : null;

    var collaborationOpportunities = infrastructureCollaborationOpportunityToFileLink != null
        ? infrastructureCollaborationOpportunityToFileLink.entrySet().stream()
            .map(entry -> CollaborationOpportunityJson.from(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet())
        : null;

    var campaign = campaignInformation != null
        ? InfrastructureProjectCampaignJson.from(campaignInformation, campaignProjects)
        : null;

    var wellCommissioningSchedules = commissionedWellScheduleToWells != null
        ? commissionedWellScheduleToWells.entrySet().stream()
            .map(entry -> InfrastructureProjectWellScheduleJson.from(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet())
        : null;

    var decommissioningScheduleJson = decommissioningSchedule != null
        ? InfrastructureProjectDecommissioningScheduleJson.from(decommissioningSchedule)
        : null;

    var wellDecommissioningSchedules = plugAbandonmentScheduleToWells != null
        ? plugAbandonmentScheduleToWells.entrySet().stream()
            .map(entry -> InfrastructureProjectWellScheduleJson.from(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet())
        : null;

    var platformOrFpsosToBeDecommissioned = platformFpsos != null
        ? platformFpsos.stream().map(InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson::from).collect(Collectors.toSet())
        : null;

    var integratedRigsToBeDecommissioned = integratedRigs != null
        ? integratedRigs.stream().map(InfrastructureProjectIntegratedRigToBeDecommissionedJson::from).collect(Collectors.toSet())
        : null;

    var subseaInfrastructuresToBeDecommissioned = subseaInfrastructures != null
        ? subseaInfrastructures.stream().map(InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson::from)
            .collect(Collectors.toSet())
        : null;

    var pipelinesToBeDecommissioned = decommissionedPipelines != null
        ? decommissionedPipelines.stream().map(InfrastructureProjectPipelineToBeDecommissionedJson::from)
            .collect(Collectors.toSet())
        : null;

    var submittedOn = LocalDateTime.ofInstant(projectDetail.getSubmittedInstant(), ZoneId.systemDefault());

    return new InfrastructureProjectJson(
        id,
        details,
        contact,
        firstProductionDate,
        location,
        upcomingTenders,
        awardedContracts,
        collaborationOpportunities,
        campaign,
        wellCommissioningSchedules,
        decommissioningScheduleJson,
        wellDecommissioningSchedules,
        platformOrFpsosToBeDecommissioned,
        integratedRigsToBeDecommissioned,
        subseaInfrastructuresToBeDecommissioned,
        pipelinesToBeDecommissioned,
        submittedOn
    );
  }
}
