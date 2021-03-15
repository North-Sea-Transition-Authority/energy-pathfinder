package uk.co.ogauthority.pathfinder.model.view.decommissioningschedule;

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
}
