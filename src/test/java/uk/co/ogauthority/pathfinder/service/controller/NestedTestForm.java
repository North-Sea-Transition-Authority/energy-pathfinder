package uk.co.ogauthority.pathfinder.service.controller;

public class NestedTestForm {

  private String firstField;

  private FieldOrderTestForm nestedForm;

  private String thirdField;

  public String getFirstField() {
    return firstField;
  }

  public void setFirstField(String firstField) {
    this.firstField = firstField;
  }

  public FieldOrderTestForm getNestedForm() {
    return nestedForm;
  }

  public void setNestedForm(FieldOrderTestForm nestedForm) {
    this.nestedForm = nestedForm;
  }

  public String getThirdField() {
    return thirdField;
  }

  public void setThirdField(String thirdField) {
    this.thirdField = thirdField;
  }
}
