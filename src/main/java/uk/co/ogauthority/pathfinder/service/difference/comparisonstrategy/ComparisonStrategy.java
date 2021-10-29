package uk.co.ogauthority.pathfinder.service.difference.comparisonstrategy;

import uk.co.ogauthority.pathfinder.model.difference.DiffedField;
import uk.co.ogauthority.pathfinder.service.difference.ComparisonType;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;

/**
 * How to diff objects of type T. For use by {@link ComparisonType} and {@link DifferenceService}
 */
public interface ComparisonStrategy<T> {

  /**
   * create a diffedField object where the diff type is DELETED.
   */
  default DiffedField createDeletedDiffedField(Object value) {
    return createTypeDeletedDiffedField(objectAsType(value));
  }

  /**
   * create a diffedField object where the diff type is ADDED.
   */
  default DiffedField createAddedDiffedField(Object value) {
    return createTypeAddedDiffedField(objectAsType(value));
  }


  /**
   * create a diffedField object where the diff type is NOT_DIFFED.
   */
  default DiffedField createNotDiffedField(Object value) {
    return createTypeNotDiffedField(objectAsType(value));
  }

  /**
   * This is the attach point the diff service code uses. The Implementation detail is left to each strategy.
   */
  default DiffedField compare(Object currentValue, Object previousValue) {
    return compareType(objectAsType(currentValue), objectAsType(previousValue));
  }

  /**
   * Convert an object into type T. Throws IllegalArgumentException when object class not supported.
   */
  T objectAsType(Object value);

  /**
   * Do type specific comparison steps to construct a DiffedField.
   *
   * @return diffed field representation of difference.
   */
  DiffedField compareType(T currentValue, T previousValue);

  /**
   * Type specific representation of deleted value.
   */
  DiffedField createTypeDeletedDiffedField(T value);

  /**
   * Type specific representation of added value.
   */
  DiffedField createTypeAddedDiffedField(T value);

  /**
   * Type specific representation of a 'not diffed' value.
   */
  DiffedField createTypeNotDiffedField(T value);
}
