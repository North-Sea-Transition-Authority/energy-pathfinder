package uk.co.ogauthority.pathfinder.model.enums.contact;

public enum ContactDetail {

  TECHNICAL_SUPPORT(
      "UKOP service desk",
      "ukop@ogauthority.co.uk",
      "0300 067 1682",
      "For example, unexpected problems using the service or system errors being received"
  );

  private final String displayName;

  private final String emailAddress;

  private final String phoneNumber;

  private final String description;

  ContactDetail(String displayName, String emailAddress, String phoneNumber, String description) {
    this.displayName = displayName;
    this.emailAddress = emailAddress;
    this.phoneNumber = phoneNumber;
    this.description = description;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getDescription() {
    return description;
  }
}
