package uk.co.ogauthority.pathfinder.model.form.forminput.dateinput;

public abstract class DateInputCommonAccessor {

  protected String month;
  protected String year;

  public String getMonth() {
    return month;
  }

  public void setMonth(String month) {
    this.month = month;
  }

  public void setMonth(int month) {
    this.month = String.valueOf(month);
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public void setYear(int year) {
    this.year = String.valueOf(year);
  }
}
