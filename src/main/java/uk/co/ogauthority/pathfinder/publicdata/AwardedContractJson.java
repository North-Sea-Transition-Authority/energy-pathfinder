package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContractCommon;

record AwardedContractJson(
    Integer id,
    String contractorName,
    String function,
    String manualFunction,
    String descriptionOfWork,
    LocalDate dateAwarded,
    String contractBand,
    ContactJson contact
) {

  static AwardedContractJson from(AwardedContractCommon awardedContractCommon) {
    var id = awardedContractCommon.getId();
    var contractorName = awardedContractCommon.getContractorName();
    var function = awardedContractCommon.getContractFunction() != null
        ? awardedContractCommon.getContractFunction().name()
        : null;
    var manualFunction = awardedContractCommon.getManualContractFunction();
    var descriptionOfWork = awardedContractCommon.getDescriptionOfWork();
    var dateAwarded = awardedContractCommon.getDateAwarded();
    var contractBand = awardedContractCommon.getContractBand().name();
    var contact = ContactJson.from(awardedContractCommon);

    return new AwardedContractJson(
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
