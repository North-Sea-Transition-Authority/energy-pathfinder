package uk.co.ogauthority.pathfinder.service.dashboard;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.dashboard.DashboardProjectItemRepository;

@Service
public class RegulatorDashboardService {

  public static final List<ProjectStatus> REGULATOR_PROJECT_ACCESS_STATUSES = List.of(ProjectStatus.PUBLISHED, ProjectStatus.QA);

  private final DashboardProjectItemRepository dashboardProjectItemRepository;


  @Autowired
  public RegulatorDashboardService(DashboardProjectItemRepository dashboardProjectItemRepository) {
    this.dashboardProjectItemRepository = dashboardProjectItemRepository;
  }

  public List<DashboardProjectItem> getDashboardProjectItems() {
    return dashboardProjectItemRepository.findAllByStatusInOrderByCreatedDatetimeDesc(
        REGULATOR_PROJECT_ACCESS_STATUSES
    );
  }

}
