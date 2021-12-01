package uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@Service
public class TestUpdateSubmittedEmailPropertyService implements UpdateSubmittedEmailPropertyProvider {

  @Override
  public ProjectType getSupportedProjectType() {
    return null;
  }

  @Override
  public ProjectUpdateEmailProperties getUpdateSubmittedEmailProperties(ProjectDetail projectDetail) {
    return null;
  }
}
