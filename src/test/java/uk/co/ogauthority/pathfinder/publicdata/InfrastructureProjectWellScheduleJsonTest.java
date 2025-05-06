package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.CommissionedWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.PlugAbandonmentScheduleTestUtil;
import uk.co.ogauthority.pathfinder.testutil.PlugAbandonmentWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.WellboreTestUtil;

class InfrastructureProjectWellScheduleJsonTest {

  @Test
  void from_withCommissionedWellSchedule() {
    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    var commissionedWells = WellboreTestUtil.getUnorderedWellbores()
        .stream()
        .map(wellbore ->
            CommissionedWellTestUtil.getCommissionedWell(null, wellbore, commissionedWellSchedule))
        .toList();

    var infrastructureProjectWellScheduleJson =
        InfrastructureProjectWellScheduleJson.from(commissionedWellSchedule, commissionedWells);

    var expectedInfrastructureProjectWellScheduleJson =
        new InfrastructureProjectWellScheduleJson(
            commissionedWellSchedule.getId(),
            new StartEndYearJson(commissionedWellSchedule.getEarliestStartYear(), commissionedWellSchedule.getLatestCompletionYear()),
            WellboreTestUtil.getOrderedWellbores()
                .stream()
                .map(WellboreJson::from)
                .toList()
        );

    assertThat(infrastructureProjectWellScheduleJson).isEqualTo(expectedInfrastructureProjectWellScheduleJson);
  }

  @Test
  void from_withPlugAbandonmentSchedule() {
    var plugAbandonmentSchedule = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();

    var plugAbandonmentWells = WellboreTestUtil.getUnorderedWellbores()
        .stream()
        .map(wellbore ->
            PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(null, plugAbandonmentSchedule, wellbore))
        .toList();

    var infrastructureProjectWellScheduleJson =
        InfrastructureProjectWellScheduleJson.from(plugAbandonmentSchedule, plugAbandonmentWells);

    var expectedInfrastructureProjectWellScheduleJson =
        new InfrastructureProjectWellScheduleJson(
            plugAbandonmentSchedule.getId(),
            new StartEndYearJson(plugAbandonmentSchedule.getEarliestStartYear(), plugAbandonmentSchedule.getLatestCompletionYear()),
            WellboreTestUtil.getOrderedWellbores()
                .stream()
                .map(WellboreJson::from)
                .toList()
        );

    assertThat(infrastructureProjectWellScheduleJson).isEqualTo(expectedInfrastructureProjectWellScheduleJson);
  }
}
