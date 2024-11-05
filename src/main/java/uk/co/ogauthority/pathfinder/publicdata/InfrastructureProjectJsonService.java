package uk.co.ogauthority.pathfinder.publicdata;

import com.google.common.collect.Streams;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationBlockRepository;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;

@Service
class InfrastructureProjectJsonService {

  private static final Comparator<InfrastructureProjectJson> INFRASTRUCTURE_PROJECT_JSON_COMPARATOR =
      Comparator.<InfrastructureProjectJson, String>
              comparing(infrastructureProjectJson -> infrastructureProjectJson.operatorName().toLowerCase())
          .thenComparing(infrastructureProjectJson -> infrastructureProjectJson.title().toLowerCase());

  private final ProjectDetailsRepository projectDetailsRepository;
  private final ProjectOperatorRepository projectOperatorRepository;
  private final ProjectInformationRepository projectInformationRepository;
  private final ProjectLocationRepository projectLocationRepository;
  private final ProjectLocationBlockRepository projectLocationBlockRepository;

  InfrastructureProjectJsonService(
      ProjectDetailsRepository projectDetailsRepository,
      ProjectOperatorRepository projectOperatorRepository,
      ProjectInformationRepository projectInformationRepository,
      ProjectLocationRepository projectLocationRepository,
      ProjectLocationBlockRepository projectLocationBlockRepository
  ) {
    this.projectDetailsRepository = projectDetailsRepository;
    this.projectOperatorRepository = projectOperatorRepository;
    this.projectInformationRepository = projectInformationRepository;
    this.projectLocationRepository = projectLocationRepository;
    this.projectLocationBlockRepository = projectLocationBlockRepository;
  }

  List<InfrastructureProjectJson> getPublishedInfrastructureProjects() {
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

    return allProjectDetails
        .stream()
        .map(projectDetail ->
            InfrastructureProjectJson.from(
                projectDetail,
                projectOperatorByProjectDetailId.get(projectDetail.getId()),
                projectInformationByProjectDetailId.get(projectDetail.getId()),
                projectLocationByProjectDetailId.get(projectDetail.getId()),
                projectLocationBlocksByProjectDetailId.get(projectDetail.getId())
            )
        )
        .sorted(INFRASTRUCTURE_PROJECT_JSON_COMPARATOR)
        .toList();
  }
}
