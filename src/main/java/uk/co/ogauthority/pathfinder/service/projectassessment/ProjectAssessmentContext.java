package uk.co.ogauthority.pathfinder.service.projectassessment;

import java.util.Set;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;

public class ProjectAssessmentContext extends ProjectContext {

  public ProjectAssessmentContext(ProjectDetail projectDetails,
                                  Set<ProjectPermission> projectPermissions,
                                  AuthenticatedUserAccount userAccount,
                                  Set<UserToProjectRelationship> relationships) {
    super(projectDetails, projectPermissions, userAccount, relationships);
  }
}
