package uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;

@Repository
public interface CollaborationOpportunitiesRepository extends CrudRepository<CollaborationOpportunity, Integer> {
}
