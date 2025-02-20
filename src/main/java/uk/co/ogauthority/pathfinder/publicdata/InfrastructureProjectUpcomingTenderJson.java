package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTenderFileLink;

record InfrastructureProjectUpcomingTenderJson(
    Integer id,
    String function,
    String manualFunction,
    String descriptionOfWork,
    LocalDate estimatedTenderDate,
    String contractBand,
    ContactJson contact,
    UploadedFileJson supportingDocumentUploadedFile
) {

  static InfrastructureProjectUpcomingTenderJson from(
      UpcomingTender upcomingTender,
      UpcomingTenderFileLink upcomingTenderFileLink
  ) {
    var id = upcomingTender.getId();
    var function = upcomingTender.getTenderFunction() != null ? upcomingTender.getTenderFunction().name() : null;
    var manualFunction = upcomingTender.getManualTenderFunction();
    var descriptionOfWork = upcomingTender.getDescriptionOfWork();
    var estimatedTenderDate = upcomingTender.getEstimatedTenderDate();
    var contractBand = upcomingTender.getContractBand().name();
    var contact = ContactJson.from(upcomingTender);
    var supportingDocumentUploadedFile = upcomingTenderFileLink != null
        ? UploadedFileJson.from(upcomingTenderFileLink.getProjectDetailFile().getUploadedFile())
        : null;

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
