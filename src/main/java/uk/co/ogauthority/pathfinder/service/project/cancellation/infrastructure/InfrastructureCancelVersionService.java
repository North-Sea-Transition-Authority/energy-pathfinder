package uk.co.ogauthority.pathfinder.service.project.cancellation.infrastructure;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.cancellation.CancelProjectVersionService;

@Service
public class InfrastructureCancelVersionService implements CancelProjectVersionService {

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.INFRASTRUCTURE;
  }

  @Override
  public boolean isCancellable(ProjectDetail projectDetail) {
    return projectDetail.getProjectType().equals(getSupportedProjectType())
        && projectDetail.getStatus().equals(ProjectStatus.DRAFT);
  }
}
