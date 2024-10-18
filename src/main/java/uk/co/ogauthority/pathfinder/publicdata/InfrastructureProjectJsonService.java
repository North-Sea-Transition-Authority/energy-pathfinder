package uk.co.ogauthority.pathfinder.publicdata;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;

@Service
class InfrastructureProjectJsonService {

  public final ProjectDetailsRepository projectDetailsRepository;

  InfrastructureProjectJsonService(ProjectDetailsRepository projectDetailsRepository) {
    this.projectDetailsRepository = projectDetailsRepository;
  }

  List<InfrastructureProjectJson> getPublishedInfrastructureProjects() {
    return projectDetailsRepository.getAllPublishedProjectDetailsByProjectType(ProjectType.INFRASTRUCTURE)
        .stream()
        .map(InfrastructureProjectJson::from)
        .toList();
  }
}
