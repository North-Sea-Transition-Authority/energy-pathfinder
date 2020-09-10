package uk.co.ogauthority.pathfinder.model.form.forminput.contact;

public class ContactDetailTestEntity implements ContactDetailCapture {

  private String name;

  private String jobTitle;

  private String phoneNumber;

  private String emailAddress;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getPhoneNumber() {
    return phoneNumber;
  }

  @Override
  public String getJobTitle() {
    return jobTitle;
  }

  @Override
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
}
