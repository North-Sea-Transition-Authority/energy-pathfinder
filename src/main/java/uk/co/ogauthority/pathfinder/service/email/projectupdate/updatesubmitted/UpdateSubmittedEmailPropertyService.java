package uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.LinkService;

@Service
public class UpdateSubmittedEmailPropertyService {

  private final List<UpdateSubmittedEmailPropertyProvider> updateSubmittedEmailPropertyProviders;

  private final LinkService linkService;

  @Autowired
  public UpdateSubmittedEmailPropertyService(
      List<UpdateSubmittedEmailPropertyProvider> updateSubmittedEmailPropertyProviders,
      LinkService linkService
  ) {
    this.updateSubmittedEmailPropertyProviders = updateSubmittedEmailPropertyProviders;
    this.linkService = linkService;
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

      final var projectManagementUrl = linkService.generateProjectManagementUrl(projectDetail.getProject());

      return new ProjectUpdateEmailProperties(projectManagementUrl);
    }
  }
}
