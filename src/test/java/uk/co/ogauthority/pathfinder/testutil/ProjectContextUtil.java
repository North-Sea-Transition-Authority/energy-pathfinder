package uk.co.ogauthority.pathfinder.testutil;

import java.util.Set;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;

public class ProjectContextUtil {


  public static ProjectContext getContext_withAllPermissions(ProjectDetail detail, AuthenticatedUserAccount user) {
    return new ProjectContext(
        detail,
        Set.of(ProjectPermission.values()),
        user,
        Set.of(
            UserToProjectRelationship.REGULATOR,
            UserToProjectRelationship.OPERATOR,
            UserToProjectRelationship.CONTRIBUTOR
        ));
  }
}
