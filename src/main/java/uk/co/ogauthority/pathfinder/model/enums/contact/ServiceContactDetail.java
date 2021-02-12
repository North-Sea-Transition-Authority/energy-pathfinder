package uk.co.ogauthority.pathfinder.model.enums.contact;

public enum ServiceContactDetail {

  BUSINESS_SUPPORT(
      "Business support",
      "OGA",
      "pathfinder@ogauthority.co.uk",
      "",
      "For example, questions about filling in your project and the information you need to provide",
      true,
      10
  ),
  TECHNICAL_SUPPORT(
      "Technical support",
      "UKOP service desk",
      "ukop@ogauthority.co.uk",
      "0300 067 1682",
      "For example, unexpected problems using the service or system errors being received",
      true,
      20
  );

  private final String displayName;

  private final String serviceName;

  private final String emailAddress;

  private final String phoneNumber;

  private final String description;

  private final boolean shownOnContactPage;

  private final int displayOrder;

  ServiceContactDetail(String displayName,
                       String serviceName,
                       String emailAddress,
                       String phoneNumber,
                       String description,
                       boolean shownOnContactPage,
                       int displayOrder) {
    this.displayName = displayName;
    this.serviceName = serviceName;
    this.emailAddress = emailAddress;
    this.phoneNumber = phoneNumber;
    this.description = description;
    this.shownOnContactPage = shownOnContactPage;
    this.displayOrder = displayOrder;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getServiceName() {
    return serviceName;
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

  public boolean isShownOnContactPage() {
    return shownOnContactPage;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }
}
