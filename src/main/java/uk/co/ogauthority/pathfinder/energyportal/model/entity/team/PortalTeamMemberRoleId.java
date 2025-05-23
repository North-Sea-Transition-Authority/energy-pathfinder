package uk.co.ogauthority.pathfinder.energyportal.model.entity.team;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PortalTeamMemberRoleId implements Serializable {

  @Column(name = "person_id")
  private int personId;

  @Column(name = "res_id")
  private int resId;

  @Column(name = "res_type")
  private String resType;

  @Column(name = "role_name")
  private String roleName;

  public int getPersonId() {
    return personId;
  }

  public int getResId() {
    return resId;
  }

  public String getResType() {
    return resType;
  }

  public String getRoleName() {
    return roleName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PortalTeamMemberRoleId)) {
      return false;
    }
    PortalTeamMemberRoleId that = (PortalTeamMemberRoleId) o;
    return personId == that.personId
        && resId == that.resId
        && Objects.equals(resType, that.resType)
        && Objects.equals(roleName, that.roleName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(personId, resId, resType, roleName);
  }
}
