package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.CessationOfProductionDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.DecommissioningStartDateType;
import uk.co.ogauthority.pathfinder.testutil.DecommissioningScheduleTestUtil;

class InfrastructureProjectDecommissioningScheduleJsonTest {

  @Test
  void from() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();

    decommissioningSchedule.setDecommissioningStartDateType(DecommissioningStartDateType.EXACT);
    decommissioningSchedule.setExactDecommissioningStartDate(LocalDate.of(2025, 2, 18));

    decommissioningSchedule.setCessationOfProductionDateType(CessationOfProductionDateType.UNKNOWN);

    var decommissioningScheduleJson = InfrastructureProjectDecommissioningScheduleJson.from(decommissioningSchedule);

    var expectedDecommissioningScheduleJson = new InfrastructureProjectDecommissioningScheduleJson(
        DecommissioningStartDateType.EXACT.name(),
        LocalDate.of(2025, 2, 18),
        null,
        DecommissioningStartDateType.UNKNOWN.name(),
        null,
        null
    );

    assertThat(decommissioningScheduleJson).isEqualTo(expectedDecommissioningScheduleJson);
  }

  @Test
  void from_decommissioningStartDateTypeExact() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();

    decommissioningSchedule.setDecommissioningStartDateType(DecommissioningStartDateType.EXACT);
    decommissioningSchedule.setExactDecommissioningStartDate(LocalDate.of(2025, 2, 18));

    var decommissioningScheduleJson = InfrastructureProjectDecommissioningScheduleJson.from(decommissioningSchedule);

    assertThat(decommissioningScheduleJson)
        .extracting(
            InfrastructureProjectDecommissioningScheduleJson::startDateType,
            InfrastructureProjectDecommissioningScheduleJson::exactStartDate,
            InfrastructureProjectDecommissioningScheduleJson::estimatedStartDate
        )
        .containsExactly(
            DecommissioningStartDateType.EXACT.name(),
            LocalDate.of(2025, 2, 18),
            null
        );
  }

  @Test
  void from_decommissioningStartDateTypeEstimated() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();

    decommissioningSchedule.setDecommissioningStartDateType(DecommissioningStartDateType.ESTIMATED);
    decommissioningSchedule.setEstimatedDecommissioningStartDateQuarter(Quarter.Q1);
    decommissioningSchedule.setEstimatedDecommissioningStartDateYear(2025);

    var decommissioningScheduleJson = InfrastructureProjectDecommissioningScheduleJson.from(decommissioningSchedule);

    assertThat(decommissioningScheduleJson)
        .extracting(
            InfrastructureProjectDecommissioningScheduleJson::startDateType,
            InfrastructureProjectDecommissioningScheduleJson::exactStartDate,
            InfrastructureProjectDecommissioningScheduleJson::estimatedStartDate
        )
        .containsExactly(
            DecommissioningStartDateType.ESTIMATED.name(),
            null,
            QuarterYearJson.from(Quarter.Q1, 2025)
        );
  }

  @Test
  void from_decommissioningStartDateTypeUnknown() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();

    decommissioningSchedule.setDecommissioningStartDateType(DecommissioningStartDateType.UNKNOWN);

    var decommissioningScheduleJson = InfrastructureProjectDecommissioningScheduleJson.from(decommissioningSchedule);

    assertThat(decommissioningScheduleJson)
        .extracting(
            InfrastructureProjectDecommissioningScheduleJson::startDateType,
            InfrastructureProjectDecommissioningScheduleJson::exactStartDate,
            InfrastructureProjectDecommissioningScheduleJson::estimatedStartDate
        )
        .containsExactly(
            DecommissioningStartDateType.UNKNOWN.name(),
            null,
            null
        );
  }

  @Test
  void from_cessationOfProductionDateTypeExact() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();

    decommissioningSchedule.setCessationOfProductionDateType(CessationOfProductionDateType.EXACT);
    decommissioningSchedule.setExactCessationOfProductionDate(LocalDate.of(2025, 2, 18));

    var decommissioningScheduleJson = InfrastructureProjectDecommissioningScheduleJson.from(decommissioningSchedule);

    assertThat(decommissioningScheduleJson)
        .extracting(
            InfrastructureProjectDecommissioningScheduleJson::cessationOfProductionDateType,
            InfrastructureProjectDecommissioningScheduleJson::exactCessationOfProductionDate,
            InfrastructureProjectDecommissioningScheduleJson::estimatedCessationOfProductionDate
        )
        .containsExactly(
            CessationOfProductionDateType.EXACT.name(),
            LocalDate.of(2025, 2, 18),
            null
        );
  }

  @Test
  void from_cessationOfProductionDateTypeEstimated() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();

    decommissioningSchedule.setCessationOfProductionDateType(CessationOfProductionDateType.ESTIMATED);
    decommissioningSchedule.setEstimatedCessationOfProductionDateQuarter(Quarter.Q1);
    decommissioningSchedule.setEstimatedCessationOfProductionDateYear(2025);

    var decommissioningScheduleJson = InfrastructureProjectDecommissioningScheduleJson.from(decommissioningSchedule);

    assertThat(decommissioningScheduleJson)
        .extracting(
            InfrastructureProjectDecommissioningScheduleJson::cessationOfProductionDateType,
            InfrastructureProjectDecommissioningScheduleJson::exactCessationOfProductionDate,
            InfrastructureProjectDecommissioningScheduleJson::estimatedCessationOfProductionDate
        )
        .containsExactly(
            CessationOfProductionDateType.ESTIMATED.name(),
            null,
            QuarterYearJson.from(Quarter.Q1, 2025)
        );
  }

  @Test
  void from_cessationOfProductionDateTypeUnknown() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();

    decommissioningSchedule.setCessationOfProductionDateType(CessationOfProductionDateType.UNKNOWN);

    var decommissioningScheduleJson = InfrastructureProjectDecommissioningScheduleJson.from(decommissioningSchedule);

    assertThat(decommissioningScheduleJson)
        .extracting(
            InfrastructureProjectDecommissioningScheduleJson::cessationOfProductionDateType,
            InfrastructureProjectDecommissioningScheduleJson::exactCessationOfProductionDate,
            InfrastructureProjectDecommissioningScheduleJson::estimatedCessationOfProductionDate
        )
        .containsExactly(
            CessationOfProductionDateType.UNKNOWN.name(),
            null,
            null
        );
  }
}
