package uk.co.ogauthority.pathfinder.service.difference;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import uk.co.ogauthority.pathfinder.model.difference.DiffableAsString;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.service.difference.comparisonstrategy.ComparisonStrategy;
import uk.co.ogauthority.pathfinder.service.difference.comparisonstrategy.ObjectDiffableAsStringStrategy;
import uk.co.ogauthority.pathfinder.service.difference.comparisonstrategy.StringComparisonStrategy;
import uk.co.ogauthority.pathfinder.service.difference.comparisonstrategy.StringWithTagComparisonStrategy;

public enum ComparisonType {

  STRING(Set.of(String.class, Integer.class, Boolean.class), new StringComparisonStrategy()),
  LIST(Set.of(List.class), null),
  STRING_WITH_TAG(Set.of(StringWithTag.class), new StringWithTagComparisonStrategy()),
  OBJECT_DIFFABLE_AS_STRING(Set.of(DiffableAsString.class), new ObjectDiffableAsStringStrategy()),
  NOT_SUPPORTED(Collections.emptySet(), null);

  private final Set<Class<?>> supportedClasses;
  private final ComparisonStrategy<?> comparisonStrategy;

  ComparisonType(Set<Class<?>> supportedClasses, ComparisonStrategy<?> comparisonStrategy) {
    this.supportedClasses = supportedClasses;
    this.comparisonStrategy = comparisonStrategy;
  }


  public Set<Class> getSupportedClasses() {
    return Collections.unmodifiableSet(this.supportedClasses);
  }

  public static ComparisonType findComparisonType(Class searchClass) {
    for (ComparisonType type : ComparisonType.values()) {
      for (Class<?> clazz : type.getSupportedClasses()) {
        if (clazz.equals(searchClass) || clazz.isAssignableFrom(searchClass)) {
          return type;
        }
      }
    }

    return NOT_SUPPORTED;
  }

  public ComparisonStrategy<?> getComparisonStrategy() {
    return comparisonStrategy;
  }
}
