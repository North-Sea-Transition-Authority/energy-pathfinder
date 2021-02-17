package uk.co.ogauthority.pathfinder.service.communication;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.repository.communication.CommunicationRecipientRepository;
import uk.co.ogauthority.pathfinder.testutil.CommunicationTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class CommunicationRecipientServiceTest {

  @Mock
  private CommunicationRecipientRepository communicationRecipientRepository;

  private CommunicationRecipientService communicationRecipientService;

  @Before
  public void setup() {
    communicationRecipientService = new CommunicationRecipientService(communicationRecipientRepository);
  }

  @Test
  public void saveCommunicationRecipients_verifyInteractions() {
    final var communicationRecipientList = List.of(
        CommunicationTestUtil.getCommunicationRecipient("someone@example.com"),
        CommunicationTestUtil.getCommunicationRecipient("someone.else@example.com")
    );
    communicationRecipientService.saveCommunicationRecipients(communicationRecipientList);
    verify(communicationRecipientRepository, times(1)).saveAll(communicationRecipientList);
  }

}