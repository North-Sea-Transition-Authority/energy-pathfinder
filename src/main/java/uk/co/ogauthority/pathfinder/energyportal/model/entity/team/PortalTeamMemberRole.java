package uk.co.ogauthority.pathfinder.energyportal.model.entity.team;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Immutable
@Entity
@Table(name = "portal_res_memb_current_roles")
public class PortalTeamMemberRole {

  @EmbeddedId
  private PortalTeamMemberRoleId portalTeamMemberRoleId;

  @ManyToOne
  @JoinColumn(name = "person_id", referencedColumnName = "person_id", insertable = false, updatable = false)
  @JoinColumn(name = "res_id", referencedColumnName = "res_id", insertable = false, updatable = false)
  private PortalTeamMember portalTeamMember;

  @ManyToOne
  @JoinColumn(name = "res_type", referencedColumnName = "res_type", insertable = false, updatable = false)
  @JoinColumn(name = "role_name", referencedColumnName = "role_name", insertable = false, updatable = false)
  private PortalTeamTypeRole portalTeamTypeRole;

  public PortalTeamMemberRoleId getPortalTeamMemberRoleId() {
    return portalTeamMemberRoleId;
  }

  public PortalTeamTypeRole getPortalTeamTypeRole() {
    return portalTeamTypeRole;
  }
}
