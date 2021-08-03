package uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.ProjectUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@Service
public class TestUpdateRequestedEmailPropertyService implements UpdateRequestedEmailPropertyProvider {

  @Override
  public ProjectType getSupportedProjectType() {
    return null;
  }

  @Override
  public ProjectUpdateRequestedEmailProperties getUpdateRequestedEmailProperties(ProjectDetail projectDetail,
                                                                                 String updateReason,
                                                                                 String deadlineDate) {
    return null;
  }
}
