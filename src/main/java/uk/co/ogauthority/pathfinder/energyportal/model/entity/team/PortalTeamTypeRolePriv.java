package uk.co.ogauthority.pathfinder.energyportal.model.entity.team;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;

@Immutable
@Entity
@Table(name = "portal_resource_type_role_priv")
public class PortalTeamTypeRolePriv {

  @EmbeddedId
  private PortalTeamTypeRolePrivId portalTeamTypeRolePrivId;

  @ManyToOne
  @JoinColumn(name = "role_name", referencedColumnName = "role_name", insertable = false, updatable = false)
  @JoinColumn(name = "res_type", referencedColumnName = "res_type", insertable = false, updatable = false)
  private PortalTeamTypeRole portalTeamTypeRole;

  // Maps entry in EmbeddedId so must be marked not updatable and not insertable
  @Column(name = "default_system_priv", insertable = false, updatable = false)
  private String privilege;

  public PortalTeamTypeRolePrivId getPortalTeamTypeRolePrivId() {
    return portalTeamTypeRolePrivId;
  }

  public PortalTeamTypeRole getPortalTeamTypeRole() {
    return portalTeamTypeRole;
  }

}
