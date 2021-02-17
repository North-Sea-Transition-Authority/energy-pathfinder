package uk.co.ogauthority.pathfinder.service.communication;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.communication.CommunicationRecipient;
import uk.co.ogauthority.pathfinder.repository.communication.CommunicationRecipientRepository;

@Service
class CommunicationRecipientService {

  private final CommunicationRecipientRepository communicationRecipientRepository;

  @Autowired
  CommunicationRecipientService(CommunicationRecipientRepository communicationRecipientRepository) {
    this.communicationRecipientRepository = communicationRecipientRepository;
  }

  void saveCommunicationRecipients(List<CommunicationRecipient> communicationRecipients) {
    communicationRecipientRepository.saveAll(communicationRecipients);
  }
}
