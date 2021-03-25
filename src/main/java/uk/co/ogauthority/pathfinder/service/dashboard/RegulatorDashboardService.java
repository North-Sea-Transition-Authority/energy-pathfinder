package uk.co.ogauthority.pathfinder.service.dashboard;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.dashboard.RegulatorDashboardProjectItemRepository;

@Service
public class RegulatorDashboardService {

  public static final List<ProjectStatus> REGULATOR_PROJECT_ACCESS_STATUSES = List.of(
      ProjectStatus.PUBLISHED,
      ProjectStatus.QA,
      ProjectStatus.ARCHIVED
  );

  private final RegulatorDashboardProjectItemRepository regulatorDashboardProjectItemRepository;
  private final DashboardFilterService filterService;


  @Autowired
  public RegulatorDashboardService(RegulatorDashboardProjectItemRepository regulatorDashboardProjectItemRepository,
                                   DashboardFilterService filterService) {
    this.regulatorDashboardProjectItemRepository = regulatorDashboardProjectItemRepository;
    this.filterService = filterService;
  }

  public List<DashboardProjectItem> getDashboardProjectItems(DashboardFilter filter) {
    return filterService.filter(
      regulatorDashboardProjectItemRepository.findAllByStatusIn(REGULATOR_PROJECT_ACCESS_STATUSES),
      filter
    );
  }

}
