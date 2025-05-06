package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;

class InfrastructureProjectUpcomingTenderJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer id = 1;
    private String function = Function.DRILLING.name();
    private String manualFunction;
    private String descriptionOfWork = "Test description of work";
    private LocalDate estimatedTenderDate = LocalDate.of(2025, 1, 1);
    private String contractBand = ContractBand.GREATER_THAN_OR_EQUAL_TO_25M.name();
    private ContactJson contact = ContactJsonTestUtil.newBuilder().build();
    private UploadedFileJson supportingDocumentUploadedFile = UploadedFileJsonTestUtil.newBuilder().build();

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

    Builder withEstimatedTenderDate(LocalDate estimatedTenderDate) {
      this.estimatedTenderDate = estimatedTenderDate;
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

    Builder withSupportingDocumentUploadedFile(UploadedFileJson supportingDocumentUploadedFile) {
      this.supportingDocumentUploadedFile = supportingDocumentUploadedFile;
      return this;
    }

    InfrastructureProjectUpcomingTenderJson build() {
      return new InfrastructureProjectUpcomingTenderJson(
          id,
          function,
          manualFunction,
          descriptionOfWork,
          estimatedTenderDate,
          contractBand,
          contact,
          supportingDocumentUploadedFile
      );
    }
  }
}
