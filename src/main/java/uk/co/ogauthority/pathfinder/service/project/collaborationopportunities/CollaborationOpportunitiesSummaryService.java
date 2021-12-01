package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;

@Service
public abstract class CollaborationOpportunitiesSummaryService<E, V> {

  /**
   * Method to return a view class which represents entity of type E.
   * @param opportunity the opportunity to create the view from
   * @param displayOrder the display order the view should have
   * @return return a view class which represents entity of type E
   */
  public abstract V getView(
      E opportunity,
      Integer displayOrder
  );

  /**
   * Method to return a view class which represents entity of type E.
   * @param opportunity the opportunity to create the view from
   * @param displayOrder the display order the view should have
   * @param isValid indicates if the underlying entity is valid
   * @return return a view class which represents entity of type E
   */
  public abstract V getView(
      E opportunity,
      Integer displayOrder,
      boolean isValid
  );

  /**
   * Method to determine if a entity of type E is valid based on the validation type.
   * @param opportunity the opportunity to validate
   * @param validationType the validation type to validate with
   * @return true if the entity is valid, false otherwise
   */
  protected abstract boolean isValid(
      E opportunity,
      ValidationType validationType
  );

  protected List<V> constructCollaborationOpportunityViews(
      List<E> collaborationOpportunities,
      ValidationType validationType
  ) {
    return IntStream.range(0, collaborationOpportunities.size())
        .mapToObj(index -> {

          var collaborationOpportunity = collaborationOpportunities.get(index);
          var displayIndex = index + 1;

          return validationType.equals(ValidationType.NO_VALIDATION)
              ? getView(collaborationOpportunity, displayIndex)
              : getView(
                  collaborationOpportunity,
                  displayIndex,
                  isValid(collaborationOpportunity, validationType)
              );
        })
        .collect(Collectors.toList());
  }

}
