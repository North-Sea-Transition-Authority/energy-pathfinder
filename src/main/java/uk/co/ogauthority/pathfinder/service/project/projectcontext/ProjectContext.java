package uk.co.ogauthority.pathfinder.service.project.projectcontext;

import java.util.Set;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;

/**
 * Provide contextual information about the relationship between a user and project.
 */
public class ProjectContext {

  private  final ProjectDetail projectDetails;

  private final Set<ProjectPermission> projectPermissions;

  private final AuthenticatedUserAccount userAccount;

  private final Set<UserToProjectRelationship> userToProjectRelationships;

  public ProjectContext(ProjectDetail projectDetails,
                        Set<ProjectPermission> projectPermissions,
                        AuthenticatedUserAccount userAccount,
                        Set<UserToProjectRelationship> userToProjectRelationships) {
    this.projectDetails = projectDetails;
    this.projectPermissions = projectPermissions;
    this.userAccount = userAccount;
    this.userToProjectRelationships = userToProjectRelationships;
  }

  public ProjectDetail getProjectDetails() {
    return projectDetails;
  }

  public Set<ProjectPermission> getProjectPermissions() {
    return projectPermissions;
  }

  public AuthenticatedUserAccount getUserAccount() {
    return userAccount;
  }

  public Set<UserToProjectRelationship> getUserToProjectRelationships() {
    return userToProjectRelationships;
  }
}
