package uk.co.ogauthority.pathfinder.service.communication;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.communication.CommunicationForm;
import uk.co.ogauthority.pathfinder.repository.communication.CommunicationRepository;
import uk.co.ogauthority.pathfinder.service.email.notify.DefaultEmailPersonalisationService;
import uk.co.ogauthority.pathfinder.service.scheduler.SchedulerService;
import uk.co.ogauthority.pathfinder.service.scheduler.communication.CommunicationJob;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class CommunicationService {

  private final ValidationService validationService;
  private final CommunicationRepository communicationRepository;
  private final OrganisationGroupCommunicationService organisationGroupCommunicationService;
  private final SchedulerService schedulerService;
  private final DefaultEmailPersonalisationService defaultEmailPersonalisationService;

  @Autowired
  public CommunicationService(ValidationService validationService,
                              CommunicationRepository communicationRepository,
                              OrganisationGroupCommunicationService organisationGroupCommunicationService,
                              SchedulerService schedulerService,
                              DefaultEmailPersonalisationService defaultEmailPersonalisationService) {
    this.validationService = validationService;
    this.communicationRepository = communicationRepository;
    this.organisationGroupCommunicationService = organisationGroupCommunicationService;
    this.schedulerService = schedulerService;
    this.defaultEmailPersonalisationService = defaultEmailPersonalisationService;
  }

  protected List<Communication> getCommunicationsWithStatuses(List<CommunicationStatus> communicationStatuses) {
    return communicationRepository.findAllByStatusIn(communicationStatuses);
  }

  public BindingResult validateCommunicationForm(CommunicationForm communicationForm,
                                                 BindingResult bindingResult,
                                                 ValidationType validationType) {
    return validationService.validate(communicationForm, bindingResult, validationType);
  }

  @Transactional
  public Communication createCommunication(CommunicationForm communicationForm,
                                           CommunicationStatus communicationStatus,
                                           AuthenticatedUserAccount user) {
    var communicationEntity = new Communication();
    communicationEntity.setCreatedDatetime(Instant.now());
    communicationEntity.setCreatedByWuaId(user.getWuaId());
    return updateCommunication(communicationEntity, communicationForm, communicationStatus, CommunicationJourneyStatus.START);
  }

  @Transactional
  public Communication updateCommunication(Communication communicationEntity,
                                           CommunicationForm communicationForm,
                                           CommunicationStatus status,
                                           CommunicationJourneyStatus communicationJourneyStatus) {
    communicationEntity.setRecipientType(communicationForm.getRecipientType());
    communicationEntity.setEmailSubject(communicationForm.getSubject());
    communicationEntity.setEmailBody(communicationForm.getBody());
    communicationEntity.setStatus(status);
    communicationEntity.setLatestCommunicationJourneyStatus(communicationJourneyStatus);
    communicationEntity.setGreetingText(DefaultEmailPersonalisationService.DEFAULT_GREETING_TEXT);
    communicationEntity.setSignOffText(DefaultEmailPersonalisationService.DEFAULT_SIGN_OFF_TEXT);
    communicationEntity.setSignOffIdentifier(defaultEmailPersonalisationService.getDefaultSignOffIdentifier());
    communicationEntity = communicationRepository.save(communicationEntity);

    if (RecipientType.SUBSCRIBERS.equals(communicationEntity.getRecipientType())) {
      organisationGroupCommunicationService.deleteOrganisationGroupCommunications(communicationEntity);
    }

    return communicationEntity;
  }

  @Transactional
  public Communication updateCommunicationJourneyStatus(Communication communication,
                                                        CommunicationJourneyStatus communicationJourneyStatus) {
    communication.setLatestCommunicationJourneyStatus(communicationJourneyStatus);
    return communicationRepository.save(communication);
  }

  private Optional<Communication> getCommunication(Integer communicationId) {
    return communicationRepository.findById(communicationId);
  }

  public Communication getCommunicationOrError(Integer communicationId) {
    return getCommunication(communicationId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Could not find Communication entity with id %d", communicationId))
        );
  }

  public CommunicationForm getCommunicationForm(Communication communication) {
    var form = new CommunicationForm();
    form.setRecipientType(communication.getRecipientType());
    form.setSubject(communication.getEmailSubject());
    form.setBody(communication.getEmailBody());
    return form;
  }

  protected Communication submitCommunication(Communication communication, AuthenticatedUserAccount user) {
    communication.setStatus(CommunicationStatus.SENDING);
    communication.setLatestCommunicationJourneyStatus(CommunicationJourneyStatus.REVIEW_AND_SEND);
    communication.setSubmittedByWuaId(user.getWuaId());
    communication.setSubmittedDatetime(Instant.now());
    return communicationRepository.save(communication);
  }

  protected Communication setCommunicationComplete(Communication communication) {
    communication.setStatus(CommunicationStatus.SENT);
    return communicationRepository.save(communication);
  }

  @Transactional
  public void finaliseCommunication(Communication communication, AuthenticatedUserAccount user) {
    submitCommunication(communication, user);
    scheduleCommunication(communication);
  }

  private void scheduleCommunication(Communication communication) {

    final var jobIdentifier = String.format(
        "CommunicationService.scheduleCommunication-communication-%s",
        communication.getId()
    );

    var jobData = new HashMap<String, Object>();
    jobData.put("communicationId", communication.getId());

    schedulerService.scheduleJob(jobIdentifier, jobData, CommunicationJob.class);
  }
}
