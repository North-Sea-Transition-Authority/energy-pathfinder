package uk.co.ogauthority.pathfinder.model.form.communication;

import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;

public class CommunicationForm {

  @NotNull(message = "Select the group this email is being sent to", groups = FullValidation.class)
  private RecipientType recipientType;

  @LengthRestrictedString(messagePrefix = "The email subject", groups = {FullValidation.class, PartialValidation.class})
  @NotNull(message = "Enter the email subject", groups = FullValidation.class)
  private String subject;

  @NotNull(message = "Enter the body of the email", groups = FullValidation.class)
  private String body;

  public RecipientType getRecipientType() {
    return recipientType;
  }

  public void setRecipientType(RecipientType recipientType) {
    this.recipientType = recipientType;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
