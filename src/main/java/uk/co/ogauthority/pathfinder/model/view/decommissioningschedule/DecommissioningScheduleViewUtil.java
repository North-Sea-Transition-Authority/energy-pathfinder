package uk.co.ogauthority.pathfinder.model.view.decommissioningschedule;

import uk.co.ogauthority.pathfinder.model.entity.project.decommissioningschedule.DecommissioningSchedule;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.CessationOfProductionDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.DecommissioningStartDateType;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class DecommissioningScheduleViewUtil {

  private DecommissioningScheduleViewUtil() {
    throw new IllegalStateException("DecommissioningScheduleViewUtil is a util class and should not be instantiated");
  }

  public static DecommissioningScheduleView from(DecommissioningSchedule decommissioningSchedule) {
    var decommissioningScheduleView = new DecommissioningScheduleView();

    var decommissioningStartDateType = decommissioningSchedule.getDecommissioningStartDateType();
    if (DecommissioningStartDateType.EXACT.equals(decommissioningStartDateType)) {
      decommissioningScheduleView.setDecommissioningStartDate(
          DateUtil.formatDate(decommissioningSchedule.getExactDecommissioningStartDate())
      );
    } else if (DecommissioningStartDateType.ESTIMATED.equals(decommissioningStartDateType)) {
      decommissioningScheduleView.setDecommissioningStartDate(
          DateUtil.getDateFromQuarterYear(
              decommissioningSchedule.getEstimatedDecommissioningStartDateQuarter(),
              decommissioningSchedule.getEstimatedDecommissioningStartDateYear()
          )
      );
    } else if (DecommissioningStartDateType.UNKNOWN.equals(decommissioningStartDateType)) {
      decommissioningScheduleView.setDecommissioningStartDate(
          decommissioningSchedule.getDecommissioningStartDateNotProvidedReason()
      );
    }

    var cessationOfProductionDateType = decommissioningSchedule.getCessationOfProductionDateType();
    if (CessationOfProductionDateType.EXACT.equals(cessationOfProductionDateType)) {
      decommissioningScheduleView.setCessationOfProductionDate(
          DateUtil.formatDate(decommissioningSchedule.getExactCessationOfProductionDate())
      );
    } else if (CessationOfProductionDateType.ESTIMATED.equals(cessationOfProductionDateType)) {
      decommissioningScheduleView.setCessationOfProductionDate(
          DateUtil.getDateFromQuarterYear(
              decommissioningSchedule.getEstimatedCessationOfProductionDateQuarter(),
              decommissioningSchedule.getEstimatedCessationOfProductionDateYear()
          )
      );
    } else if (CessationOfProductionDateType.UNKNOWN.equals(cessationOfProductionDateType)) {
      decommissioningScheduleView.setCessationOfProductionDate(
          decommissioningSchedule.getCessationOfProductionDateNotProvidedReason()
      );
    }

    return decommissioningScheduleView;
  }
}
