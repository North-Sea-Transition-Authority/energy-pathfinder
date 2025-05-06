package uk.co.ogauthority.pathfinder.publicdata;

class ContactJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String name = "Test contact name";
    private String phoneNumber = "01303 123 456";
    private String jobTitle = "Test contact job title";
    private String emailAddress = "test@email.address";

    private Builder() {
    }

    Builder withName(String name) {
      this.name = name;
      return this;
    }

    Builder withPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    Builder withJobTitle(String jobTitle) {
      this.jobTitle = jobTitle;
      return this;
    }

    Builder withEmailAddress(String emailAddress) {
      this.emailAddress = emailAddress;
      return this;
    }

    ContactJson build() {
      return new ContactJson(name, phoneNumber, jobTitle, emailAddress);
    }
  }
}
