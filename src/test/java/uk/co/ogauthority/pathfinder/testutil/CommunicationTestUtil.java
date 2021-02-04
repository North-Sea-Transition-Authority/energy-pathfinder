package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.communication.CommunicationForm;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyStatus;

public class CommunicationTestUtil {

  private static final RecipientType RECIPIENT_TYPE = RecipientType.OPERATORS;
  private static final String EMAIL_SUBJECT = "Email subject";
  private static final String EMAIL_BODY = "Email body";

  public static CommunicationForm getCompleteCommunicationForm() {
    return getCommunicationForm(RECIPIENT_TYPE, EMAIL_SUBJECT, EMAIL_BODY);
  }

  private static CommunicationForm getCommunicationForm(RecipientType recipientType,
                                                        String emailSubject,
                                                        String emailBody) {
    var communicationForm = new CommunicationForm();
    communicationForm.setRecipientType(recipientType);
    communicationForm.setSubject(emailSubject);
    communicationForm.setBody(emailBody);
    return communicationForm;
  }

  public static Communication getCompleteCommunication() {
    return getCompleteCommunication(RECIPIENT_TYPE, EMAIL_SUBJECT, EMAIL_BODY);
  }

  private static Communication getCompleteCommunication(RecipientType recipientType,
                                                       String emailSubject,
                                                       String emailBody) {
    var communication = new Communication();
    communication.setId(1);
    communication.setCreatedByWuaId(1);
    communication.setCreatedDatetime(Instant.now());
    communication.setRecipientType(recipientType);
    communication.setEmailSubject(emailSubject);
    communication.setEmailBody(emailBody);
    communication.setLatestCommunicationJourneyStatus(CommunicationJourneyStatus.START);
    communication.setStatus(CommunicationStatus.DRAFT);
    return communication;
  }
}
