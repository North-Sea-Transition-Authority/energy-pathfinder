package uk.co.ogauthority.pathfinder.repository.dashboard;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.RegulatorDashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;

@Repository
public interface RegulatorDashboardProjectItemRepository extends CrudRepository<RegulatorDashboardProjectItem, Integer> {

  List<DashboardProjectItem> findAllByStatusIn(
      List<ProjectStatus> statuses
  );

}
