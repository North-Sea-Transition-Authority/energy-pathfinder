package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellForm;
import uk.co.ogauthority.pathfinder.model.view.commissionedwell.CommissionedWellScheduleView;
import uk.co.ogauthority.pathfinder.model.view.commissionedwell.CommissionedWellScheduleViewUtil;

public class CommissionedWellTestUtil {

  private CommissionedWellTestUtil() {
    throw new IllegalStateException("CommissionedWellTestUtil is a utility class and should not be instantiated");
  }

  public static CommissionedWellForm getCompleteCommissionedWellForm() {
    var form = new CommissionedWellForm();
    form.setCommissioningSchedule(new MinMaxDateInput("2021", String.valueOf(LocalDate.now().getYear())));
    form.setWells(List.of(1, 2));
    return form;
  }

  public static CommissionedWellForm getEmptyCommissionedWellForm() {
    var form = new CommissionedWellForm();
    form.setCommissioningSchedule(new MinMaxDateInput(null, null));
    return form;
  }

  public static CommissionedWellSchedule getCommissionedWellSchedule() {
    return getCommissionedWellSchedule(
        ProjectUtil.getProjectDetails(),
        2021,
        LocalDate.now().getYear()
    );
  }

  public static CommissionedWellSchedule getCommissionedWellSchedule(Integer id, ProjectDetail projectDetail) {
    return getCommissionedWellSchedule(id, projectDetail, 2021, LocalDate.now().getYear());
  }

  public static CommissionedWellSchedule getCommissionedWellSchedule(
      ProjectDetail projectDetail,
      int earliestStartYear,
      int latestStartYear
  ) {
    return getCommissionedWellSchedule(null, projectDetail, earliestStartYear, latestStartYear);
  }

  public static CommissionedWellSchedule getCommissionedWellSchedule(
      Integer id,
      ProjectDetail projectDetail,
      int earliestStartYear,
      int latestStartYear
  ) {
    var commissionedWellSchedule = new CommissionedWellSchedule(id);
    commissionedWellSchedule.setProjectDetail(projectDetail);
    commissionedWellSchedule.setEarliestStartYear(earliestStartYear);
    commissionedWellSchedule.setLatestCompletionYear(latestStartYear);
    return commissionedWellSchedule;
  }

  public static CommissionedWell getCommissionedWell(Integer id, CommissionedWellSchedule commissionedWellSchedule) {
    return getCommissionedWell(id, WellboreTestUtil.createWellbore(), commissionedWellSchedule);
  }

  public static CommissionedWell getCommissionedWell(Integer id,
                                                     Wellbore wellbore,
                                                     CommissionedWellSchedule commissionedWellSchedule) {
    var commissionedWell = new CommissionedWell();
    commissionedWell.setId(id);
    commissionedWell.setWellbore(wellbore);
    commissionedWell.setCommissionedWellSchedule(commissionedWellSchedule);
    return commissionedWell;
  }

  public static CommissionedWell getCommissionedWell() {
    return getCommissionedWell(
        1,
        WellboreTestUtil.createWellbore(),
        getCommissionedWellSchedule()
    );
  }

  public static CommissionedWellScheduleView getCommissionedWellScheduleView(int displayOrder, boolean isValid) {
    return CommissionedWellScheduleViewUtil.from(
        getCommissionedWellSchedule(),
        List.of(getCommissionedWell()),
        displayOrder,
        isValid
    );
  }
}
