package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;

class InfrastructureProjectAwardedContractJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer id = 1;
    private String contractorName = "Test contractor";
    private String function = Function.FABRICATION.name();
    private String manualFunction;
    private String descriptionOfWork = "Test description of work";
    private LocalDate dateAwarded = LocalDate.of(2024, 11, 26);
    private String contractBand = ContractBand.GREATER_THAN_OR_EQUAL_TO_25M.name();
    private ContactJson contact = ContactJsonTestUtil.newBuilder().build();

    private Builder() {
    }

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withContractorName(String contractorName) {
      this.contractorName = contractorName;
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

    Builder withDateAwarded(LocalDate dateAwarded) {
      this.dateAwarded = dateAwarded;
      return this;
    }

    Builder withContractBand(String contractBand) {
      this.contractBand = contractBand;
      return this;
    }

    Builder withContact(ContactJson contact) {
      this.contact = contact;
      return this;
    }

    InfrastructureProjectAwardedContractJson build() {
      return new InfrastructureProjectAwardedContractJson(
          id,
          contractorName,
          function,
          manualFunction,
          descriptionOfWork,
          dateAwarded,
          contractBand,
          contact
      );
    }
  }
}
