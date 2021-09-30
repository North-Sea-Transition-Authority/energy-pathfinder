package uk.co.ogauthority.pathfinder.energyportal.repository.organisation;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnit;

public interface PortalOrganisationUnitRepository extends CrudRepository<PortalOrganisationUnit, Integer> {

  @EntityGraph(attributePaths = "portalOrganisationGroup")
  List<PortalOrganisationUnit> findAll();

  List<PortalOrganisationUnit> findByNameContainingIgnoreCaseAndActiveTrue(String searchTerm);

  List<PortalOrganisationUnit> findByActiveTrueAndPortalOrganisationGroupIn(List<PortalOrganisationGroup> organisationGroups);

  List<PortalOrganisationUnit> findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn(
      String organisationUnit,
      List<PortalOrganisationGroup> organisationGroups
  );

  boolean existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId(int organisationUnitId, int organisationGroupId);
}
