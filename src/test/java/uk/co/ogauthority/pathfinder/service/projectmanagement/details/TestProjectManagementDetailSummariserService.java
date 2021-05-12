package uk.co.ogauthority.pathfinder.service.projectmanagement.details;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.ProjectManagementDetailView;

@Service
public class TestProjectManagementDetailSummariserService implements ProjectManagementDetailService {

  @Override
  public ProjectType getSupportedProjectType() {
    return null;
  }

  @Override
  public String getTemplatePath() {
    return null;
  }

  @Override
  public ProjectManagementDetailView getManagementDetailView(ProjectDetail projectDetail) {
    return null;
  }
}
