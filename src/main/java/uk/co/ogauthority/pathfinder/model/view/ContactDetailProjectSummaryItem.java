package uk.co.ogauthority.pathfinder.model.view;

import java.util.Objects;

public abstract class ContactDetailProjectSummaryItem extends ProjectSummaryItem {

  private String contactName;

  private String contactPhoneNumber;

  private String contactJobTitle;

  private String contactEmailAddress;

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactPhoneNumber() {
    return contactPhoneNumber;
  }

  public void setContactPhoneNumber(String contactPhoneNumber) {
    this.contactPhoneNumber = contactPhoneNumber;
  }

  public String getContactJobTitle() {
    return contactJobTitle;
  }

  public void setContactJobTitle(String contactJobTitle) {
    this.contactJobTitle = contactJobTitle;
  }

  public String getContactEmailAddress() {
    return contactEmailAddress;
  }

  public void setContactEmailAddress(String contactEmailAddress) {
    this.contactEmailAddress = contactEmailAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    if (!super.equals(o)) {
      return false;
    }

    ContactDetailProjectSummaryItem that = (ContactDetailProjectSummaryItem) o;
    return Objects.equals(contactName, that.contactName)
        && Objects.equals(contactPhoneNumber, that.contactPhoneNumber)
        && Objects.equals(contactJobTitle, that.contactJobTitle)
        && Objects.equals(contactEmailAddress, that.contactEmailAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        contactName,
        contactPhoneNumber,
        contactJobTitle,
        contactEmailAddress
    );
  }
}
