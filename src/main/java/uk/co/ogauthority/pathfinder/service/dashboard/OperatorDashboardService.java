package uk.co.ogauthority.pathfinder.service.dashboard;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.repository.dashboard.DashboardProjectItemRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Service
public class OperatorDashboardService {

  private final DashboardProjectItemRepository dashboardProjectItemRepository;
  private final TeamService teamService;

  @Autowired
  public OperatorDashboardService(DashboardProjectItemRepository dashboardProjectItemRepository,
                                  TeamService teamService) {
    this.dashboardProjectItemRepository = dashboardProjectItemRepository;
    this.teamService = teamService;
  }

  public List<DashboardProjectItem> getDashboardProjectItems(Person person) {
    var orgGroups = teamService.getOrganisationTeamsPersonIsMemberOf(person)
        .stream()
        .map(OrganisationTeam::getPortalOrganisationGroup)
        .collect(Collectors.toList());
    var dashboardItems = dashboardProjectItemRepository.findAllByOrganisationGroupInOrderByCreatedDatetimeDesc(orgGroups);

    return filter(dashboardItems, true);
  }

  //TODO filter object {@link DashboardFilter}
  //Thoughts on the filter object:
    //Filter object has it's own filter methods for each field rather than defining them in the service?
    //Do we Want a filter.apply or a filterService.apply(dashbaordItems, filter) type method to be how this is called.
  public List<DashboardProjectItem> filter(List<DashboardProjectItem> dashboardItems, boolean filter) { //TODO add filter object
    List<Predicate<DashboardProjectItem>> allPredicates = Arrays.asList(
        di -> titleMatches(di, "LM"),
        di -> testDefaultFilter(di, "test"),
        di -> fieldStageMatches(di, FieldStage.DECOMMISSIONING)
    );

    Predicate<DashboardProjectItem> compositePredicate = allPredicates.stream()
        .reduce(di -> true, Predicate::and);

    return dashboardItems.stream().filter(compositePredicate).collect(Collectors.toList());

  }

  private boolean fieldStageMatches(DashboardProjectItem di, FieldStage fieldStage) {
    return fieldStage.equals(di.getFieldStage());
  }

  //This will be the default behaviour of the xMatches function when a field is not set in the filter
  private boolean testDefaultFilter(DashboardProjectItem di, String test) {
    return true;
  }

  private boolean titleMatches(DashboardProjectItem di, String title) {
    return di.getProjectTitle() != null && di.getProjectTitle().toLowerCase().contains(title.toLowerCase());
  }

  public Stream<DashboardProjectItem> filterByProjectTitle(Stream<DashboardProjectItem> dashboardItems, String projectTitle) {
    return dashboardItems
        .filter(ui -> ui.getProjectTitle() != null)
        .filter(ui -> ui.getProjectTitle().toLowerCase().contains(projectTitle.toLowerCase()));
  }

  public Stream<DashboardProjectItem> filterByFieldStage(Stream<DashboardProjectItem> userItems, FieldStage fieldStage) {
    return userItems
        .filter(ui -> fieldStage.equals(ui.getFieldStage()));
  }
}
