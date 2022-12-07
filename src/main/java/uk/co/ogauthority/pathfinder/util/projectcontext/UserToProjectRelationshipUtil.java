package uk.co.ogauthority.pathfinder.util.projectcontext;

import java.util.Set;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;

public class UserToProjectRelationshipUtil {

  private UserToProjectRelationshipUtil() {
    throw new IllegalStateException("UserToProjectRelationshipUtil is a util class and should not be instantiated");
  }

  public static boolean canAccessProjectTask(ProjectTask task, Set<UserToProjectRelationship> relationships) {
    return task.getPermittedUserRelationships()
        .stream()
        .anyMatch(relationships::contains);
  }
}
