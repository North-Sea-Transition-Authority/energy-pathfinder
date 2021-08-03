package uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.NoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;

@Service
public class NoUpdateNotificationEmailPropertyService {

  private final List<NoUpdateNotificationEmailPropertyProvider> noUpdateNotificationEmailPropertyProviderServices;

  private final EmailLinkService emailLinkService;

  @Autowired
  public NoUpdateNotificationEmailPropertyService(
      List<NoUpdateNotificationEmailPropertyProvider> noUpdateNotificationEmailPropertyProviderServices,
      EmailLinkService emailLinkService
  ) {
    this.noUpdateNotificationEmailPropertyProviderServices = noUpdateNotificationEmailPropertyProviderServices;
    this.emailLinkService = emailLinkService;
  }

  public NoUpdateNotificationEmailProperties getNoUpdateNotificationEmailProperties(ProjectDetail projectDetail,
                                                                                    String noUpdateReason) {
    final var noUpdateEmailPropertyService = noUpdateNotificationEmailPropertyProviderServices
        .stream()
        .filter(noUpdateNotificationEmailPropertyProvider ->
            noUpdateNotificationEmailPropertyProvider.getSupportedProjectType().equals(projectDetail.getProjectType())
        )
        .findFirst();

    // if a specific project type implementation is available get the email properties from that service. Otherwise,
    // fall back to the generic no update notification email properties.
    if (noUpdateEmailPropertyService.isPresent()) {
      return noUpdateEmailPropertyService.get().getNoUpdateNotificationEmailProperties(projectDetail, noUpdateReason);
    } else {

      final var projectManagementUrl = emailLinkService.generateProjectManagementUrl(projectDetail.getProject());

      return new NoUpdateNotificationEmailProperties(
          projectManagementUrl,
          noUpdateReason
      );
    }
  }
}
