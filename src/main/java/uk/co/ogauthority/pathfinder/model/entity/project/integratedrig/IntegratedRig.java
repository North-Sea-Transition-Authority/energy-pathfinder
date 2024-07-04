package uk.co.ogauthority.pathfinder.model.entity.project.integratedrig;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigIntentionToReactivate;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigStatus;

@Entity
@Table(name = "integrated_rigs")
public class IntegratedRig extends ProjectDetailEntity {

  @ManyToOne
  @JoinColumn(name = "facility_id")
  private DevUkFacility facility;

  private String manualFacility;

  private String name;

  @Enumerated(EnumType.STRING)
  private IntegratedRigStatus status;

  @Enumerated(EnumType.STRING)
  private IntegratedRigIntentionToReactivate intentionToReactivate;

  public DevUkFacility getFacility() {
    return facility;
  }

  public void setFacility(DevUkFacility facility) {
    this.facility = facility;
  }

  public String getManualFacility() {
    return manualFacility;
  }

  public void setManualFacility(String manualFacility) {
    this.manualFacility = manualFacility;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public IntegratedRigStatus getStatus() {
    return status;
  }

  public void setStatus(IntegratedRigStatus status) {
    this.status = status;
  }

  public IntegratedRigIntentionToReactivate getIntentionToReactivate() {
    return intentionToReactivate;
  }

  public void setIntentionToReactivate(
      IntegratedRigIntentionToReactivate intentionToReactivate) {
    this.intentionToReactivate = intentionToReactivate;
  }
}
