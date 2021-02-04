package uk.co.ogauthority.pathfinder.repository.communication;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;

@Repository
public interface CommunicationRepository extends CrudRepository<Communication, Integer> {
}
