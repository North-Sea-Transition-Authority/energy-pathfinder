package uk.co.ogauthority.pathfinder.model.view.decommissioningschedule;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.CessationOfProductionDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.DecommissioningStartDateType;
import uk.co.ogauthority.pathfinder.testutil.DecommissioningScheduleTestUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissioningScheduleViewUtilTest {

  @Test
  public void from_whenExactDecommissioningStartDateType() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();
    decommissioningSchedule.setDecommissioningStartDateType(DecommissioningStartDateType.EXACT);

    var decommissioningStartDate = LocalDate.now();
    decommissioningSchedule.setExactDecommissioningStartDate(decommissioningStartDate);

    var decommissioningScheduleView = DecommissioningScheduleViewUtil.from(decommissioningSchedule);

    assertThat(decommissioningScheduleView.getDecommissioningStartDate()).isEqualTo(
        DateUtil.formatDate(decommissioningStartDate)
    );
  }

  @Test
  public void from_whenEstimatedDecommissioningStartDateType() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();
    decommissioningSchedule.setDecommissioningStartDateType(DecommissioningStartDateType.ESTIMATED);

    var decommissioningStartDateQuarter = Quarter.Q2;
    var decommissioningStartDateYear = 2020;
    decommissioningSchedule.setEstimatedDecommissioningStartDateQuarter(decommissioningStartDateQuarter);
    decommissioningSchedule.setEstimatedDecommissioningStartDateYear(decommissioningStartDateYear);

    var decommissioningScheduleView = DecommissioningScheduleViewUtil.from(decommissioningSchedule);

    assertThat(decommissioningScheduleView.getDecommissioningStartDate()).isEqualTo(
        DateUtil.getDateFromQuarterYear(
            decommissioningStartDateQuarter,
            decommissioningStartDateYear
        )
    );
  }

  @Test
  public void from_whenUnknownDecommissioningStartDateType() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();
    decommissioningSchedule.setDecommissioningStartDateType(DecommissioningStartDateType.UNKNOWN);

    var decommissioningStartDateNotProvidedReason = "Test reason";
    decommissioningSchedule.setDecommissioningStartDateNotProvidedReason(decommissioningStartDateNotProvidedReason);

    var decommissioningScheduleView = DecommissioningScheduleViewUtil.from(decommissioningSchedule);

    assertThat(decommissioningScheduleView.getDecommissioningStartDate()).isEqualTo(
        decommissioningStartDateNotProvidedReason
    );
  }

  @Test
  public void from_whenExactCessationOfProductionDateType() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();
    decommissioningSchedule.setCessationOfProductionDateType(CessationOfProductionDateType.EXACT);

    var cessationOfProductionDate = LocalDate.now();
    decommissioningSchedule.setExactCessationOfProductionDate(cessationOfProductionDate);

    var decommissioningScheduleView = DecommissioningScheduleViewUtil.from(decommissioningSchedule);

    assertThat(decommissioningScheduleView.getCessationOfProductionDate()).isEqualTo(
        DateUtil.formatDate(cessationOfProductionDate)
    );
  }

  @Test
  public void from_whenEstimatedCessationOfProductionDateType() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();
    decommissioningSchedule.setCessationOfProductionDateType(CessationOfProductionDateType.ESTIMATED);

    var cessationOfProductionDateQuarter = Quarter.Q2;
    var cessationOfProductionDateYear = 2020;
    decommissioningSchedule.setEstimatedCessationOfProductionDateQuarter(cessationOfProductionDateQuarter);
    decommissioningSchedule.setEstimatedCessationOfProductionDateYear(cessationOfProductionDateYear);

    var decommissioningScheduleView = DecommissioningScheduleViewUtil.from(decommissioningSchedule);

    assertThat(decommissioningScheduleView.getCessationOfProductionDate()).isEqualTo(
        DateUtil.getDateFromQuarterYear(
            cessationOfProductionDateQuarter,
            cessationOfProductionDateYear
        )
    );
  }

  @Test
  public void from_whenUnknownCessationOfProductionDateType() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();
    decommissioningSchedule.setCessationOfProductionDateType(CessationOfProductionDateType.UNKNOWN);

    var cessationOfProductionDateNotProvidedReason = "Test reason";
    decommissioningSchedule.setCessationOfProductionDateNotProvidedReason(cessationOfProductionDateNotProvidedReason);

    var decommissioningScheduleView = DecommissioningScheduleViewUtil.from(decommissioningSchedule);

    assertThat(decommissioningScheduleView.getCessationOfProductionDate()).isEqualTo(
        cessationOfProductionDateNotProvidedReason
    );
  }
}
