package uk.co.ogauthority.pathfinder.publicdata;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentWell;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;

record InfrastructureProjectWellScheduleJson(
    Integer id,
    StartEndYearJson period,
    List<WellboreJson> wellbores
) {

  static InfrastructureProjectWellScheduleJson from(
      CommissionedWellSchedule commissionedWellSchedule,
      Collection<CommissionedWell> commissionedWells
  ) {
    var id = commissionedWellSchedule.getId();
    var period = new StartEndYearJson(
        commissionedWellSchedule.getEarliestStartYear(),
        commissionedWellSchedule.getLatestCompletionYear()
    );
    var wellbores = commissionedWells != null
        ? commissionedWells.stream()
            .map(CommissionedWell::getWellbore)
            .sorted(Comparator.comparing(Wellbore::getSortKey))
            .map(WellboreJson::from)
            .toList()
        : null;

    return new InfrastructureProjectWellScheduleJson(
        id,
        period,
        wellbores
    );
  }

  static InfrastructureProjectWellScheduleJson from(
      PlugAbandonmentSchedule plugAbandonmentSchedule,
      Collection<PlugAbandonmentWell> plugAbandonmentWells
  ) {
    var id = plugAbandonmentSchedule.getId();
    var period = new StartEndYearJson(
        plugAbandonmentSchedule.getEarliestStartYear(),
        plugAbandonmentSchedule.getLatestCompletionYear()
    );
    var wellbores = plugAbandonmentWells != null
        ? plugAbandonmentWells.stream()
            .map(PlugAbandonmentWell::getWellbore)
            .sorted(Comparator.comparing(Wellbore::getSortKey))
            .map(WellboreJson::from)
            .toList()
        : null;

    return new InfrastructureProjectWellScheduleJson(
        id,
        period,
        wellbores
    );
  }
}
