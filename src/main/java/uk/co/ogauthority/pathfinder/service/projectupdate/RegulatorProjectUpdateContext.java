package uk.co.ogauthority.pathfinder.service.projectupdate;

import java.util.Set;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;

public class RegulatorProjectUpdateContext extends ProjectUpdateContext {

  public RegulatorProjectUpdateContext(ProjectUpdateContext projectUpdateContext) {
    this(projectUpdateContext.getProjectDetails(),
        projectUpdateContext.getProjectPermissions(),
        projectUpdateContext.getUserAccount(),
        projectUpdateContext.getUserToProjectRelationships());
  }

  public RegulatorProjectUpdateContext(ProjectDetail projectDetails,
                                       Set<ProjectPermission> projectPermissions,
                                       AuthenticatedUserAccount userAccount,
                                       Set<UserToProjectRelationship> relationships) {
    super(projectDetails, projectPermissions, userAccount, relationships);
  }
}
