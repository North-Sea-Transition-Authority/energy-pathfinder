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
import uk.co.ogauthority.pathfinder.testutil.CommunicationTestUtil;

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

  private CommunicationSendingService communicationSendingService;

  @Before
  public void setup() {
    communicationSendingService = new CommunicationSendingService(
        communicationService,
        organisationGroupCommunicationService,
        portalTeamAccessor,
        communicationEmailService
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
    communication.setStatus(CommunicationStatus.COMPLETE);
    communication.setRecipientType(null);

    when(communicationService.getCommunicationOrError(communication.getId())).thenReturn(communication);

    communicationSendingService.sendCommunication(communication.getId());
  }

}