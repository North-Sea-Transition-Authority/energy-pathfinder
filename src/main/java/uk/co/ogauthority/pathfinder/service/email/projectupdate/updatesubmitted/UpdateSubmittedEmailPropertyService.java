package uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;

@Service
public class UpdateSubmittedEmailPropertyService {

  private final List<UpdateSubmittedEmailPropertyProvider> updateSubmittedEmailPropertyProviders;

  private final EmailLinkService emailLinkService;

  @Autowired
  public UpdateSubmittedEmailPropertyService(
      List<UpdateSubmittedEmailPropertyProvider> updateSubmittedEmailPropertyProviders,
      EmailLinkService emailLinkService
  ) {
    this.updateSubmittedEmailPropertyProviders = updateSubmittedEmailPropertyProviders;
    this.emailLinkService = emailLinkService;
  }

  public ProjectUpdateEmailProperties getUpdateSubmittedEmailProperties(ProjectDetail projectDetail) {

    final var updateSubmittedEmailPropertyProvider = updateSubmittedEmailPropertyProviders
        .stream()
        .filter(updateSubmittedEmailPropertyProviderService ->
            updateSubmittedEmailPropertyProviderService.getSupportedProjectType().equals(projectDetail.getProjectType())
        )
        .findFirst();

    // if a specific project type implementation is available get the email properties from that service. Otherwise,
    // fall back to the generic update submitted email properties.
    if (updateSubmittedEmailPropertyProvider.isPresent()) {
      return updateSubmittedEmailPropertyProvider.get().getUpdateSubmittedEmailProperties(projectDetail);
    } else {

      final var projectManagementUrl = emailLinkService.generateProjectManagementUrl(projectDetail.getProject());

      return new ProjectUpdateEmailProperties(projectManagementUrl);
    }
  }
}
