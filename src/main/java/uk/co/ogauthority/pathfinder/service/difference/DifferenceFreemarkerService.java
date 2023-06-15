package uk.co.ogauthority.pathfinder.service.difference;

import freemarker.ext.beans.HashAdapter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.difference.DiffedField;
import uk.co.ogauthority.pathfinder.model.difference.DifferenceType;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;

/**
 * Intended to be injected into freemarker templates for utils to help with diffs.
 */
@Service
public class DifferenceFreemarkerService {

  public boolean isDiffableFieldIgnored(String fieldName) {
    return Arrays.stream(ProjectSummaryItem.class.getDeclaredFields())
        .anyMatch(field -> field.getName().equals(fieldName));
  }

  public <T> boolean areAllFieldsDeleted(T diffObject) throws IllegalAccessException, NoSuchFieldException {
    if (!(diffObject instanceof Map)) {
      throw new RuntimeException("Diffed field is not of type: HashAdapter");
    }
    var usableDiffObject = (Map<?, ?>) diffObject;
    @SuppressWarnings(value = "unchecked")
    var diffKeys = (Set<String>) usableDiffObject.keySet();
    return diffKeys.stream()
        // Only get diffed fields
        .filter(fieldName -> usableDiffObject.get(fieldName) instanceof DiffedField)
        // Remove fields marked as deleted
        .filter(fieldName -> {
          var diffField = (DiffedField) usableDiffObject.get(fieldName);
          var fieldIsDeleted = diffField.getDifferenceType().equals(DifferenceType.DELETED);
          if (!fieldIsDeleted) {
            if (diffField.getDifferenceType().equals(DifferenceType.UNCHANGED)) {
              // If field has no content then assume deletable.
              return !diffField.getCurrentValue().isBlank();
            }
          }
          // If field is not deleted, keep in stream as not all fields are deleted.
          return !fieldIsDeleted;
        })
        // Ensure that no un-ignored fields are remaining
        .noneMatch(field -> {
          var localizedName = field.split("_")[1];
          return !isDiffableFieldIgnored(localizedName);
        });
  }

}
