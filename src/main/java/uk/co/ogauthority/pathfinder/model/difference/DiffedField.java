package uk.co.ogauthority.pathfinder.model.difference;

import uk.co.ogauthority.pathfinder.model.view.Tag;

public class DiffedField {

  private DifferenceType differenceType;

  private String currentValue;

  private Tag currentValueTag;

  private String previousValue;

  private Tag previousValueTag;

  public DiffedField(DifferenceType differenceType, String currentValue, String previousValue, Tag currentValueTag, Tag previousValueTag) {
    this.differenceType = differenceType;
    this.currentValue = currentValue;
    this.previousValue = previousValue;
    this.currentValueTag = currentValueTag != null ? currentValueTag : Tag.NONE;
    this.previousValueTag = previousValueTag != null ? previousValueTag : Tag.NONE;
  }

  public DiffedField(DifferenceType differenceType, String currentValue, String previousValue) {
    this(differenceType, currentValue, previousValue, Tag.NONE, Tag.NONE);
  }

  public DifferenceType getDifferenceType() {
    return differenceType;
  }

  public String getCurrentValue() {
    return currentValue;
  }

  public String getPreviousValue() {
    return previousValue;
  }

  public Tag getCurrentValueTag() {
    return currentValueTag;
  }

  public void setCurrentValueTag(Tag currentValueTag) {
    this.currentValueTag = currentValueTag;
  }

  public Tag getPreviousValueTag() {
    return previousValueTag;
  }

  public void setPreviousValueTag(Tag previousValueTag) {
    this.previousValueTag = previousValueTag;
  }
}
