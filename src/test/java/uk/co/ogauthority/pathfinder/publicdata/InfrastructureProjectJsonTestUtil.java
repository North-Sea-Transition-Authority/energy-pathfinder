package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDateTime;

class InfrastructureProjectJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Builder() {
    }

    private Integer id = 1;
    private InfrastructureProjectDetailsJson details = InfrastructureProjectDetailsJsonTestUtil.newBuilder().build();
    private ContactJson contact = ContactJsonTestUtil.newBuilder().build();
    private String firstProductionDateQuarter;
    private Integer firstProductionDateYear;
    private InfrastructureProjectLocationJson location = InfrastructureProjectLocationJsonTestUtil.newBuilder().build();
    private LocalDateTime submittedOn = LocalDateTime.of(2024, 10, 29, 11, 20, 38, 424521789);

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withDetails(InfrastructureProjectDetailsJson details) {
      this.details = details;
      return this;
    }

    Builder withContact(ContactJson contact) {
      this.contact = contact;
      return this;
    }

    Builder withFirstProductionDateQuarter(String firstProductionDateQuarter) {
      this.firstProductionDateQuarter = firstProductionDateQuarter;
      return this;
    }

    Builder withFirstProductionDateYear(Integer firstProductionDateYear) {
      this.firstProductionDateYear = firstProductionDateYear;
      return this;
    }

    Builder withLocation(InfrastructureProjectLocationJson location) {
      this.location = location;
      return this;
    }

    Builder withSubmittedOn(LocalDateTime submittedOn) {
      this.submittedOn = submittedOn;
      return this;
    }

    InfrastructureProjectJson build() {
      return new InfrastructureProjectJson(
          id,
          details,
          contact,
          firstProductionDateQuarter,
          firstProductionDateYear,
          location,
          submittedOn
      );
    }
  }
}
