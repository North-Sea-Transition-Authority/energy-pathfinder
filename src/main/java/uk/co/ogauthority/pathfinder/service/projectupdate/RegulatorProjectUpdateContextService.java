package uk.co.ogauthority.pathfinder.service.projectupdate;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;

@Service
public class RegulatorProjectUpdateContextService extends ProjectUpdateContextService {

  private final ProjectService projectService;
  private final RegulatorUpdateRequestService regulatorUpdateRequestService;

  @Autowired
  public RegulatorProjectUpdateContextService(
      ProjectContextService projectContextService,
      ProjectUpdateService projectUpdateService,
      ProjectService projectService,
      RegulatorUpdateRequestService regulatorUpdateRequestService) {
    super(projectContextService, projectUpdateService);
    this.projectService = projectService;
    this.regulatorUpdateRequestService = regulatorUpdateRequestService;
  }

  @Override
  public RegulatorProjectUpdateContext buildProjectUpdateContext(ProjectDetail detail,
                                                                 AuthenticatedUserAccount user,
                                                                 Set<ProjectStatus> statusCheck,
                                                                 Set<ProjectPermission> permissionCheck,
                                                                 Set<ProjectType> allowedProjectTypes,
                                                                 boolean allowProjectContributors) {
    // This check to get the latest submitted detail is a safety check, in case
    // detail is not the latest submitted detail.
    var latestSubmittedDetail = projectService.getLatestSubmittedDetailOrError(detail.getProject().getId());

    if (regulatorUpdateRequestService.hasUpdateBeenRequested(latestSubmittedDetail)) {
      throw new AccessDeniedException(
          String.format(
              "An update has already been requested on latest submitted project detail id with %s",
              latestSubmittedDetail.getId()
          )
      );
    }

    var projectUpdateContext = super.buildProjectUpdateContext(
        detail,
        user,
        statusCheck,
        permissionCheck,
        allowedProjectTypes,
        allowProjectContributors
    );

    return new RegulatorProjectUpdateContext(projectUpdateContext);
  }
}
