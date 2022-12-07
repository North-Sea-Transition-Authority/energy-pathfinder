package uk.co.ogauthority.pathfinder.model.view.commissionedwell;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;

public class CommissionedWellScheduleView extends ProjectSummaryItem {

  private String earliestStartYear;

  private String latestCompletionYear;

  private List<String> wells = new ArrayList<>();

  public String getEarliestStartYear() {
    return earliestStartYear;
  }

  public void setEarliestStartYear(String earliestStartYear) {
    this.earliestStartYear = earliestStartYear;
  }

  public String getLatestCompletionYear() {
    return latestCompletionYear;
  }

  public void setLatestCompletionYear(String latestCompletionYear) {
    this.latestCompletionYear = latestCompletionYear;
  }

  public List<String> getWells() {
    return wells;
  }

  public void setWells(List<String> wells) {
    this.wells = wells;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof CommissionedWellScheduleView)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    CommissionedWellScheduleView that = (CommissionedWellScheduleView) o;
    return Objects.equals(earliestStartYear, that.earliestStartYear)
        && Objects.equals(latestCompletionYear, that.latestCompletionYear)
        && Objects.equals(wells, that.wells);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        earliestStartYear,
        latestCompletionYear,
        wells
    );
  }

  @Override
  public String toString() {
    return "CommissionedWellScheduleView{" +
        "earliestStartYear='" + earliestStartYear + '\'' +
        ", latestCompletionYear='" + latestCompletionYear + '\'' +
        ", wells=" + wells +
        '}';
  }
}
