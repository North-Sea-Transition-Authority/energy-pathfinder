package uk.co.ogauthority.pathfinder.energyportal.repository.organisation;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;


public interface PortalOrganisationGroupRepository extends CrudRepository<PortalOrganisationGroup, Integer> {

  List<PortalOrganisationGroup> findByUrefValueIn(List<String> organisationGroupUrefValues);
}
