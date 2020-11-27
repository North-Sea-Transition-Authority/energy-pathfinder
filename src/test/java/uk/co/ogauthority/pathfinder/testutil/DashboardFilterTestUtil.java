package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.form.dashboard.DashboardFilterForm;

public class DashboardFilterTestUtil {

  public static DashboardFilter getEmptyFilter() {
    return new DashboardFilter();
  }

  public static DashboardFilterForm getEmptyForm(){
    return new DashboardFilterForm();
  }
}
