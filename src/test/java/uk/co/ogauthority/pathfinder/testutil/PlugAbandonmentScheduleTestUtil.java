package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import java.util.Collections;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleForm;
import uk.co.ogauthority.pathfinder.model.view.plugabandonmentschedule.PlugAbandonmentScheduleView;
import uk.co.ogauthority.pathfinder.model.view.plugabandonmentschedule.PlugAbandonmentScheduleViewUtil;

public class PlugAbandonmentScheduleTestUtil {

  private static final Integer EARLIEST_PLUG_ABANDONMENT_START_YEAR = 2020;
  private static final Integer LATEST_PLUG_ABANDONMENT_COMPLETION_YEAR = LocalDate.now().getYear();

  private PlugAbandonmentScheduleTestUtil() {
    throw new IllegalStateException("PlugAbandonmentScheduleTestUtil is a utility class and should not be instantiated");
  }

  public static PlugAbandonmentScheduleForm getCompletedForm() {
    var form = new PlugAbandonmentScheduleForm();
    form.setPlugAbandonmentDate(new MinMaxDateInput(
        Integer.toString(EARLIEST_PLUG_ABANDONMENT_START_YEAR),
        Integer.toString(LATEST_PLUG_ABANDONMENT_COMPLETION_YEAR)
    ));
    return form;
  }

  public static PlugAbandonmentSchedule createPlugAbandonmentSchedule() {
    return createPlugAbandonmentSchedule(
        EARLIEST_PLUG_ABANDONMENT_START_YEAR,
        LATEST_PLUG_ABANDONMENT_COMPLETION_YEAR
    );
  }

  public static PlugAbandonmentSchedule createPlugAbandonmentSchedule(int earliestStartYear, int latestStartYear) {
    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();
    plugAbandonmentSchedule.setProjectDetail(ProjectUtil.getProjectDetails());
    plugAbandonmentSchedule.setEarliestStartYear(earliestStartYear);
    plugAbandonmentSchedule.setLatestCompletionYear(latestStartYear);
    return plugAbandonmentSchedule;
  }

  public static PlugAbandonmentScheduleView createPlugAbandonmentScheduleView() {
    return createPlugAbandonmentScheduleView(1, true);
  }

  public static PlugAbandonmentScheduleView createPlugAbandonmentScheduleView(int displayOrder, boolean isValid) {
    var plugAbandonmentSchedule = createPlugAbandonmentSchedule();
    return PlugAbandonmentScheduleViewUtil.from(plugAbandonmentSchedule, Collections.emptyList(), displayOrder, isValid);
  }
}
