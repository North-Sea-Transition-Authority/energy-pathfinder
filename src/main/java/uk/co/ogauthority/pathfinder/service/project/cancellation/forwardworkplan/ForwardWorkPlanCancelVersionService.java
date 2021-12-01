package uk.co.ogauthority.pathfinder.service.project.cancellation.forwardworkplan;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.cancellation.CancelProjectVersionService;

@Service
public class ForwardWorkPlanCancelVersionService implements CancelProjectVersionService {

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.FORWARD_WORK_PLAN;
  }

  @Override
  public boolean isCancellable(ProjectDetail projectDetail) {
    return projectDetail.getProjectType().equals(getSupportedProjectType())
        && projectDetail.getStatus().equals(ProjectStatus.DRAFT)
        && !projectDetail.isFirstVersion();
  }
}
