package uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint;

import java.time.LocalDate;

public final class OnOrBeforeDateHint {
  private final LocalDate date;
  private final String dateLabel;

  public OnOrBeforeDateHint(LocalDate date, String dateLabel) {
    this.date = date;
    this.dateLabel = dateLabel;
  }

  public LocalDate getDate() {
    return date;
  }

  public String getDateLabel() {
    return dateLabel;
  }
}
