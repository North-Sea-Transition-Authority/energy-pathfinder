package uk.co.ogauthority.pathfinder.service.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.communication.CommunicationForm;
import uk.co.ogauthority.pathfinder.repository.communication.CommunicationRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.CommunicationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class CommunicationServiceTest {

  @Mock
  private ValidationService validationService;

  @Mock
  private CommunicationRepository communicationRepository;

  @Mock
  private OrganisationGroupCommunicationService organisationGroupCommunicationService;

  private CommunicationService communicationService;

  @Before
  public void setup() {
    communicationService = new CommunicationService(validationService, communicationRepository, organisationGroupCommunicationService);

    when(communicationRepository.save(any(Communication.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void validateCommunicationForm_whenFullValidation_assertInteractions() {
    validateCommunicationForm_assertInteractions(ValidationType.FULL);
  }

  @Test
  public void validateCommunicationForm_whenPartialValidation_assertInteractions() {
    validateCommunicationForm_assertInteractions(ValidationType.PARTIAL);
  }

  private void validateCommunicationForm_assertInteractions(ValidationType validationType) {
    final var form = new CommunicationForm();
    final var bindingResult = new BeanPropertyBindingResult(form, "form");
    communicationService.validateCommunicationForm(form, bindingResult, validationType);
    verify(validationService, times(1)).validate(form, bindingResult, validationType);
  }

  @Test
  public void createCommunication() {
    final var communicationForm = CommunicationTestUtil.getCompleteCommunicationForm();
    final var communicationStatus = CommunicationStatus.DRAFT;
    final var user = UserTestingUtil.getAuthenticatedUserAccount();

    final var communication = communicationService.createCommunication(
        communicationForm,
        communicationStatus,
        user
    );

    assertCommonProperties(communication, communicationForm, communicationStatus, CommunicationJourneyStatus.START);
    assertThat(communication.getCreatedByWuaId()).isEqualTo(user.getWuaId());
    assertThat(communication.getCreatedDatetime()).isNotNull();
    assertThat(communication.getSubmittedByWuaId()).isNull();
    assertThat(communication.getSubmittedDatetime()).isNull();
  }

  @Test
  public void updateCommunication_whenOperatorRecipient_assertInteractions() {
    var communicationForm = CommunicationTestUtil.getCompleteCommunicationForm();
    communicationForm.setRecipientType(RecipientType.OPERATORS);

    final var communicationStatus = CommunicationStatus.DRAFT;
    final var communicationJourneyStage = CommunicationJourneyStatus.EMAIL_CONTENT_OPERATORS;

    final var communication = communicationService.updateCommunication(
        new Communication(),
        communicationForm,
        communicationStatus,
        communicationJourneyStage
    );

    assertCommonProperties(communication, communicationForm, communicationStatus, communicationJourneyStage);
    assertThat(communication.getSubmittedByWuaId()).isNull();
    assertThat(communication.getSubmittedDatetime()).isNull();

    verify(organisationGroupCommunicationService, never()).deleteOrganisationGroupCommunications(communication);
  }

  @Test
  public void updateCommunication_whenSubscriberRecipient_assertInteractions() {
    var communicationForm = CommunicationTestUtil.getCompleteCommunicationForm();
    communicationForm.setRecipientType(RecipientType.SUBSCRIBERS);

    final var communicationStatus = CommunicationStatus.DRAFT;
    final var communicationJourneyStage = CommunicationJourneyStatus.EMAIL_CONTENT_SUBSCRIBERS;

    final var communication = communicationService.updateCommunication(
        new Communication(),
        communicationForm,
        communicationStatus,
        communicationJourneyStage
    );
    assertCommonProperties(communication, communicationForm, communicationStatus, communicationJourneyStage);
    assertThat(communication.getSubmittedByWuaId()).isNull();
    assertThat(communication.getSubmittedDatetime()).isNull();

    verify(organisationGroupCommunicationService, times(1)).deleteOrganisationGroupCommunications(communication);
  }

  private void assertCommonProperties(Communication communication,
                                      CommunicationForm communicationForm,
                                      CommunicationStatus communicationStatus,
                                      CommunicationJourneyStatus communicationCommunicationJourneyStatus) {
    assertThat(communication.getRecipientType()).isEqualTo(communicationForm.getRecipientType());
    assertThat(communication.getEmailSubject()).isEqualTo(communicationForm.getSubject());
    assertThat(communication.getEmailBody()).isEqualTo(communicationForm.getBody());
    assertThat(communication.getStatus()).isEqualTo(communicationStatus);
    assertThat(communication.getLatestCommunicationJourneyStatus()).isEqualTo(communicationCommunicationJourneyStatus);
  }

  @Test
  public void getCommunicationOrError_whenFound_thenReturn() {
    var communication = new Communication();
    communication.setId(1);

    when(communicationRepository.findById(communication.getId())).thenReturn(Optional.of(communication));

    var result = communicationService.getCommunicationOrError(communication.getId());
    assertThat(result).isEqualTo(communication);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getCommunicationOrError_whenNotFound_thenException() {
    when(communicationRepository.findById(any())).thenReturn(Optional.empty());
    communicationService.getCommunicationOrError(1);
  }

  @Test
  public void getCommunicationForm() {
    final var communication = CommunicationTestUtil.getCompleteCommunication();
    final var form = communicationService.getCommunicationForm(communication);
    assertThat(form.getRecipientType()).isEqualTo(communication.getRecipientType());
    assertThat(form.getSubject()).isEqualTo(communication.getEmailSubject());
    assertThat(form.getBody()).isEqualTo(communication.getEmailBody());
  }

  @Test
  public void finaliseCommunication() {
    final var communication = CommunicationTestUtil.getCompleteCommunication();
    final var user = UserTestingUtil.getAuthenticatedUserAccount();

    var result = communicationService.finaliseCommunication(communication, user);

    assertThat(result.getSubmittedByWuaId()).isEqualTo(user.getWuaId());
    assertThat(result.getSubmittedDatetime()).isNotNull();
    assertThat(result.getStatus()).isEqualTo(CommunicationStatus.COMPLETE);
  }

  @Test
  public void updateCommunicationJourneyStatus() {
    final var journeyStage = CommunicationJourneyStatus.REVIEW_AND_SEND;

    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setLatestCommunicationJourneyStatus(journeyStage);

    var result = communicationService.updateCommunicationJourneyStatus(communication, journeyStage);
    assertThat(result.getLatestCommunicationJourneyStatus()).isEqualTo(journeyStage);
  }

}