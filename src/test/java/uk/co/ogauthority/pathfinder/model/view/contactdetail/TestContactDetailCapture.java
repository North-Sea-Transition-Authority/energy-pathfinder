package uk.co.ogauthority.pathfinder.model.view.contactdetail;

import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;

public class TestContactDetailCapture implements ContactDetailCapture {

  private String name;

  private String phoneNumber;

  private String emailAddress;

  private String jobTitle;

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @Override
  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }
}
