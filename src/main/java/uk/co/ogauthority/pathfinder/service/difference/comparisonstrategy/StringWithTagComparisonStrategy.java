package uk.co.ogauthority.pathfinder.service.difference.comparisonstrategy;

import uk.co.ogauthority.pathfinder.model.difference.DiffedField;
import uk.co.ogauthority.pathfinder.model.difference.DifferenceType;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;

public class StringWithTagComparisonStrategy implements ComparisonStrategy<StringWithTag> {

  private final StringComparisonStrategy stringComparisonStrategy;

  public StringWithTagComparisonStrategy() {
    super();
    this.stringComparisonStrategy = new StringComparisonStrategy();
  }

  @Override
  public StringWithTag objectAsType(Object value) {

    if (value == null) {
      return new StringWithTag();
    }

    if (value instanceof StringWithTag) {
      return (StringWithTag) value;
    }

    throw new IllegalArgumentException(String.format("Cannot convert value of type '%s' as a StringWithTag", value.getClass()));
  }

  @Override
  public DiffedField compareType(StringWithTag currentValue, StringWithTag previousValue) {

    var currentStringValue = (currentValue.getValue() != null) ? currentValue.getValue() : "";
    var previousStringValue = (previousValue.getValue() != null) ? previousValue.getValue() : "";
    DiffedField diffedField = stringComparisonStrategy.compareType(currentStringValue, previousStringValue);

    diffedField.setCurrentValueTag(currentValue.getTag());
    diffedField.setPreviousValueTag(previousValue.getTag());
    return diffedField;
  }

  @Override
  public DiffedField createTypeDeletedDiffedField(StringWithTag value) {
    return new DiffedField(DifferenceType.DELETED, null, value.getValue(), null, value.getTag());
  }

  @Override
  public DiffedField createTypeAddedDiffedField(StringWithTag value) {
    return new DiffedField(DifferenceType.ADDED, value.getValue(), null, value.getTag(), null);
  }

  @Override
  public DiffedField createTypeNotDiffedField(StringWithTag value) {
    return new DiffedField(DifferenceType.NOT_DIFFED, value.getValue(), null, value.getTag(), null);
  }
}

