package uk.co.ogauthority.pathfinder.repository.dashboard;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.OperatorDashboardProjectItem;

@Repository
public interface OperatorDashboardProjectItemRepository extends CrudRepository<OperatorDashboardProjectItem, Integer> {

  List<DashboardProjectItem> findAllByOrganisationGroupIn(List<PortalOrganisationGroup> organisationGroups);

}
