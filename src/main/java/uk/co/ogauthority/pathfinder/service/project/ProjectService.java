package uk.co.ogauthority.pathfinder.service.project;

import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectRepository;

/**
 * Service to get project and project details.
 */
@Service
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectDetailsRepository projectDetailsRepository;

  @Autowired
  public ProjectService(ProjectRepository projectRepository,
                        ProjectDetailsRepository projectDetailsRepository) {
    this.projectRepository = projectRepository;
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

  public ProjectDetail getLatestDetailOrError(Integer projectId) {
    return getLatestDetail(projectId).orElseThrow(() ->
        new PathfinderEntityNotFoundException(
            String.format("Unable to find project detail for project with id %d", projectId)
        )
    );
  }

  public Optional<ProjectDetail> getLatestSubmittedDetail(Integer projectId) {
    return projectDetailsRepository.findByProjectIdAndIsLatestSubmittedVersion(projectId);
  }

  public ProjectDetail getLatestSubmittedDetailOrError(Integer projectId) {
    return getLatestSubmittedDetail(projectId).orElseThrow(() ->
        new PathfinderEntityNotFoundException(
            String.format("Unable to find latest submitted project detail for project with id %d", projectId)
        )
    );
  }

  public Optional<ProjectDetail> getDetail(Project project, Integer version) {
    return projectDetailsRepository.findByProjectIdAndVersion(project.getId(), version);
  }

  public ProjectDetail getDetailOrError(Integer projectId, Integer version) {
    return projectDetailsRepository.findByProjectIdAndVersion(projectId, version)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Unable to find project detail with version %s for project with id %s", version, projectId)));
  }

  public ProjectDetail createNewProjectDetailVersion(ProjectDetail fromDetail,
                                                     ProjectStatus newStatus,
                                                     AuthenticatedUserAccount userAccount) {

    fromDetail.setIsCurrentVersion(false);

    final var newProjectDetail = new ProjectDetail(
        fromDetail.getProject(),
        newStatus,
        userAccount.getWuaId(),
        fromDetail.getVersion() + 1,
        true,
        fromDetail.getProjectType()
    );

    if (newStatus.equals(ProjectStatus.QA)
        || newStatus.equals(ProjectStatus.PUBLISHED)
        || newStatus.equals(ProjectStatus.ARCHIVED)) {
      newProjectDetail.setSubmittedByWua(userAccount.getWuaId());
      newProjectDetail.setSubmittedInstant(Instant.now());
    }

    projectDetailsRepository.save(fromDetail);
    return projectDetailsRepository.save(newProjectDetail);
  }

  public void updateProjectDetailStatus(ProjectDetail projectDetail, ProjectStatus status) {
    projectDetail.setStatus(status);
    projectDetailsRepository.save(projectDetail);
  }

  public void updateProjectDetailIsCurrentVersion(ProjectDetail projectDetail, boolean isCurrentVersion) {
    projectDetail.setIsCurrentVersion(isCurrentVersion);
    projectDetailsRepository.save(projectDetail);
  }

  public void deleteProjectDetail(ProjectDetail projectDetail) {
    projectDetailsRepository.delete(projectDetail);
  }

  public void deleteProject(Project project) {
    projectRepository.delete(project);
  }

  public static boolean isInfrastructureProject(ProjectDetail projectDetail) {
    return ProjectType.INFRASTRUCTURE.equals(projectDetail.getProjectType());
  }
}
