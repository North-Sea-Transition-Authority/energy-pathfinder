package uk.co.ogauthority.pathfinder.publicdata;

import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentWell;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.InfrastructureAwardedContractRepository;
import uk.co.ogauthority.pathfinder.repository.project.campaigninformation.CampaignInformationRepository;
import uk.co.ogauthority.pathfinder.repository.project.campaigninformation.CampaignProjectRepository;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.repository.project.commissionedwell.CommissionedWellRepository;
import uk.co.ogauthority.pathfinder.repository.project.commissionedwell.CommissionedWellScheduleRepository;
import uk.co.ogauthority.pathfinder.repository.project.decommissionedpipeline.DecommissionedPipelineRepository;
import uk.co.ogauthority.pathfinder.repository.project.decommissioningschedule.DecommissioningScheduleRepository;
import uk.co.ogauthority.pathfinder.repository.project.integratedrig.IntegratedRigRepository;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationBlockRepository;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.repository.project.platformsfpsos.PlatformFpsoRepository;
import uk.co.ogauthority.pathfinder.repository.project.plugabandonmentschedule.PlugAbandonmentScheduleRepository;
import uk.co.ogauthority.pathfinder.repository.project.plugabandonmentschedule.PlugAbandonmentWellRepository;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.repository.project.subseainfrastructure.SubseaInfrastructureRepository;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderFileLinkRepository;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

@Service
class InfrastructureProjectJsonService {

  private final ProjectDetailsRepository projectDetailsRepository;
  private final ProjectOperatorRepository projectOperatorRepository;
  private final ProjectInformationRepository projectInformationRepository;
  private final ProjectLocationRepository projectLocationRepository;
  private final ProjectLocationBlockRepository projectLocationBlockRepository;
  private final UpcomingTenderRepository upcomingTenderRepository;
  private final UpcomingTenderFileLinkRepository upcomingTenderFileLinkRepository;
  private final InfrastructureAwardedContractRepository infrastructureAwardedContractRepository;
  private final InfrastructureCollaborationOpportunitiesRepository infrastructureCollaborationOpportunitiesRepository;
  private final InfrastructureCollaborationOpportunityFileLinkRepository infrastructureCollaborationOpportunityFileLinkRepository;
  private final CampaignInformationRepository campaignInformationRepository;
  private final CampaignProjectRepository campaignProjectRepository;
  private final CommissionedWellScheduleRepository commissionedWellScheduleRepository;
  private final CommissionedWellRepository commissionedWellRepository;
  private final DecommissioningScheduleRepository decommissioningScheduleRepository;
  private final PlugAbandonmentScheduleRepository plugAbandonmentScheduleRepository;
  private final PlugAbandonmentWellRepository plugAbandonmentWellRepository;
  private final PlatformFpsoRepository platformFpsoRepository;
  private final IntegratedRigRepository integratedRigRepository;
  private final SubseaInfrastructureRepository subseaInfrastructureRepository;
  private final DecommissionedPipelineRepository decommissionedPipelineRepository;

  InfrastructureProjectJsonService(
      ProjectDetailsRepository projectDetailsRepository,
      ProjectOperatorRepository projectOperatorRepository,
      ProjectInformationRepository projectInformationRepository,
      ProjectLocationRepository projectLocationRepository,
      ProjectLocationBlockRepository projectLocationBlockRepository,
      UpcomingTenderRepository upcomingTenderRepository,
      UpcomingTenderFileLinkRepository upcomingTenderFileLinkRepository,
      InfrastructureAwardedContractRepository infrastructureAwardedContractRepository,
      InfrastructureCollaborationOpportunitiesRepository infrastructureCollaborationOpportunitiesRepository,
      InfrastructureCollaborationOpportunityFileLinkRepository infrastructureCollaborationOpportunityFileLinkRepository,
      CampaignInformationRepository campaignInformationRepository,
      CampaignProjectRepository campaignProjectRepository,
      CommissionedWellScheduleRepository commissionedWellScheduleRepository,
      CommissionedWellRepository commissionedWellRepository,
      DecommissioningScheduleRepository decommissioningScheduleRepository,
      PlugAbandonmentScheduleRepository plugAbandonmentScheduleRepository,
      PlugAbandonmentWellRepository plugAbandonmentWellRepository,
      PlatformFpsoRepository platformFpsoRepository,
      IntegratedRigRepository integratedRigRepository,
      SubseaInfrastructureRepository subseaInfrastructureRepository,
      DecommissionedPipelineRepository decommissionedPipelineRepository
  ) {
    this.projectDetailsRepository = projectDetailsRepository;
    this.projectOperatorRepository = projectOperatorRepository;
    this.projectInformationRepository = projectInformationRepository;
    this.projectLocationRepository = projectLocationRepository;
    this.projectLocationBlockRepository = projectLocationBlockRepository;
    this.upcomingTenderRepository = upcomingTenderRepository;
    this.upcomingTenderFileLinkRepository = upcomingTenderFileLinkRepository;
    this.infrastructureAwardedContractRepository = infrastructureAwardedContractRepository;
    this.infrastructureCollaborationOpportunitiesRepository = infrastructureCollaborationOpportunitiesRepository;
    this.infrastructureCollaborationOpportunityFileLinkRepository = infrastructureCollaborationOpportunityFileLinkRepository;
    this.campaignInformationRepository = campaignInformationRepository;
    this.campaignProjectRepository = campaignProjectRepository;
    this.commissionedWellScheduleRepository = commissionedWellScheduleRepository;
    this.commissionedWellRepository = commissionedWellRepository;
    this.decommissioningScheduleRepository = decommissioningScheduleRepository;
    this.plugAbandonmentScheduleRepository = plugAbandonmentScheduleRepository;
    this.plugAbandonmentWellRepository = plugAbandonmentWellRepository;
    this.platformFpsoRepository = platformFpsoRepository;
    this.integratedRigRepository = integratedRigRepository;
    this.subseaInfrastructureRepository = subseaInfrastructureRepository;
    this.decommissionedPipelineRepository = decommissionedPipelineRepository;
  }

