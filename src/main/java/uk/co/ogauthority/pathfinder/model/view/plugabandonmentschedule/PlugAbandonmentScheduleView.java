package uk.co.ogauthority.pathfinder.model.view.plugabandonmentschedule;

import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;

public class PlugAbandonmentScheduleView extends ProjectSummaryItem {

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
}
