package uk.co.ogauthority.pathfinder.service.projectassessment;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;

@Service
public class ProjectAssessmentContextService {

  private final ProjectContextService projectContextService;
  private final ProjectAssessmentService projectAssessmentService;

  @Autowired
  public ProjectAssessmentContextService(ProjectContextService projectContextService,
                                         ProjectAssessmentService projectAssessmentService) {
    this.projectContextService = projectContextService;
    this.projectAssessmentService = projectAssessmentService;
  }

  public ProjectAssessmentContext buildProjectAssessmentContext(ProjectDetail detail,
                                                                AuthenticatedUserAccount user,
                                                                Set<ProjectStatus> statusCheck,
                                                                Set<ProjectPermission> permissionCheck) {
    if (projectAssessmentService.hasProjectBeenAssessed(detail)) {
      throw new AccessDeniedException(
          String.format(
              "Project detail with id %s has already been assessed",
              detail.getId())
      );
    }

    var projectContext = projectContextService.buildProjectContext(
        detail,
        user,
        statusCheck,
        permissionCheck
    );

    return new ProjectAssessmentContext(
        projectContext.getProjectDetails(),
        projectContext.getProjectPermissions(),
        projectContext.getUserAccount()
    );
  }
}
