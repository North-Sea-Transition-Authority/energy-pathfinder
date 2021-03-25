package uk.co.ogauthority.pathfinder.service.projectupdate;

import java.util.Set;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;

public class RegulatorProjectUpdateContext extends ProjectUpdateContext {

  public RegulatorProjectUpdateContext(ProjectUpdateContext projectUpdateContext) {
    this(projectUpdateContext.getProjectDetails(),
         projectUpdateContext.getProjectPermissions(),
         projectUpdateContext.getUserAccount());
  }

  public RegulatorProjectUpdateContext(ProjectDetail projectDetails,
                                       Set<ProjectPermission> projectPermissions,
                                       AuthenticatedUserAccount userAccount) {
    super(projectDetails, projectPermissions, userAccount);
  }
}
