package uk.co.ogauthority.pathfinder.service.project;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetails;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;

/**
 * Service to get project and project details.
 */
@Service
public class ProjectService {

  private final ProjectDetailsRepository projectDetailsRepository;

  @Autowired
  public ProjectService(ProjectDetailsRepository projectDetailsRepository) {
    this.projectDetailsRepository = projectDetailsRepository;
  }

  /**
   * Get the current projectDetail for a given projectId.
   * @param projectId the id of the Project associated with the detail
   * @return
   */
  public Optional<ProjectDetails> getLatestDetail(Integer projectId) {
    return projectDetailsRepository.findByProjectIdAndIsCurrentVersionIsTrue(projectId);
  }

}
