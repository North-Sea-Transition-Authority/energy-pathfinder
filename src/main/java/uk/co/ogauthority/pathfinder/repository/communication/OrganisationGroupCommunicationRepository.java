package uk.co.ogauthority.pathfinder.repository.communication;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.entity.communication.OrganisationGroupCommunication;

@Repository
public interface OrganisationGroupCommunicationRepository
    extends CrudRepository<OrganisationGroupCommunication, Integer> {

  List<OrganisationGroupCommunication> findByCommunication(Communication communication);

  void deleteByCommunication(Communication communication);
}
