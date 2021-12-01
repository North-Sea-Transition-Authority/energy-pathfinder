package uk.co.ogauthority.pathfinder.service.project.cancellation;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@Service
public class TestCancelProjectVersionService implements CancelProjectVersionService {

  @Override
  public ProjectType getSupportedProjectType() {
    return null;
  }

  @Override
  public boolean isCancellable(ProjectDetail projectDetail) {
    return false;
  }
}
