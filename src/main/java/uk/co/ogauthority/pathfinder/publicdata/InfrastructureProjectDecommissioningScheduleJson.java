package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissioningschedule.DecommissioningSchedule;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.CessationOfProductionDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.DecommissioningStartDateType;

record InfrastructureProjectDecommissioningScheduleJson(
    String startDateType,
    LocalDate exactStartDate,
    QuarterYearJson estimatedStartDate,
    String cessationOfProductionDateType,
    LocalDate exactCessationOfProductionDate,
    QuarterYearJson estimatedCessationOfProductionDate
) {

  static InfrastructureProjectDecommissioningScheduleJson from(DecommissioningSchedule decommissioningSchedule) {
    var decommissioningStartDateType = decommissioningSchedule.getDecommissioningStartDateType();
    var startDateTypeName = decommissioningStartDateType.name();
    var exactStartDate = decommissioningStartDateType == DecommissioningStartDateType.EXACT
        ? decommissioningSchedule.getExactDecommissioningStartDate()
        : null;
    var estimatedStartDate = decommissioningStartDateType == DecommissioningStartDateType.ESTIMATED
        ? QuarterYearJson.from(
              decommissioningSchedule.getEstimatedDecommissioningStartDateQuarter(),
              decommissioningSchedule.getEstimatedDecommissioningStartDateYear()
          )
        : null;

    var cessationOfProductionDateType = decommissioningSchedule.getCessationOfProductionDateType();
    var cessationOfProductionDateTypeName = cessationOfProductionDateType.name();
    var exactCessationOfProductionDate = cessationOfProductionDateType == CessationOfProductionDateType.EXACT
        ? decommissioningSchedule.getExactCessationOfProductionDate()
        : null;
    var estimatedCessationOfProductionDate =
        cessationOfProductionDateType == CessationOfProductionDateType.ESTIMATED
            ? QuarterYearJson.from(
                decommissioningSchedule.getEstimatedCessationOfProductionDateQuarter(),
                decommissioningSchedule.getEstimatedCessationOfProductionDateYear()
            )
            : null;

    return new InfrastructureProjectDecommissioningScheduleJson(
        startDateTypeName,
        exactStartDate,
        estimatedStartDate,
        cessationOfProductionDateTypeName,
        exactCessationOfProductionDate,
        estimatedCessationOfProductionDate
    );
  }
}
