package uk.co.ogauthority.pathfinder.publicdata;

import com.google.common.collect.Streams;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.InfrastructureAwardedContractRepository;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationBlockRepository;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderRepository;

@Service
class InfrastructureProjectJsonService {

  private final ProjectDetailsRepository projectDetailsRepository;
  private final ProjectOperatorRepository projectOperatorRepository;
  private final ProjectInformationRepository projectInformationRepository;
  private final ProjectLocationRepository projectLocationRepository;
  private final ProjectLocationBlockRepository projectLocationBlockRepository;
  private final UpcomingTenderRepository upcomingTenderRepository;
  private final InfrastructureAwardedContractRepository infrastructureAwardedContractRepository;
  private final InfrastructureCollaborationOpportunitiesRepository infrastructureCollaborationOpportunitiesRepository;

  InfrastructureProjectJsonService(
      ProjectDetailsRepository projectDetailsRepository,
      ProjectOperatorRepository projectOperatorRepository,
      ProjectInformationRepository projectInformationRepository,
      ProjectLocationRepository projectLocationRepository,
      ProjectLocationBlockRepository projectLocationBlockRepository,
      UpcomingTenderRepository upcomingTenderRepository,
      InfrastructureAwardedContractRepository infrastructureAwardedContractRepository,
      InfrastructureCollaborationOpportunitiesRepository infrastructureCollaborationOpportunitiesRepository
  ) {
    this.projectDetailsRepository = projectDetailsRepository;
    this.projectOperatorRepository = projectOperatorRepository;
    this.projectInformationRepository = projectInformationRepository;
    this.projectLocationRepository = projectLocationRepository;
    this.projectLocationBlockRepository = projectLocationBlockRepository;
    this.upcomingTenderRepository = upcomingTenderRepository;
    this.infrastructureAwardedContractRepository = infrastructureAwardedContractRepository;
    this.infrastructureCollaborationOpportunitiesRepository = infrastructureCollaborationOpportunitiesRepository;
  }

  Set<InfrastructureProjectJson> getPublishedInfrastructureProjects() {
    var allProjectDetails = projectDetailsRepository.getAllPublishedProjectDetailsByProjectType(ProjectType.INFRASTRUCTURE);

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

    var upcomingTendersByProjectDetailId = Streams.stream(upcomingTenderRepository.findAll())
        .collect(Collectors.groupingBy(upcomingTender -> upcomingTender.getProjectDetail().getId()));

    var infrastructureAwardedContractsByProjectDetailId = Streams.stream(infrastructureAwardedContractRepository.findAll())
        .collect(Collectors.groupingBy(
            infrastructureAwardedContract -> infrastructureAwardedContract.getProjectDetail().getId()));

    var infrastructureCollaborationOpportunitiesByProjectDetailId =
        Streams.stream(infrastructureCollaborationOpportunitiesRepository.findAll())
            .collect(Collectors.groupingBy(
                infrastructureCollaborationOpportunity -> infrastructureCollaborationOpportunity.getProjectDetail().getId()));

    return allProjectDetails
        .stream()
        .map(projectDetail ->
            InfrastructureProjectJson.from(
                projectDetail,
                projectOperatorByProjectDetailId.get(projectDetail.getId()),
                projectInformationByProjectDetailId.get(projectDetail.getId()),
                projectLocationByProjectDetailId.get(projectDetail.getId()),
                projectLocationBlocksByProjectDetailId.get(projectDetail.getId()),
                upcomingTendersByProjectDetailId.get(projectDetail.getId()),
                infrastructureAwardedContractsByProjectDetailId.get(projectDetail.getId()),
                infrastructureCollaborationOpportunitiesByProjectDetailId.get(projectDetail.getId())
            )
        )
        .collect(Collectors.toSet());
  }
}
