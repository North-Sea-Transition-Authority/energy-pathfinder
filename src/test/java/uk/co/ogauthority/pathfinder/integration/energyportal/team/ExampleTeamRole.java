package uk.co.ogauthority.pathfinder.integration.energyportal.team;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum ExampleTeamRole {
  ROLE_WITH_PRIVILEGE("PrivilegeName", "WITH_PRIVILEGE_TITLE", "WITH_PRIVILEGE_DESC", 0, 999),
  ROLE_WITHOUT_PRIVILEGE(null, "NO_PRIVILEGE_TITLE", "NO_PRIVILEGE_DESC", 1, 999);

  private final String exampleRolePriv;
  private final String title;
  private final String desc;
  private final int minMembers;
  private final int maxMembers;

  ExampleTeamRole(String exampleRolePrivilege, String title, String desc, int minMembers, int maxMembers) {
    this.exampleRolePriv = exampleRolePrivilege;
    this.title = title;
    this.desc = desc;
    this.minMembers = minMembers;
    this.maxMembers = maxMembers;
  }

  public static Collection<String> getAllRoleNames(){
    return Arrays.stream(ExampleTeamRole.values())
        .map(Enum::name)
        .collect(Collectors.toList());
  }

  public String getExampleRolePrivilege() {
    return exampleRolePriv;
  }

  public int getMinMembers() {
    return minMembers;
  }

  public int getMaxMembers() {
    return maxMembers;
  }

  public String getTitle() {
    return title;
  }

  public String getDesc() {
    return desc;
  }
}

