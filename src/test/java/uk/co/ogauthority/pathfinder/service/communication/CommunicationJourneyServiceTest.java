package uk.co.ogauthority.pathfinder.service.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.testutil.CommunicationTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class CommunicationJourneyServiceTest {

  @Mock
  private CommunicationService communicationService;

  private Communication communication;

  private CommunicationJourneyService communicationJourneyService;

  @Before
  public void setup() {
    communicationJourneyService = new CommunicationJourneyService(communicationService);
    communication = CommunicationTestUtil.getCompleteCommunication();
  }

  @Test
  public void checkJourneyStage_whenPermittedRecipientType_thenReturn() {
    communication.setRecipientType(RecipientType.OPERATORS);
    communication.setLatestCommunicationJourneyStatus(CommunicationJourneyStatus.EMAIL_CONTENT_OPERATORS);

    var result = communicationJourneyService.checkJourneyStage(
        communication,
        CommunicationJourneyStage.OPERATOR_SELECT
    );
    assertThat(result.getCommunication()).isEqualTo(communication);
  }

  @Test(expected = AccessDeniedException.class)
  public void checkJourneyStage_whenNoPermittedRecipientType_thenAccessDenied() {
    communication.setRecipientType(RecipientType.SUBSCRIBERS);
    communication.setLatestCommunicationJourneyStatus(CommunicationJourneyStatus.EMAIL_CONTENT_OPERATORS);

    communicationJourneyService.checkJourneyStage(
        communication,
        CommunicationJourneyStage.OPERATOR_SELECT
    );
  }

  @Test
  public void checkJourneyStage_whenPermittedCommunicationStatus_thenReturn() {
    communication.setStatus(CommunicationStatus.DRAFT);

    var result = communicationJourneyService.checkJourneyStage(
        communication,
        CommunicationJourneyStage.EMAIL_CONTENT
    );
    assertThat(result.getCommunication()).isEqualTo(communication);
  }

  @Test(expected = AccessDeniedException.class)
  public void checkJourneyStage_whenNoPermittedCommunicationStatus_thenAccessDenied() {
    communication.setStatus(CommunicationStatus.COMPLETE);

    communicationJourneyService.checkJourneyStage(
        communication,
        CommunicationJourneyStage.EMAIL_CONTENT
    );
  }

  @Test
  public void checkJourneyStage_whenPermittedJourneyStatus_thenReturn() {
    communication.setLatestCommunicationJourneyStatus(CommunicationJourneyStatus.OPERATOR_SELECT);

    var result = communicationJourneyService.checkJourneyStage(
        communication,
        CommunicationJourneyStage.REVIEW_AND_SEND
    );
    assertThat(result.getCommunication()).isEqualTo(communication);
  }

  @Test(expected = AccessDeniedException.class)
  public void checkJourneyStage_whenNoPermittedJourneyStatus_thenAccessDenied() {
    communication.setLatestCommunicationJourneyStatus(CommunicationJourneyStatus.START);

    communicationJourneyService.checkJourneyStage(
        communication,
        CommunicationJourneyStage.REVIEW_AND_SEND
    );
  }

  @Test
  public void getCommunicationOrError_whenFound_thenReturn() {
    when(communicationService.getCommunicationOrError(communication.getId())).thenReturn(communication);
    var result = communicationJourneyService.getCommunicationOrError(communication.getId());
    assertThat(result).isEqualTo(communication);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getCommunicationOrError_whenNotFound_thenException() {
    when(communicationService.getCommunicationOrError(communication.getId())).thenThrow(new PathfinderEntityNotFoundException(""));
    communicationJourneyService.getCommunicationOrError(communication.getId());
  }

}