package uk.co.ogauthority.pathfinder.model.form;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;

@RunWith(MockitoJUnitRunner.class)
public class ThreeFieldDateInputTest {

  private ThreeFieldDateInput threeFieldDateInput;

  @Before
  public void setup() {
    threeFieldDateInput = new ThreeFieldDateInput();
  }

  @Test
  public void createDate_whenNullValues() {
    assertThat(threeFieldDateInput.createDate()).isEmpty();
  }

  @Test
  public void createDate_whenInvalidTextDateComponents() {
    threeFieldDateInput.setMonth("testMonth");
    threeFieldDateInput.setYear("testYear");
    assertThat(threeFieldDateInput.createDate()).isEmpty();
  }

  @Test
  public void createDate_whenValidDateComponents_dateCreated() {
    threeFieldDateInput.setDay("1");
    threeFieldDateInput.setMonth("2");
    threeFieldDateInput.setYear("2020");
    assertThat(threeFieldDateInput.createDate()).contains(LocalDate.of(2020, 2, 1));
  }

  @Test
  public void createDate_whenInValidNumberDateComponents_invalidLargeMonth() {
    threeFieldDateInput.setDay("1");
    threeFieldDateInput.setMonth("13");
    threeFieldDateInput.setYear("2020");
    assertThat(threeFieldDateInput.createDate()).isEmpty();
  }

  @Test
  public void createDate_whenInValidNumberDateComponents_invalidSmallMonth() {
    threeFieldDateInput.setDay("1");
    threeFieldDateInput.setMonth("0");
    threeFieldDateInput.setYear("2020");
    assertThat(threeFieldDateInput.createDate()).isEmpty();
  }


  @Test
  public void isAfter_whenInvalidDate() {
    assertThat(threeFieldDateInput.isAfter(LocalDate.now())).isFalse();
  }

  @Test
  public void isAfter_whenValidDate_andDateIsSame() {
    threeFieldDateInput.setYear(LocalDate.now().getYear());
    threeFieldDateInput.setMonth(LocalDate.now().getMonthValue());
    threeFieldDateInput.setDay(LocalDate.now().getDayOfMonth());

    assertThat(threeFieldDateInput.isAfter(LocalDate.now()))
        .isFalse();
  }


  @Test
  public void isAfter_whenValidDate_andDateIsMonthsBefore() {
    threeFieldDateInput.setYear(LocalDate.now().getYear());
    threeFieldDateInput.setMonth(LocalDate.now().getMonthValue());
    threeFieldDateInput.setDay(LocalDate.now().getDayOfMonth());

    assertThat(threeFieldDateInput.isAfter(LocalDate.now().plus(1, ChronoUnit.MONTHS)))
        .isFalse();
  }

  @Test
  public void isAfter_whenValidDate_andDateIsMonthsAfter() {
    threeFieldDateInput.setYear(LocalDate.now().getYear());
    threeFieldDateInput.setMonth(LocalDate.now().getMonthValue());
    threeFieldDateInput.setDay(LocalDate.now().getDayOfMonth());

    assertThat(threeFieldDateInput.isAfter(LocalDate.now().minus(1, ChronoUnit.MONTHS)))
        .isTrue();
  }


  @Test
  public void isBefore_whenInvalidDate() {
    assertThat(threeFieldDateInput.isBefore(LocalDate.now())).isFalse();
  }

  @Test
  public void isBefore_whenValidDate_andDateIsSame() {
    threeFieldDateInput.setYear(LocalDate.now().getYear());
    threeFieldDateInput.setMonth(LocalDate.now().getMonthValue());
    threeFieldDateInput.setDay(LocalDate.now().getDayOfMonth());

    assertThat(threeFieldDateInput.isBefore(LocalDate.now()))
        .isFalse();
  }


  @Test
  public void isBefore_whenValidDate_andDateIsMonthsBefore() {
    threeFieldDateInput.setYear(LocalDate.now().getYear());
    threeFieldDateInput.setMonth(LocalDate.now().getMonthValue());
    threeFieldDateInput.setDay(LocalDate.now().getDayOfMonth());

    assertThat(threeFieldDateInput.isBefore(LocalDate.now().plus(1, ChronoUnit.MONTHS)))
        .isTrue();
  }

  @Test
  public void isBefore_whenValidDate_andDateIsMonthsAfter() {
    threeFieldDateInput.setYear(LocalDate.now().getYear());
    threeFieldDateInput.setMonth(LocalDate.now().getMonthValue());
    threeFieldDateInput.setDay(LocalDate.now().getDayOfMonth());

    assertThat(threeFieldDateInput.isBefore(LocalDate.now().minus(1, ChronoUnit.MONTHS)))
        .isFalse();
  }



  @Test
  public void isEqualTo_whenInvalidDate() {
    assertThat(threeFieldDateInput.isEqualTo(LocalDate.now())).isFalse();
  }

  @Test
  public void isEqualTo_whenValidDate_andDateIsSame() {
    threeFieldDateInput.setYear(LocalDate.now().getYear());
    threeFieldDateInput.setMonth(LocalDate.now().getMonthValue());
    threeFieldDateInput.setDay(LocalDate.now().getDayOfMonth());

    assertThat(threeFieldDateInput.isEqualTo(LocalDate.now()))
        .isTrue();
  }

  @Test
  public void isEqualTo_whenValidDate_andDateIsMonthsBefore() {
    threeFieldDateInput.setYear(LocalDate.now().getYear());
    threeFieldDateInput.setMonth(LocalDate.now().getMonthValue());
    threeFieldDateInput.setDay(LocalDate.now().getDayOfMonth());

    assertThat(threeFieldDateInput.isEqualTo(LocalDate.now().plus(1, ChronoUnit.MONTHS)))
        .isFalse();
  }

  @Test
  public void isEqualTo_whenValidDate_andDateIsMonthsAfter() {
    threeFieldDateInput.setYear(LocalDate.now().getYear());
    threeFieldDateInput.setMonth(LocalDate.now().getMonthValue());
    threeFieldDateInput.setDay(LocalDate.now().getDayOfMonth());

    assertThat(threeFieldDateInput.isEqualTo(LocalDate.now().minus(1, ChronoUnit.MONTHS)))
        .isFalse();
  }
}