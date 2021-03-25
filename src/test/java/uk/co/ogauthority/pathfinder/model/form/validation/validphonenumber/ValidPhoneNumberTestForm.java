package uk.co.ogauthority.pathfinder.model.form.validation.validphonenumber;

import uk.co.ogauthority.pathfinder.model.form.validation.phonenumber.ValidPhoneNumber;

public class ValidPhoneNumberTestForm {
  public static final String PREFIX ="The telephone number";

  @ValidPhoneNumber(messagePrefix = PREFIX)
  private String phoneNumber;

  public ValidPhoneNumberTestForm() {
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
