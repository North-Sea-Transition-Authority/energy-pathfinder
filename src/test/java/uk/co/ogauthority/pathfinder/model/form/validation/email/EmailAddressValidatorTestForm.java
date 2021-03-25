package uk.co.ogauthority.pathfinder.model.form.validation.email;

public class EmailAddressValidatorTestForm {

  public static final String PREFIX = "The email address";

  @ValidEmail(messagePrefix = PREFIX)
  private String emailAddress;

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
}
