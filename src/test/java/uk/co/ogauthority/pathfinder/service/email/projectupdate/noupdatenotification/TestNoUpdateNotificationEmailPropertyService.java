package uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.NoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@Service
public class TestNoUpdateNotificationEmailPropertyService implements NoUpdateNotificationEmailPropertyProvider {

  @Override
  public ProjectType getSupportedProjectType() {
    return null;
  }

  @Override
  public NoUpdateNotificationEmailProperties getNoUpdateNotificationEmailProperties(ProjectDetail projectDetail,
                                                                                    String noUpdateReason) {
    return null;
  }
}
