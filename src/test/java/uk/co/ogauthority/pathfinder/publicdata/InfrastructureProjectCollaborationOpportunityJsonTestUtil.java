package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.enums.project.Function;

class InfrastructureProjectCollaborationOpportunityJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer id = 1;
    private String function = Function.LOGISTICS.name();
    private String manualFunction;
    private String descriptionOfWork = "Test description of work";
    private Boolean urgent = true;
    private ContactJson contact = ContactJsonTestUtil.newBuilder().build();

    private Builder() {
    }

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withFunction(String function) {
      this.function = function;
      return this;
    }

    Builder withManualFunction(String manualFunction) {
      this.manualFunction = manualFunction;
      return this;
    }

    Builder withDescriptionOfWork(String descriptionOfWork) {
      this.descriptionOfWork = descriptionOfWork;
      return this;
    }

    Builder withUrgent(Boolean urgent) {
      this.urgent = urgent;
      return this;
    }

    Builder withContact(ContactJson contact) {
      this.contact = contact;
      return this;
    }

    InfrastructureProjectCollaborationOpportunityJson build() {
      return new InfrastructureProjectCollaborationOpportunityJson(
          id,
          function,
          manualFunction,
          descriptionOfWork,
          urgent,
          contact
      );
    }
  }
}
