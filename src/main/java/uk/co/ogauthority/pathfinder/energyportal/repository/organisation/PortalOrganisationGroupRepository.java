package uk.co.ogauthority.pathfinder.energyportal.repository.organisation;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;

@Repository
public interface PortalOrganisationGroupRepository extends CrudRepository<PortalOrganisationGroup, Integer> {

  List<PortalOrganisationGroup> findByUrefValueIn(List<String> organisationGroupUrefValues);

  List<PortalOrganisationGroup> findByNameContainingIgnoreCase(String searchTerm);
}
