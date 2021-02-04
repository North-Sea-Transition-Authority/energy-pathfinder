package uk.co.ogauthority.pathfinder.service.communication;

import java.util.Set;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;

public enum CommunicationJourneyStage {
  EMAIL_CONTENT(
      Set.of(),
      Set.of(CommunicationStatus.DRAFT),
      Set.of(CommunicationJourneyStatus.values())
  ),
  OPERATOR_SELECT(
      Set.of(RecipientType.OPERATORS),
      Set.of(CommunicationStatus.DRAFT),
      Set.of(CommunicationJourneyStatus.EMAIL_CONTENT_OPERATORS, CommunicationJourneyStatus.OPERATOR_SELECT)
  ),
  REVIEW_AND_SEND(
      Set.of(RecipientType.values()),
      Set.of(CommunicationStatus.DRAFT),
      Set.of(
          CommunicationJourneyStatus.OPERATOR_SELECT,
          CommunicationJourneyStatus.EMAIL_CONTENT_SUBSCRIBERS,
          CommunicationJourneyStatus.REVIEW_AND_SEND
      )
  );

  private final Set<RecipientType> permittedRecipientTypes;

  private final Set<CommunicationStatus> permittedCommunicationStatuses;

  private final Set<CommunicationJourneyStatus> permittedCommunicationJourneyStatuses;

  CommunicationJourneyStage(Set<RecipientType> permittedRecipientTypes,
                            Set<CommunicationStatus> permittedCommunicationStatuses,
                            Set<CommunicationJourneyStatus> permittedCommunicationJourneyStatuses) {
    this.permittedRecipientTypes = permittedRecipientTypes;
    this.permittedCommunicationStatuses = permittedCommunicationStatuses;
    this.permittedCommunicationJourneyStatuses = permittedCommunicationJourneyStatuses;
  }

  public Set<RecipientType> getPermittedRecipientTypes() {
    return permittedRecipientTypes;
  }

  public Set<CommunicationStatus> getPermittedCommunicationStatuses() {
    return permittedCommunicationStatuses;
  }

  public Set<CommunicationJourneyStatus> getPermittedCommunicationJourneyStatuses() {
    return permittedCommunicationJourneyStatuses;
  }
}
