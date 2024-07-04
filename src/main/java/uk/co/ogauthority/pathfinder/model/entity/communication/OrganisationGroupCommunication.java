package uk.co.ogauthority.pathfinder.model.entity.communication;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;

@Entity
@Table(name = "org_group_communications")
public class OrganisationGroupCommunication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne
  @JoinColumn(name = "communication_id")
  private Communication communication;

  @ManyToOne
  @JoinColumn(name = "organisation_group_id")
  private PortalOrganisationGroup organisationGroup;

  public Communication getCommunication() {
    return communication;
  }

  public void setCommunication(Communication communication) {
    this.communication = communication;
  }

  public PortalOrganisationGroup getOrganisationGroup() {
    return organisationGroup;
  }

  public void setOrganisationGroup(PortalOrganisationGroup organisationGroup) {
    this.organisationGroup = organisationGroup;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrganisationGroupCommunication organisationGroupCommunication = (OrganisationGroupCommunication) o;
    return Objects.equals(id, organisationGroupCommunication.id)
        && Objects.equals(communication, organisationGroupCommunication.communication)
        && Objects.equals(organisationGroup, organisationGroupCommunication.getOrganisationGroup());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, communication, organisationGroup);
  }
}
