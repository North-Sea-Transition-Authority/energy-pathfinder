package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

import java.util.Objects;

public class ForwardWorkPlanTenderSetupView {

  private String hasTendersToAdd;

  public String getHasTendersToAdd() {
    return hasTendersToAdd;
  }

  public void setHasTendersToAdd(String hasTendersToAdd) {
    this.hasTendersToAdd = hasTendersToAdd;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ForwardWorkPlanTenderSetupView that = (ForwardWorkPlanTenderSetupView) o;
    return Objects.equals(hasTendersToAdd, that.hasTendersToAdd);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(hasTendersToAdd);
  }
}
