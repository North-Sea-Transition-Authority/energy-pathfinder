package uk.co.ogauthority.pathfinder.service.dashboard;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.dashboard.DashboardProjectItemRepository;

@Service
public class RegulatorDashboardService {

  public static final List<ProjectStatus> REGULATOR_PROJECT_ACCESS_STATUSES = List.of(ProjectStatus.PUBLISHED, ProjectStatus.QA);

  private final DashboardProjectItemRepository dashboardProjectItemRepository;
  private final DashboardFilterService filterService;


  @Autowired
  public RegulatorDashboardService(DashboardProjectItemRepository dashboardProjectItemRepository,
                                   DashboardFilterService filterService) {
    this.dashboardProjectItemRepository = dashboardProjectItemRepository;
    this.filterService = filterService;
  }

  public List<DashboardProjectItem> getDashboardProjectItems(DashboardFilter filter) {
    return filterService.filter(
      dashboardProjectItemRepository.findAllByStatusInOrderByCreatedDatetimeDesc(REGULATOR_PROJECT_ACCESS_STATUSES),
      filter
    );
  }

}
