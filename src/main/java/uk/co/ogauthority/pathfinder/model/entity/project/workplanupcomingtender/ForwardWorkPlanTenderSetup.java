package uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender;

import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "work_plan_tender_setup")
public class ForwardWorkPlanTenderSetup extends ProjectDetailEntity {

  private Boolean hasTendersToAdd;

  private Boolean hasOtherTendersToAdd;

  public ForwardWorkPlanTenderSetup() {}

  public ForwardWorkPlanTenderSetup(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public Boolean getHasTendersToAdd() {
    return hasTendersToAdd;
  }

  public void setHasTendersToAdd(Boolean hasTendersToAdd) {
    this.hasTendersToAdd = hasTendersToAdd;
  }

  public Boolean getHasOtherTendersToAdd() {
    return hasOtherTendersToAdd;
  }

  public void setHasOtherTendersToAdd(Boolean hasOtherTendersToAdd) {
    this.hasOtherTendersToAdd = hasOtherTendersToAdd;
  }
}