package uk.co.ogauthority.pathfinder.testutil;

import java.util.Collections;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleForm;
import uk.co.ogauthority.pathfinder.model.view.plugabandonmentschedule.PlugAbandonmentScheduleView;
import uk.co.ogauthority.pathfinder.model.view.plugabandonmentschedule.PlugAbandonmentScheduleViewUtil;

public class PlugAbandonmentScheduleTestUtil {

  private static final Integer EARLIEST_PLUG_ABANDONMENT_START_YEAR = 2020;
  private static final Integer LATEST_PLUG_ABANDONMENT_COMPLETION_YEAR = 2021;

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
    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();
    plugAbandonmentSchedule.setProjectDetail(ProjectUtil.getProjectDetails());
    plugAbandonmentSchedule.setEarliestStartYear(EARLIEST_PLUG_ABANDONMENT_START_YEAR);
    plugAbandonmentSchedule.setLatestCompletionYear(LATEST_PLUG_ABANDONMENT_COMPLETION_YEAR);
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
