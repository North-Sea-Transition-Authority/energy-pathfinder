package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan;

import java.util.Objects;

public class ForwardWorkPlanCollaborationSetupView {

  private String hasCollaborationsToAdd;

  public String getHasCollaborationsToAdd() {
    return hasCollaborationsToAdd;
  }

  public void setHasCollaborationsToAdd(String hasCollaborationsToAdd) {
    this.hasCollaborationsToAdd = hasCollaborationsToAdd;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ForwardWorkPlanCollaborationSetupView that = (ForwardWorkPlanCollaborationSetupView) o;
    return Objects.equals(hasCollaborationsToAdd, that.hasCollaborationsToAdd);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(hasCollaborationsToAdd);
  }
}
