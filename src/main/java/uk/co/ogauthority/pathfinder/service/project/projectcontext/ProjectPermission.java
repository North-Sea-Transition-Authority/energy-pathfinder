package uk.co.ogauthority.pathfinder.service.project.projectcontext;

import java.util.Collections;
import java.util.List;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;

/**
 * Actions a user can perform on a project.
 * Which of these a user has is decided by the {@link ProjectContextService}.
 */
public enum ProjectPermission {
  EDIT(Collections.singletonList(UserPrivilege.PATHFINDER_PROJECT_CREATE)),
  SUBMIT(Collections.singletonList(UserPrivilege.PATHFINDER_PROJECT_CREATE)),
  VIEW(Collections.singletonList(UserPrivilege.PATHFINDER_PROJECT_VIEWER)),
  PROVIDE_ASSESSMENT(Collections.singletonList(UserPrivilege.PATHFINDER_PROJECT_ADMINISTRATOR)),
  PROVIDE_UPDATE(Collections.singletonList(UserPrivilege.PATHFINDER_PROJECT_CREATE)),
  REQUEST_UPDATE(Collections.singletonList(UserPrivilege.PATHFINDER_PROJECT_ADMINISTRATOR)),
  TRANSFER(Collections.singletonList(UserPrivilege.PATHFINDER_PROJECT_ADMINISTRATOR)),
  ARCHIVE(List.of(UserPrivilege.PATHFINDER_PROJECT_CREATE, UserPrivilege.PATHFINDER_PROJECT_ADMINISTRATOR));

  private final List<UserPrivilege> userPrivileges;

  ProjectPermission(List<UserPrivilege> userPrivileges) {
    this.userPrivileges = userPrivileges;
  }

  public List<UserPrivilege> getUserPrivileges() {
    return userPrivileges;
  }

  public boolean hasPrivilege(UserPrivilege userPrivilege) {
    return userPrivileges.contains(userPrivilege);
  }
}
