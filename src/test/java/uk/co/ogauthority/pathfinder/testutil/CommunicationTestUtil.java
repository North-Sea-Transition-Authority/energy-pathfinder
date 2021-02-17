package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.communication.CommunicationForm;
import uk.co.ogauthority.pathfinder.model.view.communication.CommunicationView;
import uk.co.ogauthority.pathfinder.model.view.communication.SentCommunicationView;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyStatus;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class CommunicationTestUtil {

  private static final RecipientType RECIPIENT_TYPE = RecipientType.OPERATORS;
  private static final String EMAIL_SUBJECT = "Email subject";
  private static final String EMAIL_BODY = "Email body";
  private static final String GREETING_TEXT = "Greeting";
  private static final String SIGN_OFF_TEXT = "Sign off";
  private static final String SIGN_OFF_IDENTIFIER = "Sign off identifier";
  private static final String SENDER_NAME = "Sender";
  private static final String RECIPIENT_LIST = "recipient@example.com";
  private static final String SUBMITTED_BY_USERNAME = "Someone";
  private static final String SUBMITTED_BY_EMAIL = "someone@example.com";
  private static final String FORMATTED_SUBMITTED_DATE = DateUtil.formatInstant(Instant.now());

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
        SENDER_NAME,
        RECIPIENT_LIST,
        EMAIL_SUBJECT,
        EMAIL_BODY,
        GREETING_TEXT,
        SIGN_OFF_TEXT,
        SIGN_OFF_IDENTIFIER
    );
  }

  public static SentCommunicationView getSentCommunicationView() {
    return new SentCommunicationView(
        SENDER_NAME,
        RECIPIENT_LIST,
        EMAIL_SUBJECT,
        EMAIL_BODY,
        GREETING_TEXT,
        SIGN_OFF_TEXT,
        SIGN_OFF_IDENTIFIER,
        SUBMITTED_BY_USERNAME,
        SUBMITTED_BY_EMAIL,
        FORMATTED_SUBMITTED_DATE
    );
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
