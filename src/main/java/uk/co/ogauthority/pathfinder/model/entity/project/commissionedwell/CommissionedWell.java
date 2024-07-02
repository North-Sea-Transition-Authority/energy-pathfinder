package uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;
import uk.co.ogauthority.pathfinder.service.entityduplication.ChildEntity;

@Entity
@Table(name = "commissioned_wells")
public class CommissionedWell implements ChildEntity<Integer, CommissionedWellSchedule> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "commissioned_well_schedule_id")
  private CommissionedWellSchedule commissionedWellSchedule;

  @ManyToOne
  @JoinColumn(name = "wellbore_id")
  private Wellbore wellbore;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public CommissionedWellSchedule getCommissionedWellSchedule() {
    return commissionedWellSchedule;
  }

  public void setCommissionedWellSchedule(CommissionedWellSchedule commissionedWellSchedule) {
    this.commissionedWellSchedule = commissionedWellSchedule;
  }

  public Wellbore getWellbore() {
    return wellbore;
  }

  public void setWellbore(Wellbore wellbore) {
    this.wellbore = wellbore;
  }

  @Override
  public void clearId() {
    setId(null);
  }

  @Override
  public void setParent(CommissionedWellSchedule parentEntity) {
    setCommissionedWellSchedule(parentEntity);
  }

  @Override
  public CommissionedWellSchedule getParent() {
    return getCommissionedWellSchedule();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CommissionedWell)) {
      return false;
    }
    CommissionedWell that = (CommissionedWell) o;
    return Objects.equals(id, that.id)
        && Objects.equals(commissionedWellSchedule, that.commissionedWellSchedule)
        && Objects.equals(wellbore, that.wellbore);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        commissionedWellSchedule,
        wellbore
    );
  }
}
