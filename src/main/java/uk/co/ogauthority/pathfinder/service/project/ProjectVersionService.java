package uk.co.ogauthority.pathfinder.service.project;

import java.util.List;
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

  public List<ProjectVersionDto> getProjectVersionDtos(Project project) {
    return projectDetailsRepository.getProjectVersionDtos(project.getId());
  }
}
