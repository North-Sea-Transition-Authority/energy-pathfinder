package uk.co.ogauthority.pathfinder.repository.dashboard;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.OperatorDashboardProjectItem;

@Repository
public interface OperatorDashboardProjectItemRepository extends CrudRepository<OperatorDashboardProjectItem, Integer> {

  @Query("" +
      "SELECT DISTINCT odi " +
      "FROM OperatorDashboardProjectItem odi " +
      "LEFT JOIN ProjectContributor pc ON pc.projectDetail.id = odi.projectDetailId " +
      "WHERE (" +
      "   odi.organisationGroup IN(:organisationGroups) OR " +
      "   pc.contributionOrganisationGroup IN(:organisationGroups)" +
      ")")
  List<DashboardProjectItem> findAllByOrganisationGroupOrContributorIn(
      @Param("organisationGroups") List<PortalOrganisationGroup> organisationGroups
  );
}
