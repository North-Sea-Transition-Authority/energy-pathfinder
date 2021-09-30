package uk.co.ogauthority.pathfinder.model.entity.projecttransfer;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnit;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "project_transfers")
public class ProjectTransfer extends ProjectDetailEntity {

  @ManyToOne
  @JoinColumn(name = "from_operator_org_grp_id")
  private PortalOrganisationGroup fromOrganisationGroup;

  @ManyToOne
  @JoinColumn(name = "to_operator_org_grp_id")
  private PortalOrganisationGroup toOrganisationGroup;

  @Lob
  @Column(name = "transfer_reason", columnDefinition = "CLOB")
  private String transferReason;

  @Column(name = "publish_as_project_operator")
  private Boolean isPublishedAsOperator;

  @ManyToOne
  @JoinColumn(name = "publishable_org_unit_id")
  private PortalOrganisationUnit publishableOrganisationUnit;

  @Column(name = "transferred_datetime")
  private Instant transferredInstant;

  private Integer transferredByWuaId;

  public PortalOrganisationGroup getFromOrganisationGroup() {
    return fromOrganisationGroup;
  }

  public void setFromOrganisationGroup(
      PortalOrganisationGroup fromOrganisationGroup) {
    this.fromOrganisationGroup = fromOrganisationGroup;
  }

  public PortalOrganisationGroup getToOrganisationGroup() {
    return toOrganisationGroup;
  }

  public void setToOrganisationGroup(
      PortalOrganisationGroup toOrganisationGroup) {
    this.toOrganisationGroup = toOrganisationGroup;
  }

  public String getTransferReason() {
    return transferReason;
  }

  public void setTransferReason(String transferReason) {
    this.transferReason = transferReason;
  }

  public Instant getTransferredInstant() {
    return transferredInstant;
  }

  public void setTransferredInstant(Instant transferredInstant) {
    this.transferredInstant = transferredInstant;
  }

  public Integer getTransferredByWuaId() {
    return transferredByWuaId;
  }

  public void setTransferredByWuaId(Integer transferredByWuaId) {
    this.transferredByWuaId = transferredByWuaId;
  }

  public Boolean isPublishedAsOperator() {
    return isPublishedAsOperator;
  }

  public void setIsPublishedAsOperator(Boolean isPublishedAsOperator) {
    this.isPublishedAsOperator = isPublishedAsOperator;
  }

  public PortalOrganisationUnit getPublishableOrganisationUnit() {
    return publishableOrganisationUnit;
  }

  public void setPublishableOrganisationUnit(PortalOrganisationUnit publishableOrganisationUnit) {
    this.publishableOrganisationUnit = publishableOrganisationUnit;
  }
}
