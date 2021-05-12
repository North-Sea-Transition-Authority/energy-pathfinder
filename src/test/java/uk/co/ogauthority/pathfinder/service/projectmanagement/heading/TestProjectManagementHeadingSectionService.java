package uk.co.ogauthority.pathfinder.service.projectmanagement.heading;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class TestProjectManagementHeadingSectionService implements ProjectManagementHeadingService {

  @Override
  public ProjectType getSupportedProjectType() {
    return null;
  }

  @Override
  public String getHeadingText(ProjectDetail projectDetail) {
    return null;
  }

  @Override
  public String getCaptionText(ProjectDetail projectDetail) {
    return null;
  }
}
