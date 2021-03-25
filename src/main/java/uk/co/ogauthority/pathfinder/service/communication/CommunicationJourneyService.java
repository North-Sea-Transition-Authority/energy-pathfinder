package uk.co.ogauthority.pathfinder.service.communication;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;

@Service
public class CommunicationJourneyService {

  private final CommunicationService communicationService;

  @Autowired
  public CommunicationJourneyService(CommunicationService communicationService) {
    this.communicationService = communicationService;
  }

  public Communication getCommunicationOrError(Integer communicationId) {
    return communicationService.getCommunicationOrError(communicationId);
  }

  public CommunicationContext checkJourneyStage(Communication communication,
                                         CommunicationJourneyStage communicationJourneyStage) {
    if (!hasPermittedRecipientType(communication, communicationJourneyStage)) {
      throw new AccessDeniedException(
          String.format(
              "Communication with id %s does not have the required recipient type: %s",
              communication.getId(),
              communicationJourneyStage.getPermittedRecipientTypes()
                  .stream()
                  .map(Enum::name)
                  .collect(Collectors.joining(","))
          )
      );
    }

    if (!hasPermittedCommunicationStatus(communication, communicationJourneyStage)) {
      throw new AccessDeniedException(
          String.format(
              "Communication with id %s does not have the required statuses %s",
              communication.getId(),
              communicationJourneyStage.getPermittedCommunicationStatuses()
                  .stream()
                  .map(Enum::name)
                  .collect(Collectors.joining(","))
          )
      );
    }

    if (!hasPermittedJourneyStatus(communication, communicationJourneyStage)) {
      throw new AccessDeniedException(
          String.format(
              "Communication with id %s does not have the required journey status %s",
              communication.getId(),
              communicationJourneyStage.getPermittedCommunicationJourneyStatuses()
                  .stream()
                  .map(Enum::name)
                  .collect(Collectors.joining(","))
          )
      );
    }

    return new CommunicationContext(communication);
  }

  private boolean hasPermittedRecipientType(Communication communication,
                                            CommunicationJourneyStage communicationJourneyStage) {
    final var permittedRecipientTypes = communicationJourneyStage.getPermittedRecipientTypes();
    return permittedRecipientTypes.isEmpty() || (communication.getRecipientType() != null
        && permittedRecipientTypes.contains(communication.getRecipientType()));

  }

  private boolean hasPermittedCommunicationStatus(Communication communication,
                                                  CommunicationJourneyStage communicationJourneyStage) {
    final var permittedStatuses = communicationJourneyStage.getPermittedCommunicationStatuses();
    return communication.getStatus() != null && permittedStatuses.contains(communication.getStatus());
  }

  private boolean hasPermittedJourneyStatus(Communication communication,
                                            CommunicationJourneyStage communicationJourneyStage) {
    final var permittedJourneyStages = communicationJourneyStage.getPermittedCommunicationJourneyStatuses();
    return communication.getLatestCommunicationJourneyStatus() != null
        && permittedJourneyStages.contains(communication.getLatestCommunicationJourneyStatus());
  }
}
