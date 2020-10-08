package uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint;

public class MinMaxYearLabelsHint {

  private final String minYearLabel;
  private final String maxYearLabel;

  public MinMaxYearLabelsHint(String minYearLabel, String maxYearLabel) {
    this.minYearLabel = minYearLabel;
    this.maxYearLabel = maxYearLabel;
  }

  public String getMinYearLabel() {
    return minYearLabel;
  }

  public String getMaxYearLabel() {
    return maxYearLabel;
  }
}
