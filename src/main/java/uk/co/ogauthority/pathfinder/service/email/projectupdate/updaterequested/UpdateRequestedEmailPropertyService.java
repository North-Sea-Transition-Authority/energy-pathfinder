package uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.ProjectUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.LinkService;

@Service
public class UpdateRequestedEmailPropertyService {

  private final List<UpdateRequestedEmailPropertyProvider> updateRequestedEmailPropertyProviders;

  private final LinkService linkService;

  @Autowired
  public UpdateRequestedEmailPropertyService(
      List<UpdateRequestedEmailPropertyProvider> updateRequestedEmailPropertyProviders,
      LinkService linkService
  ) {
    this.updateRequestedEmailPropertyProviders = updateRequestedEmailPropertyProviders;
    this.linkService = linkService;
  }

  public ProjectUpdateRequestedEmailProperties getUpdateRequestedEmailProperties(ProjectDetail projectDetail,
                                                                                 String updateReason,
                                                                                 String deadlineDate) {

    final var updateRequestedEmailPropertyProvider = updateRequestedEmailPropertyProviders
        .stream()
        .filter(updateRequestedEmailPropertyProviderService ->
            updateRequestedEmailPropertyProviderService.getSupportedProjectType().equals(projectDetail.getProjectType())
        )
        .findFirst();

    // if a specific project type implementation is available get the email properties from that service. Otherwise,
    // fall back to the generic update requested email properties.
    if (updateRequestedEmailPropertyProvider.isPresent()) {
      return updateRequestedEmailPropertyProvider.get().getUpdateRequestedEmailProperties(
          projectDetail,
          updateReason,
          deadlineDate
      );
    } else {

      final var projectManagementUrl = linkService.generateProjectManagementUrl(projectDetail.getProject());

      return new ProjectUpdateRequestedEmailProperties(
          updateReason,
          deadlineDate,
          projectManagementUrl
      );
    }
  }
}