  Set<InfrastructureProjectJson> getPublishedInfrastructureProjects() {
    var allProjectDetails =
        projectDetailsRepository.getAllPublishedProjectDetailsByProjectTypes(EnumSet.of(ProjectType.INFRASTRUCTURE));

    // TODO: When replatforming to use Postgres, switch to findAllByProjectDetail_IdIn. We can't do this with Oracle at the moment
    // due to the 1000 IN clause limit.
    var projectOperatorByProjectDetailId = Streams.stream(projectOperatorRepository.findAll())
        .collect(Collectors.toMap(projectOperator -> projectOperator.getProjectDetail().getId(), Function.identity()));

    var projectInformationByProjectDetailId = Streams.stream(projectInformationRepository.findAll())
        .collect(Collectors.toMap(projectInformation -> projectInformation.getProjectDetail().getId(), Function.identity()));

    var projectLocationByProjectDetailId = Streams.stream(projectLocationRepository.findAll())
        .collect(Collectors.toMap(projectLocation -> projectLocation.getProjectDetail().getId(), Function.identity()));

    var projectLocationBlocksByProjectDetailId = Streams.stream(projectLocationBlockRepository.findAll())
        .collect(Collectors.groupingBy(projectLocationBlock -> projectLocationBlock.getProjectLocation().getProjectDetail().getId()));

    var fileLinkByUpcomingTenderId = Streams.stream(upcomingTenderFileLinkRepository.findAll())
        .collect(Collectors.toMap(fileLink -> fileLink.getUpcomingTender().getId(), Function.identity()));

    var upcomingTenderToFileLinkByProjectDetailId = Streams.stream(upcomingTenderRepository.findAll())
        .collect(
            Collectors.groupingBy(
                upcomingTender -> upcomingTender.getProjectDetail().getId(),
                StreamUtil.toMapNullValueFriendly(
                    Function.identity(),
                    upcomingTender -> fileLinkByUpcomingTenderId.get(upcomingTender.getId())
                )
            )
        );

    var infrastructureAwardedContractsByProjectDetailId = Streams.stream(infrastructureAwardedContractRepository.findAll())
        .collect(Collectors.groupingBy(
            infrastructureAwardedContract -> infrastructureAwardedContract.getProjectDetail().getId()));

    var fileLinkByInfrastructureCollaborationOpportunityId =
        Streams.stream(infrastructureCollaborationOpportunityFileLinkRepository.findAll())
            .collect(Collectors.toMap(fileLink -> fileLink.getCollaborationOpportunity().getId(), Function.identity()));

    var infrastructureCollaborationOpportunityToFileLinkByProjectDetailId =
        Streams.stream(infrastructureCollaborationOpportunitiesRepository.findAll())
            .collect(
                Collectors.groupingBy(
                    collaborationOpportunity -> collaborationOpportunity.getProjectDetail().getId(),
                    StreamUtil.toMapNullValueFriendly(
                        Function.identity(),
                        collaborationOpportunity -> fileLinkByInfrastructureCollaborationOpportunityId.get(collaborationOpportunity.getId())
                    )
                )
            );

    var campaignInformationByProjectDetailId = Streams.stream(campaignInformationRepository.findAll())
        .collect(Collectors.toMap(campaignInformation -> campaignInformation.getProjectDetail().getId(), Function.identity()));

    var campaignProjectsByProjectDetailId = Streams.stream(campaignProjectRepository.findAll())
        .collect(Collectors.groupingBy(campaignProject -> campaignProject.getCampaignInformation().getProjectDetail().getId()));

    var commissionedWellsByScheduleId = Streams.stream(commissionedWellRepository.findAll())
        .collect(Collectors.groupingBy(commissionedWell -> commissionedWell.getCommissionedWellSchedule().getId()));

    Map<Integer, Map<CommissionedWellSchedule, Collection<CommissionedWell>>> commissionedWellScheduleToWellsByProjectDetailId =
        Streams.stream(commissionedWellScheduleRepository.findAll())
            .collect(
                Collectors.groupingBy(
                    commissionedWellSchedule -> commissionedWellSchedule.getProjectDetail().getId(),
                    StreamUtil.toMapNullValueFriendly(
                        Function.identity(),
                        commissionedWellSchedule -> commissionedWellsByScheduleId.get(commissionedWellSchedule.getId())
                    )
                )
            );

    var decommissioningScheduleByProjectDetailId = Streams.stream(decommissioningScheduleRepository.findAll())
        .collect(Collectors.toMap(decommissioningSchedule -> decommissioningSchedule.getProjectDetail().getId(), Function.identity()));

    var plugAbandonmentWellsByScheduleId = Streams.stream(plugAbandonmentWellRepository.findAll())
        .collect(Collectors.groupingBy(plugAbandonmentWell -> plugAbandonmentWell.getPlugAbandonmentSchedule().getId()));

    Map<Integer, Map<PlugAbandonmentSchedule, Collection<PlugAbandonmentWell>>> plugAbandonmentScheduleToWellsByProjectDetailId =
        Streams.stream(plugAbandonmentScheduleRepository.findAll())
            .collect(
                Collectors.groupingBy(
                    plugAbandonmentSchedule -> plugAbandonmentSchedule.getProjectDetail().getId(),
                    StreamUtil.toMapNullValueFriendly(
                        Function.identity(),
                        plugAbandonmentSchedule -> plugAbandonmentWellsByScheduleId.get(plugAbandonmentSchedule.getId())
                    )
                )
            );

    var platformFpsosByProjectDetailId = Streams.stream(platformFpsoRepository.findAll())
        .collect(Collectors.groupingBy(platformFpso -> platformFpso.getProjectDetail().getId()));

    var integratedRigsByProjectDetailId = Streams.stream(integratedRigRepository.findAll())
        .collect(Collectors.groupingBy(integratedRig -> integratedRig.getProjectDetail().getId()));

    var subseaInfrastructuresByProjectDetailId = Streams.stream(subseaInfrastructureRepository.findAll())
        .collect(Collectors.groupingBy(subseaInfrastructure -> subseaInfrastructure.getProjectDetail().getId()));

    var decommissionedPipelinesByProjectDetailId = Streams.stream(decommissionedPipelineRepository.findAll())
        .collect(Collectors.groupingBy(decommissionedPipeline -> decommissionedPipeline.getProjectDetail().getId()));

    return allProjectDetails
        .stream()
        .map(projectDetail ->
            InfrastructureProjectJson.from(
                projectDetail,
                projectOperatorByProjectDetailId.get(projectDetail.getId()),
                projectInformationByProjectDetailId.get(projectDetail.getId()),
                projectLocationByProjectDetailId.get(projectDetail.getId()),
                projectLocationBlocksByProjectDetailId.get(projectDetail.getId()),
                upcomingTenderToFileLinkByProjectDetailId.get(projectDetail.getId()),
                infrastructureAwardedContractsByProjectDetailId.get(projectDetail.getId()),
                infrastructureCollaborationOpportunityToFileLinkByProjectDetailId.get(projectDetail.getId()),
                campaignInformationByProjectDetailId.get(projectDetail.getId()),
                campaignProjectsByProjectDetailId.get(projectDetail.getId()),
                commissionedWellScheduleToWellsByProjectDetailId.get(projectDetail.getId()),
                decommissioningScheduleByProjectDetailId.get(projectDetail.getId()),
                plugAbandonmentScheduleToWellsByProjectDetailId.get(projectDetail.getId()),
                platformFpsosByProjectDetailId.get(projectDetail.getId()),
                integratedRigsByProjectDetailId.get(projectDetail.getId()),
                subseaInfrastructuresByProjectDetailId.get(projectDetail.getId()),
                decommissionedPipelinesByProjectDetailId.get(projectDetail.getId())
            )
        )
        .collect(Collectors.toSet());
  }
}
