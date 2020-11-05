package uk.co.ogauthority.pathfinder.model.view.contactdetail;

import java.util.Objects;

public class ContactDetailView {

  private String name;

  private String phoneNumber;

  private String jobTitle;

  private String emailAddress;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContactDetailView that = (ContactDetailView) o;
    return Objects.equals(name, that.name)
        && Objects.equals(phoneNumber, that.phoneNumber)
        && Objects.equals(jobTitle, that.jobTitle)
        && Objects.equals(emailAddress, that.emailAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, phoneNumber, jobTitle, emailAddress);
  }
}
