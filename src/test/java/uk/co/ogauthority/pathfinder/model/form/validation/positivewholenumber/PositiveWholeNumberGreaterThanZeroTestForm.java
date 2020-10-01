package uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber;

public class PositiveWholeNumberGreaterThanZeroTestForm {

  public static final String PREFIX = "The number";

  @PositiveWholeNumberGreaterThanZero(messagePrefix = PREFIX)
  private Integer number;

  public PositiveWholeNumberGreaterThanZeroTestForm() {
  }

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }
}
