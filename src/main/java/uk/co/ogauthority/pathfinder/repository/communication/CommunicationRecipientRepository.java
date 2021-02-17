package uk.co.ogauthority.pathfinder.repository.communication;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.communication.CommunicationRecipient;

@Repository
public interface CommunicationRecipientRepository extends CrudRepository<CommunicationRecipient, Integer> {
}
