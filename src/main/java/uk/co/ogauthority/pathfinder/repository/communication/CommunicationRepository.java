package uk.co.ogauthority.pathfinder.repository.communication;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;

@Repository
public interface CommunicationRepository extends CrudRepository<Communication, Integer> {

  List<Communication> findAllByStatusIn(List<CommunicationStatus> communicationStatuses);

}
