package uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber;

public class PositiveWholeNumberTestForm {
  public static final String PREFIX = "The number";

  @PositiveWholeNumber(messagePrefix = PREFIX)
  private Integer number;

  public PositiveWholeNumberTestForm() {
  }

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }
}
