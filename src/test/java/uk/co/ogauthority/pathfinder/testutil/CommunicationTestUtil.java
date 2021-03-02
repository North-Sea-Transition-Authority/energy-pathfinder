package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.entity.communication.CommunicationRecipient;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.communication.CommunicationForm;
import uk.co.ogauthority.pathfinder.model.view.communication.CommunicationView;
import uk.co.ogauthority.pathfinder.model.view.communication.EmailView;
import uk.co.ogauthority.pathfinder.model.view.communication.SentCommunicationView;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyStatus;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class CommunicationTestUtil {

  private static final Integer COMMUNICATION_ID = 10;
  private static final RecipientType RECIPIENT_TYPE = RecipientType.OPERATORS;
  private static final String EMAIL_SUBJECT = "Email subject";
  private static final String EMAIL_BODY = "Email body";
  private static final String GREETING_TEXT = "Greeting";
  private static final String SIGN_OFF_TEXT = "Sign off";
  private static final String SIGN_OFF_IDENTIFIER = "Sign off identifier";
  private static final String SENDER_NAME = "Sender";
  private static final List<String> RECIPIENT_LIST = List.of("recipient@example.com");
  private static final String SUBMITTED_BY_USERNAME = "Someone";
  private static final String FORMATTED_SUBMITTED_DATE = DateUtil.formatInstant(Instant.now());
  private static final CommunicationStatus COMMUNICATION_STATUS = CommunicationStatus.SENT;

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

  public static CommunicationView getCommunicationView() {
    return new CommunicationView(
        COMMUNICATION_ID,
        getEmailView(),
        RECIPIENT_TYPE.getDisplayName()
    );
  }

  public static SentCommunicationView getSentCommunicationView() {
    return new SentCommunicationView(
        COMMUNICATION_ID,
        getEmailView(),
        RECIPIENT_TYPE.getDisplayName(),
        SUBMITTED_BY_USERNAME,
        FORMATTED_SUBMITTED_DATE
    );
  }

  public static CommunicationRecipient getCommunicationRecipient(String sentToEmailAddress) {
    var communicationRecipient = new CommunicationRecipient();
    communicationRecipient.setCommunication(getCompleteCommunication());
    communicationRecipient.setSentToEmailAddress(sentToEmailAddress);
    communicationRecipient.setSentInstant(Instant.now());
    return communicationRecipient;
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
    communication.setStatus(COMMUNICATION_STATUS);
    communication.setSubmittedDatetime(Instant.now());
    communication.setSubmittedByWuaId(1);
    return communication;
  }

  private static EmailView getEmailView() {
    return new EmailView(
        SENDER_NAME,
        RECIPIENT_LIST,
        EMAIL_SUBJECT,
        GREETING_TEXT,
        EMAIL_BODY,
        SIGN_OFF_TEXT,
        SIGN_OFF_IDENTIFIER
    );
  }
}
