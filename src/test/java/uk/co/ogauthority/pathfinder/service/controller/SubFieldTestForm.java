package uk.co.ogauthority.pathfinder.service.controller;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.TwoFieldDateInput;

public class SubFieldTestForm {

  @NotNull
  private TwoFieldDateInput firstField;

  @NotNull
  private String secondField;

  @NotNull
  private TwoFieldDateInput thirdField;

  public SubFieldTestForm() {
    this.firstField = new TwoFieldDateInput(null, null);
    this.thirdField = new TwoFieldDateInput(null, null);
  }

  public TwoFieldDateInput getFirstField() {
    return firstField;
  }

  public void setFirstField(TwoFieldDateInput firstField) {
    this.firstField = firstField;
  }

  public String getSecondField() {
    return secondField;
  }

  public void setSecondField(String secondField) {
    this.secondField = secondField;
  }

  public TwoFieldDateInput getThirdField() {
    return thirdField;
  }

  public void setThirdField(TwoFieldDateInput thirdField) {
    this.thirdField = thirdField;
  }
}
