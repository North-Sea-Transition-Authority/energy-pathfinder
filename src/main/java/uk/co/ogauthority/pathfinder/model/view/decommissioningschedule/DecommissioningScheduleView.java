package uk.co.ogauthority.pathfinder.model.view.decommissioningschedule;

import java.util.Objects;

public class DecommissioningScheduleView {

  private String decommissioningStartDate;

  private String cessationOfProductionDate;

  public String getDecommissioningStartDate() {
    return decommissioningStartDate;
  }

  public void setDecommissioningStartDate(String decommissioningStartDate) {
    this.decommissioningStartDate = decommissioningStartDate;
  }

  public String getCessationOfProductionDate() {
    return cessationOfProductionDate;
  }

  public void setCessationOfProductionDate(String cessationOfProductionDate) {
    this.cessationOfProductionDate = cessationOfProductionDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DecommissioningScheduleView that = (DecommissioningScheduleView) o;
    return Objects.equals(decommissioningStartDate, that.decommissioningStartDate)
        && Objects.equals(cessationOfProductionDate, that.cessationOfProductionDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(decommissioningStartDate, cessationOfProductionDate);
  }
}
