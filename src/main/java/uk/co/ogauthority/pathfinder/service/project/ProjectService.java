package uk.co.ogauthority.pathfinder.service.project;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
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
   * @return an optional of the latest ProjectDetail for the provided projectId
   */
  public Optional<ProjectDetail> getLatestDetail(Integer projectId) {
    return projectDetailsRepository.findByProjectIdAndIsCurrentVersionIsTrue(projectId);
  }

  public ProjectDetail getDetailOrError(Integer projectId, Integer version) {
    return projectDetailsRepository.findByProjectIdAndVersion(projectId, version)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Unable to find project detail with version %s for project with id %s", version, projectId)));
  }

  public ProjectDetail createNewProjectDetailVersion(ProjectDetail fromDetail, AuthenticatedUserAccount userAccount) {

    fromDetail.setIsCurrentVersion(false);

    final var newProjectDetail = new ProjectDetail(
        fromDetail.getProject(),
        ProjectStatus.DRAFT,
        userAccount.getWuaId(),
        fromDetail.getVersion() + 1,
        true
    );

    projectDetailsRepository.save(fromDetail);
    return projectDetailsRepository.save(newProjectDetail);
  }

  public void setProjectDetailStatus(ProjectDetail projectDetail, ProjectStatus status) {
    projectDetail.setStatus(status);
    projectDetailsRepository.save(projectDetail);
  }
}
