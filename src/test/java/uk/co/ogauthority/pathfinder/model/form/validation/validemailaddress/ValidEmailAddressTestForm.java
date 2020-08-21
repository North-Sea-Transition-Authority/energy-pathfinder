package uk.co.ogauthority.pathfinder.model.form.validation.validemailaddress;

import uk.co.ogauthority.pathfinder.model.form.validation.email.ValidEmail;

public class ValidEmailAddressTestForm {
  public static final String PREFIX = "The email";

  @ValidEmail(messagePrefix = PREFIX)
  private String emailAddress;

  public ValidEmailAddressTestForm() {
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
}
