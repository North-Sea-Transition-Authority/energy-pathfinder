package uk.co.ogauthority.pathfinder.service.communication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriberAccessor;
import uk.co.ogauthority.pathfinder.testutil.CommunicationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.SubscriptionTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class CommunicationSendingServiceTest {

  @Mock
  private CommunicationService communicationService;

  @Mock
  private OrganisationGroupCommunicationService organisationGroupCommunicationService;

  @Mock
  private PortalTeamAccessor portalTeamAccessor;

  @Mock
  private CommunicationEmailService communicationEmailService;

  @Mock
  private SubscriberAccessor subscriberAccessor;

  private CommunicationSendingService communicationSendingService;

  @Before
  public void setup() {
    communicationSendingService = new CommunicationSendingService(
        communicationService,
        organisationGroupCommunicationService,
        portalTeamAccessor,
        communicationEmailService,
        subscriberAccessor
    );
  }

  @Test
  public void sendCommunication_whenNonDraftOperatorCommunication_verifyInteractions() {
    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setRecipientType(RecipientType.OPERATORS);
    communication.setStatus(CommunicationStatus.SENDING);

    when(communicationService.getCommunicationOrError(communication.getId())).thenReturn(communication);

    final var personList = List.of(
        new Person(1, "Someone", "Example", "someone@example.com", "123"),
        new Person(2, "Someone", "Else", "someone.else@example.com", "123")
    );
    when(portalTeamAccessor.getPortalTeamMemberPeople(any())).thenReturn(personList);

    communicationSendingService.sendCommunication(communication.getId());

    verify(organisationGroupCommunicationService, times(1))
        .getOrganisationGroupCommunications(communication);

    verify(portalTeamAccessor, times(1)).findPortalTeamByOrganisationGroupsIn(any());

    verify(communicationEmailService, times(1)).sendCommunicationEmail(
        communication,
        List.of(new Recipient(personList.get(0)), new Recipient(personList.get(1)))
    );
  }

  @Test(expected = RuntimeException.class)
  public void sendCommunication_whenDraftOperatorCommunication_thenException() {
    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setRecipientType(RecipientType.OPERATORS);
    communication.setStatus(CommunicationStatus.DRAFT);

    when(communicationService.getCommunicationOrError(communication.getId())).thenReturn(communication);

    communicationSendingService.sendCommunication(communication.getId());
  }

  @Test
  public void sendCommunication_whenDraftSubscriberCommunication_verifyInteractions() {
    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setRecipientType(RecipientType.SUBSCRIBERS);
    communication.setStatus(CommunicationStatus.SENDING);

    when(communicationService.getCommunicationOrError(communication.getId())).thenReturn(communication);

    final var subscriber1 = SubscriptionTestUtil.createSubscriber("someone@example.com");
    final var subscriber2 = SubscriptionTestUtil.createSubscriber("someone.else@example.com");
    final var subscribers = List.of(subscriber1, subscriber2);

    when(subscriberAccessor.getAllSubscribers()).thenReturn(subscribers);

    communicationSendingService.sendCommunication(communication.getId());

    verify(subscriberAccessor, times(1)).getAllSubscribers();

    verify(communicationEmailService, times(1)).sendCommunicationEmail(
        communication,
        List.of(
            new Recipient(subscriber1.getEmailAddress(), subscriber1.getForename(), subscriber1.getSurname()),
            new Recipient(subscriber2.getEmailAddress(), subscriber2.getForename(), subscriber2.getSurname())
        )
    );
  }

  @Test(expected = RuntimeException.class)
  public void sendCommunication_whenDraftSubscriberCommunication_thenException() {
    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setRecipientType(RecipientType.SUBSCRIBERS);
    communication.setStatus(CommunicationStatus.DRAFT);

    when(communicationService.getCommunicationOrError(communication.getId())).thenReturn(communication);

    communicationSendingService.sendCommunication(communication.getId());
  }

  @Test(expected = RuntimeException.class)
  public void sendCommunication_whenNoStatusCommunication_thenException() {
    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setStatus(null);

    when(communicationService.getCommunicationOrError(communication.getId())).thenReturn(communication);

    communicationSendingService.sendCommunication(communication.getId());
  }

  @Test(expected = RuntimeException.class)
  public void sendCommunication_whenNoRecipientType_thenException() {
    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setStatus(CommunicationStatus.SENT);
    communication.setRecipientType(null);

    when(communicationService.getCommunicationOrError(communication.getId())).thenReturn(communication);

    communicationSendingService.sendCommunication(communication.getId());
  }

}