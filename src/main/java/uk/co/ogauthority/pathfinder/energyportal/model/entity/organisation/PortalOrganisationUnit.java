package uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

@Entity(name = "portal_organisation_units")
@Immutable
public class PortalOrganisationUnit implements SearchSelectable {

  @Id
  private int ouId;

  private String name;

  @ManyToOne
  @JoinColumn(name = "org_grp_id")
  private PortalOrganisationGroup portalOrganisationGroup;

  private boolean active;

  public PortalOrganisationUnit() {
  }

  @VisibleForTesting
  public PortalOrganisationUnit(int ouId,
                                String name,
                                boolean active,
                                PortalOrganisationGroup portalOrganisationGroup) {
    this.ouId = ouId;
    this.name = name;
    this.active = active;
    this.portalOrganisationGroup = portalOrganisationGroup;
  }

  public int getOuId() {
    return ouId;
  }

  public String getName() {
    return name;
  }

  public PortalOrganisationGroup getPortalOrganisationGroup() {
    return portalOrganisationGroup;
  }

  public boolean isActive() {
    return active;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PortalOrganisationUnit)) {
      return false;
    }
    PortalOrganisationUnit that = (PortalOrganisationUnit) o;
    return ouId == that.ouId
        && name.equals(that.name)
        && Objects.equals(portalOrganisationGroup, that.portalOrganisationGroup)
        && Objects.equals(active, that.active);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        ouId,
        name,
        portalOrganisationGroup,
        active
    );
  }

  @Override
  public String getSelectionId() {
    return String.valueOf(ouId);
  }

  @Override
  public String getSelectionText() {
    return getName();
  }
}
