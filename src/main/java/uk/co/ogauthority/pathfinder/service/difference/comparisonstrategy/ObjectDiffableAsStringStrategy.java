package uk.co.ogauthority.pathfinder.service.difference.comparisonstrategy;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.difference.DiffableAsString;
import uk.co.ogauthority.pathfinder.model.difference.DiffedField;
import uk.co.ogauthority.pathfinder.model.difference.DifferenceType;
import uk.co.ogauthority.pathfinder.model.view.Tag;

public class ObjectDiffableAsStringStrategy implements ComparisonStrategy<DiffableAsString> {

  private final StringComparisonStrategy stringComparisonStrategy;

  public ObjectDiffableAsStringStrategy() {
    super();
    this.stringComparisonStrategy = new StringComparisonStrategy();
  }

  @Override
  public DiffableAsString objectAsType(Object value) {
    if (Objects.isNull(value)) {
      return () -> "";
    } else if (value instanceof DiffableAsString) {
      return (DiffableAsString) value;
    }

    throw new IllegalArgumentException(
        String.format("Cannot convert value of type '%s' as a DiffableAsString", value.getClass()));
  }

  @Override
  public DiffedField compareType(DiffableAsString currentValue, DiffableAsString previousValue) {
    return stringComparisonStrategy.compareType(currentValue.getDiffableString(),
        previousValue.getDiffableString());
  }

  @Override
  public DiffedField createTypeDeletedDiffedField(DiffableAsString value) {
    return new DiffedField(DifferenceType.DELETED, null, value.getDiffableString(), null, Tag.NONE);
  }

  @Override
  public DiffedField createTypeAddedDiffedField(DiffableAsString value) {
    return new DiffedField(DifferenceType.ADDED, value.getDiffableString(), null, Tag.NONE, null);
  }

  @Override
  public DiffedField createTypeNotDiffedField(DiffableAsString value) {
    return new DiffedField(DifferenceType.NOT_DIFFED, value.getDiffableString(), null, Tag.NONE, null);
  }
}

