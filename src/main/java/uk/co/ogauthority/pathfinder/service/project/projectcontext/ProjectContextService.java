package uk.co.ogauthority.pathfinder.service.project.projectcontext;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;

@Service
public class ProjectContextService {

  private final ProjectService projectService;
  private final ProjectOperatorService projectOperatorService;

  @Autowired
  public ProjectContextService(ProjectService projectService,
                               ProjectOperatorService projectOperatorService) {
    this.projectService = projectService;
    this.projectOperatorService = projectOperatorService;
  }


  public ProjectContext getProjectContext(ProjectDetail detail, AuthenticatedUserAccount user) {
    Set<ProjectPermission> projectPermissions = new java.util.HashSet<>();

    if (detail.getStatus().equals(ProjectStatus.DRAFT)) {
      projectPermissions.add(ProjectPermission.VIEW_TASK_LIST);
    }

    if (user.hasPrivilege(UserPrivilege.PATHFINDER_PROJECT_CREATE)) {
      projectPermissions.add(ProjectPermission.EDIT);
      projectPermissions.add(ProjectPermission.SUBMIT);
    }

    //TODO PAT-82 Operator view privilege

    return new ProjectContext(
        detail,
        projectPermissions,
        user
    );
  }

  public ProjectDetail getProjectDetailsOrError(Integer projectId) {
    return projectService.getLatestDetail(projectId).orElseThrow(() -> new PathfinderEntityNotFoundException(
        String.format("Unable to find project detail for project id  %d", projectId))
    );
  }


  /**
   * Check permissions of user in relation to the supplied ProjectDetail.
   *
   * @return true if the user is in the team the project is related to
   * or true if the user is a regulator and can view any project.
   */
  public boolean userIsInProjectTeamOrRegulator(ProjectDetail detail,
                                                AuthenticatedUserAccount user) {
    return projectOperatorService.isUserInProjectTeamOrRegulator(detail, user);
  }

  public boolean projectStatusMatches(ProjectDetail detail,
                                      ProjectStatus status) {
    return detail.getStatus().equals(status);
  }
}
