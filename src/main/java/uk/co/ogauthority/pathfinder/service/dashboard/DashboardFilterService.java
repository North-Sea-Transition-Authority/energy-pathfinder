package uk.co.ogauthority.pathfinder.service.dashboard;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;

/**
 * A service to filter a user's dashboard based on a {@link DashboardFilter} object.
 * Each filter field has it's own xMatches(dashboardProjectItem, filter) method in this service.
 * The default behaviour for the method is to return true if the filter field for it is not set, else apply the filter.
 */
@Service
public class DashboardFilterService {

  /**
   * Apply all the possible filters to the dashboardItems and return the result.
   * @param dashboardItems the user's list of unfiltered {@link DashboardProjectItem}
   * @param filter the users {@link DashboardFilter}
   * @return the filtered list of DashboardProjectItems
   */
  public List<DashboardProjectItem> filter(List<DashboardProjectItem> dashboardItems, DashboardFilter filter) {
    List<Predicate<DashboardProjectItem>> allPredicates = Arrays.asList(
        di -> operatorMatches(di, filter),
        di -> titleMatches(di, filter),
        di -> fieldMatches(di, filter),
        di -> fieldStageMatches(di, filter),
        di -> ukcsAreaMatches(di, filter),
        di -> statusMatches(di, filter)
    );

    Predicate<DashboardProjectItem> compositePredicate = allPredicates.stream()
        .reduce(di -> true, Predicate::and);

    return dashboardItems.stream().filter(compositePredicate)
        .sorted(Comparator.comparing(DashboardProjectItem::getSortKey).reversed())
        .sorted(Comparator.comparing(DashboardProjectItem::getProjectTypeSortKey))
        .sorted(Comparator.comparing(DashboardProjectItem::getUpdateSortKey))
        .collect(Collectors.toList());
  }


  public boolean operatorMatches(DashboardProjectItem di, DashboardFilter filter) {
    if (filter.getOperatorName() != null) {
      return di.getOperatorName() != null && di.getOperatorName().toLowerCase().contains(filter.getOperatorName().toLowerCase());
    }
    return true;
  }

  public boolean titleMatches(DashboardProjectItem di, DashboardFilter filter) {
    if (filter.getProjectTitle() != null) {
      return di.getProjectTitle() != null && di.getProjectTitle().toLowerCase().contains(filter.getProjectTitle().toLowerCase());
    }
    return true;
  }


  public boolean fieldStageMatches(DashboardProjectItem di, DashboardFilter filter) {
    return filter.getFieldStages() == null || filter.getFieldStages().contains(di.getFieldStage());
  }


  public boolean fieldMatches(DashboardProjectItem di, DashboardFilter filter) {
    if (filter.getField() != null) {
      return di.getFieldName() != null && di.getFieldName().toLowerCase().contains(filter.getField().toLowerCase());
    }
    return true;
  }


  public boolean ukcsAreaMatches(DashboardProjectItem di, DashboardFilter filter) {
    return filter.getUkcsAreas() == null || filter.getUkcsAreas().contains(di.getUkcsArea());
  }


  public boolean statusMatches(DashboardProjectItem di, DashboardFilter filter) {
    return filter.getProjectStatusList() == null || filter.getProjectStatusList().contains(di.getStatus());
  }
}
