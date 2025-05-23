package uk.co.ogauthority.pathfinder.service.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.entity.communication.OrganisationGroupCommunication;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriberAccessor;

@Service
public class CommunicationSendingService {

  private final CommunicationService communicationService;
  private final OrganisationGroupCommunicationService organisationGroupCommunicationService;
  private final PortalTeamAccessor portalTeamAccessor;
  private final CommunicationEmailService communicationEmailService;
  private final SubscriberAccessor subscriberAccessor;

  @Autowired
  public CommunicationSendingService(CommunicationService communicationService,
                                     OrganisationGroupCommunicationService organisationGroupCommunicationService,
                                     PortalTeamAccessor portalTeamAccessor,
                                     CommunicationEmailService communicationEmailService,
                                     SubscriberAccessor subscriberAccessor) {
    this.communicationService = communicationService;
    this.organisationGroupCommunicationService = organisationGroupCommunicationService;
    this.portalTeamAccessor = portalTeamAccessor;
    this.communicationEmailService = communicationEmailService;
    this.subscriberAccessor = subscriberAccessor;
  }

  @Transactional
  public void sendCommunication(Integer communicationId) {

    final var communication = communicationService.getCommunicationOrError(communicationId);

    if (communication.getStatus().equals(CommunicationStatus.SENDING)) {
      final var communicationRecipients = getCommunicationRecipients(communication);
      communicationEmailService.sendCommunicationEmail(communication, communicationRecipients);
      communicationService.setCommunicationComplete(communication);
    } else {
      throw new RuntimeException(String.format(
          "Cannot send communication with id %d as status is %s when %s is required",
          communication.getId(),
          communication.getStatus(),
          CommunicationStatus.SENDING
      ));
    }
  }

  private List<Recipient> getCommunicationRecipients(Communication communication) {

    var recipients = new ArrayList<Recipient>();

    final var recipientType = communication.getRecipientType();

    if (recipientType.equals(RecipientType.OPERATORS)) {
      recipients.addAll(getOrganisationRecipients(communication));
    } else if (recipientType.equals(RecipientType.SUBSCRIBERS)) {
      recipients.addAll(getSubscriberRecipients());
    } else {
      throw new RuntimeException(String.format(
          "Unknown recipient type for communication with id %d",
          communication.getId()
      ));
    }

    return recipients;
  }

  private List<Recipient> getOrganisationRecipients(Communication communication) {

    final var organisationGroups =
        organisationGroupCommunicationService.getOrganisationGroupCommunications(communication)
            .stream()
            .map(OrganisationGroupCommunication::getOrganisationGroup)
            .collect(Collectors.toList());

    final var portalTeamResIds = portalTeamAccessor.findPortalTeamByOrganisationGroupsIn(organisationGroups)
        .stream()
        .map(PortalTeamDto::getResId)
        .collect(Collectors.toList());

    return portalTeamAccessor.getPortalTeamMemberPeople(portalTeamResIds)
        .stream()
        .map(Recipient::new)
        .collect(Collectors.toList());
  }

  private List<Recipient> getSubscriberRecipients() {
    return subscriberAccessor.getAllSubscribers()
        .stream()
        .map(this::convertSubscriberToRecipient)
        .collect(Collectors.toList());
  }

  private Recipient convertSubscriberToRecipient(Subscriber subscriber) {
    return new Recipient(
        subscriber.getEmailAddress(),
        subscriber.getForename(),
        subscriber.getSurname()
    );
  }
}