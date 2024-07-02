package uk.co.ogauthority.pathfinder.service.controller;

import jakarta.validation.constraints.NotNull;

public class FieldOrderTestForm {

  @NotNull
  private String firstField;

  @NotNull
  private String secondField;

  @NotNull
  private String thirdField;

  public FieldOrderTestForm() {}


  public String getFirstField() {
    return firstField;
  }

  public void setFirstField(String firstField) {
    this.firstField = firstField;
  }

  public String getSecondField() {
    return secondField;
  }

  public void setSecondField(String secondField) {
    this.secondField = secondField;
  }

  public String getThirdField() {
    return thirdField;
  }

  public void setThirdField(String thirdField) {
    this.thirdField = thirdField;
  }
}
