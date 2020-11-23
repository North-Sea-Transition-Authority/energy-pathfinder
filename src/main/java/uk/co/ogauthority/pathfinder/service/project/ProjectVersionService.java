package uk.co.ogauthority.pathfinder.service.project;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.dto.project.ProjectVersionDto;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;

@Service
public class ProjectVersionService {

  private final ProjectDetailsRepository projectDetailsRepository;

  @Autowired
  public ProjectVersionService(ProjectDetailsRepository projectDetailsRepository) {
    this.projectDetailsRepository = projectDetailsRepository;
  }

  public Map<Integer, Instant> getProjectVersions(Project project) {
    return projectDetailsRepository.getProjectVersionDtos(project.getId())
        .stream()
        .collect(Collectors.toMap(ProjectVersionDto::getVersion, ProjectVersionDto::getSubmittedInstant));
  }
}
