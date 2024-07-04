package uk.co.ogauthority.pathfinder.model.entity.projectupdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "no_update_notifications")
public class NoUpdateNotification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Integer id;

  @OneToOne
  @JoinColumn(name = "project_update_id")
  private ProjectUpdate projectUpdate;

  @Lob
  @Column(name = "supply_chain_reason", columnDefinition = "CLOB")
  private String supplyChainReason;

  @Lob
  @Column(name = "regulator_reason", columnDefinition = "CLOB")
  private String regulatorReason;

  public Integer getId() {
    return id;
  }

  public ProjectUpdate getProjectUpdate() {
    return projectUpdate;
  }

  public void setProjectUpdate(ProjectUpdate projectUpdate) {
    this.projectUpdate = projectUpdate;
  }

  public String getSupplyChainReason() {
    return supplyChainReason;
  }

  public void setSupplyChainReason(String supplyChainReason) {
    this.supplyChainReason = supplyChainReason;
  }

  public String getRegulatorReason() {
    return regulatorReason;
  }

  public void setRegulatorReason(String regulatorReason) {
    this.regulatorReason = regulatorReason;
  }
}
