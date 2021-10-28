package uk.co.ogauthority.pathfinder.service.difference.comparisonstrategy;

import org.apache.commons.lang3.StringUtils;
import uk.co.ogauthority.pathfinder.exception.DifferenceProcessingException;
import uk.co.ogauthority.pathfinder.model.difference.DiffedField;
import uk.co.ogauthority.pathfinder.model.difference.DifferenceType;

public class StringComparisonStrategy implements ComparisonStrategy<String> {

  public StringComparisonStrategy() {
    super();
  }

  @Override
  public String objectAsType(Object value) {

    if (value == null) {
      return "";
    }

    if (value instanceof String) {
      return (String) value;
    }

    if (value instanceof Integer) {
      Integer current = (Integer) value;
      return current.toString();
    }

    if (value instanceof Boolean) {
      Boolean current = (Boolean) value;
      return Boolean.TRUE.equals(current) ? "Yes" : "No";
    }

    throw new DifferenceProcessingException(String.format("Cannot represent value of type '%s' as String", value.getClass()));
  }

  @Override
  public DiffedField compareType(String currentValue, String previousValue) {
    if (currentValue.equals(previousValue)) {
      return new DiffedField(DifferenceType.UNCHANGED, currentValue, previousValue);
    }

    if (StringUtils.isBlank(currentValue) && StringUtils.isNotBlank(previousValue)) {
      return new DiffedField(DifferenceType.DELETED, currentValue, previousValue);
    }

    if (StringUtils.isNotBlank(currentValue) && StringUtils.isBlank(previousValue)) {
      return new DiffedField(DifferenceType.ADDED, currentValue, previousValue);
    }

    return new DiffedField(DifferenceType.UPDATED, currentValue, previousValue);
  }

  @Override
  public DiffedField createTypeDeletedDiffedField(String value) {
    return new DiffedField(DifferenceType.DELETED, "", value);
  }

  @Override
  public DiffedField createTypeAddedDiffedField(String value) {
    return new DiffedField(DifferenceType.ADDED, value, "");
  }

  @Override
  public DiffedField createTypeNotDiffedField(String value) {
    return new DiffedField(DifferenceType.NOT_DIFFED, value, "");
  }
}
