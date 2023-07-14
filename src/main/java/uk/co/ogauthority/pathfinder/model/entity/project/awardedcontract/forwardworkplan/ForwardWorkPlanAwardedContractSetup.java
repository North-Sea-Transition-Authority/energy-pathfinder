package uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "work_plan_awarded_contracts_setup")
public class ForwardWorkPlanAwardedContractSetup extends ProjectDetailEntity {

  private Boolean hasContractToAdd;

  private Boolean hasOtherContractToAdd;

  public ForwardWorkPlanAwardedContractSetup() {

  }

  public ForwardWorkPlanAwardedContractSetup(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public Boolean getHasContractToAdd() {
    return hasContractToAdd;
  }

  public void setHasContractToAdd(Boolean hasContractToAdd) {
    this.hasContractToAdd = hasContractToAdd;
  }

  public Boolean getHasOtherContractToAdd() {
    return hasOtherContractToAdd;
  }

  public void setHasOtherContractToAdd(Boolean hasOtherContractToAdd) {
    this.hasOtherContractToAdd = hasOtherContractToAdd;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    var that = (ForwardWorkPlanAwardedContractSetup) o;
    return Objects.equals(hasContractToAdd, that.hasContractToAdd)
        && Objects.equals(hasOtherContractToAdd, that.hasOtherContractToAdd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        hasContractToAdd,
        hasOtherContractToAdd
    );
  }
}
