package uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "work_plan_collaboration_setup")
public class ForwardWorkPlanCollaborationSetup extends ProjectDetailEntity {

  private Boolean hasCollaborationsToAdd;

  private Boolean hasOtherCollaborationToAdd;

  public ForwardWorkPlanCollaborationSetup() {}

  public ForwardWorkPlanCollaborationSetup(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public Boolean getHasCollaborationToAdd() {
    return hasCollaborationsToAdd;
  }

  public void setHasCollaborationToAdd(Boolean hasCollaborationsToAdd) {
    this.hasCollaborationsToAdd = hasCollaborationsToAdd;
  }

  public Boolean getHasOtherCollaborationToAdd() {
    return hasOtherCollaborationToAdd;
  }

  public void setHasOtherCollaborationToAdd(Boolean hasOtherCollaborationToAdd) {
    this.hasOtherCollaborationToAdd = hasOtherCollaborationToAdd;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ForwardWorkPlanCollaborationSetup that = (ForwardWorkPlanCollaborationSetup) o;
    return Objects.equals(hasCollaborationsToAdd, that.hasCollaborationsToAdd)
        && Objects.equals(hasOtherCollaborationToAdd, that.hasOtherCollaborationToAdd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        hasCollaborationsToAdd,
        hasOtherCollaborationToAdd
    );
  }
}