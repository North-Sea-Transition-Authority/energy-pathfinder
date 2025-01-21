package uk.co.ogauthority.pathfinder.publicdata;

class WellboreJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String registrationNumber = "48/14-G7";
    private String mechanicalStatus = "DRILLING";

    private Builder() {
    }

    Builder withRegistrationNumber(String registrationNumber) {
      this.registrationNumber = registrationNumber;
      return this;
    }

    Builder withMechanicalStatus(String mechanicalStatus) {
      this.mechanicalStatus = mechanicalStatus;
      return this;
    }

    WellboreJson build() {
      return new WellboreJson(registrationNumber, mechanicalStatus);
    }
  }
}
