package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissioningschedule.DecommissioningSchedule;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.CessationOfProductionDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.DecommissioningStartDateType;

public class DecommissioningScheduleTestUtil {

  private static final DecommissioningStartDateType DECOM_START_DATE_TYPE = DecommissioningStartDateType.ESTIMATED;
  private static final Quarter ESTIMATED_DECOM_START_DATE_QUARTER = Quarter.Q2;
  private static final Integer ESTIMATED_DECOM_START_DATE_YEAR = 2020;
  private static final CessationOfProductionDateType COP_DATE_TYPE = CessationOfProductionDateType.UNKNOWN;
  private static final String COP_DATE_NOT_PROVIDED_REASON = "Test reason";

  private DecommissioningScheduleTestUtil() {
    throw new IllegalStateException("DecommissioningScheduleTestUtil is a utility class and should not be instantiated");
  }

  public static DecommissioningSchedule createDecommissioningSchedule() {
    return createDecommissioningSchedule(ProjectUtil.getProjectDetails());
  }

  public static DecommissioningSchedule createDecommissioningSchedule(ProjectDetail projectDetail) {
    return createDecommissioningSchedule(null, projectDetail);
  }

  public static DecommissioningSchedule createDecommissioningSchedule(Integer id, ProjectDetail projectDetail) {
    var decommissioningSchedule = new DecommissioningSchedule(id);
    decommissioningSchedule.setProjectDetail(projectDetail);
    decommissioningSchedule.setDecommissioningStartDateType(DECOM_START_DATE_TYPE);
    decommissioningSchedule.setEstimatedDecommissioningStartDateQuarter(ESTIMATED_DECOM_START_DATE_QUARTER);
    decommissioningSchedule.setEstimatedDecommissioningStartDateYear(ESTIMATED_DECOM_START_DATE_YEAR);
    decommissioningSchedule.setCessationOfProductionDateType(COP_DATE_TYPE);
    decommissioningSchedule.setCessationOfProductionDateNotProvidedReason(COP_DATE_NOT_PROVIDED_REASON);
    return decommissioningSchedule;
  }
}
