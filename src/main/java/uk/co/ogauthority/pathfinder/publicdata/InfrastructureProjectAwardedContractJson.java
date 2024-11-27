package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.InfrastructureAwardedContract;

record InfrastructureProjectAwardedContractJson(
    Integer id,
    String contractorName,
    String function,
    String manualFunction,
    String descriptionOfWork,
    LocalDate dateAwarded,
    String contractBand,
    ContactJson contact
) {

  static InfrastructureProjectAwardedContractJson from(InfrastructureAwardedContract infrastructureAwardedContract) {
    var id = infrastructureAwardedContract.getId();
    var contractorName = infrastructureAwardedContract.getContractorName();
    var function = infrastructureAwardedContract.getContractFunction() != null
        ? infrastructureAwardedContract.getContractFunction().name()
        : null;
    var manualFunction = infrastructureAwardedContract.getManualContractFunction();
    var descriptionOfWork = infrastructureAwardedContract.getDescriptionOfWork();
    var dateAwarded = infrastructureAwardedContract.getDateAwarded();
    var contractBand = infrastructureAwardedContract.getContractBand().name();
    var contact = ContactJson.from(infrastructureAwardedContract);

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
