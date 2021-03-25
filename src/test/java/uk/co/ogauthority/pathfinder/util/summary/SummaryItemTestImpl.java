package uk.co.ogauthority.pathfinder.util.summary;

public class SummaryItemTestImpl implements SummaryItem{


  private Integer displayOrder;

  private Boolean isValid;

  public SummaryItemTestImpl(Integer displayOrder, Boolean isValid) {
    this.displayOrder = displayOrder;
    this.isValid = isValid;
  }

  @Override
  public Integer getDisplayOrder() {
    return displayOrder;
  }

  @Override
  public Boolean isValid() {
    return isValid;
  }
}
