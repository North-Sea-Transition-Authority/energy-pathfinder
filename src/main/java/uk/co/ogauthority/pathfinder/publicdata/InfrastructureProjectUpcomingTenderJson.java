package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;

record InfrastructureProjectUpcomingTenderJson(
    Integer id,
    String function,
    String manualFunction,
    String descriptionOfWork,
    LocalDate estimatedTenderDate,
    String contractBand,
    ContactJson contact
) {

  static InfrastructureProjectUpcomingTenderJson from(UpcomingTender upcomingTender) {
    var id = upcomingTender.getId();
    var function = upcomingTender.getTenderFunction() != null ? upcomingTender.getTenderFunction().name() : null;
    var manualFunction = upcomingTender.getManualTenderFunction();
    var descriptionOfWork = upcomingTender.getDescriptionOfWork();
    var estimatedTenderDate = upcomingTender.getEstimatedTenderDate();
    var contractBand = upcomingTender.getContractBand().name();
    var contact = ContactJson.from(upcomingTender);

    return new InfrastructureProjectUpcomingTenderJson(
        id,
        function,
        manualFunction,
        descriptionOfWork,
        estimatedTenderDate,
        contractBand,
        contact
    );
  }
}
