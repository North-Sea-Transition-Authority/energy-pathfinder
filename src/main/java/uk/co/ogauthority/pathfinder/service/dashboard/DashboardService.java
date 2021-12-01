package uk.co.ogauthority.pathfinder.service.dashboard;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.UnsupportedDashboardItemServiceException;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.DashboardFilterType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem.DashboardItemService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Service
public class DashboardService {

  public static final List<ProjectStatus> OPERATOR_STATUS_DEFAULTS = List.of(
      ProjectStatus.DRAFT,
      ProjectStatus.QA,
      ProjectStatus.PUBLISHED
  );
  public static final List<ProjectStatus> REGULATOR_STATUS_DEFAULTS = List.of(ProjectStatus.QA);

  private final TeamService teamService;
  private final RegulatorDashboardService regulatorDashboardService;
  private final OperatorDashboardService operatorDashboardService;
  private final List<DashboardItemService> dashboardItemServices;
  private final TemplateRenderingService templateRenderingService;

  @Autowired
  public DashboardService(TeamService teamService,
                          RegulatorDashboardService regulatorDashboardService,
                          OperatorDashboardService operatorDashboardService,
                          List<DashboardItemService> dashboardItemServices,
                          TemplateRenderingService templateRenderingService) {
    this.teamService = teamService;
    this.regulatorDashboardService = regulatorDashboardService;
    this.operatorDashboardService = operatorDashboardService;
    this.dashboardItemServices = dashboardItemServices;
    this.templateRenderingService = templateRenderingService;
  }

  private List<DashboardProjectItem> getDashboardProjectItemsForUser(
      AuthenticatedUserAccount user,
      DashboardFilterType filterType,
      DashboardFilter filter
  ) {
    var person = user.getLinkedPerson();
    return DashboardFilterType.REGULATOR.equals(filterType)
        ? regulatorDashboardService.getDashboardProjectItems(filter)
        : operatorDashboardService.getDashboardProjectItems(person, filter);
  }

  public List<DashboardProjectHtmlItem> getDashboardProjectHtmlItemsForUser(
      AuthenticatedUserAccount user,
      DashboardFilterType filterType,
      DashboardFilter filter
  ) {
    return getDashboardProjectItemsForUser(user, filterType, filter)
        .stream()
        .map(this::createDashboardProjectHtmlItem)
        .collect(Collectors.toList());
  }

  private DashboardProjectHtmlItem createDashboardProjectHtmlItem(DashboardProjectItem dashboardProjectItem) {

    final var projectType = dashboardProjectItem.getProjectType();

    final var dashboardItemService = dashboardItemServices
        .stream()
        .filter(dashboardService -> dashboardService.getSupportedProjectType().equals(projectType))
        .findFirst()
        .orElseThrow(() -> new UnsupportedDashboardItemServiceException(
            String.format(
                "Could not find implementation of DashboardItemService supporting projectDetail with ID %d and type %s",
                dashboardProjectItem.getProjectDetailId(),
                projectType
            )
        ));

    final var dashboardItemHtmlContent = templateRenderingService.render(
        dashboardItemService.getTemplatePath(),
        dashboardItemService.getTemplateModel(dashboardProjectItem),
        true
    );

    return new DashboardProjectHtmlItem(dashboardItemHtmlContent);
  }

  public DashboardFilterType getDashboardFilterType(AuthenticatedUserAccount user) {
    return teamService.isPersonMemberOfRegulatorTeam(user.getLinkedPerson()) ? DashboardFilterType.REGULATOR : DashboardFilterType.OPERATOR;
  }

  public DashboardFilter getDefaultFilterForUser(AuthenticatedUserAccount user) {
    return getDashboardFilterType(user).equals(DashboardFilterType.REGULATOR)
        ? new DashboardFilter(REGULATOR_STATUS_DEFAULTS)
        : new DashboardFilter(OPERATOR_STATUS_DEFAULTS);
  }
}
