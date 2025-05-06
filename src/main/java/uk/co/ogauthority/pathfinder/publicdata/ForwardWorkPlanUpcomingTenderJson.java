package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;

record ForwardWorkPlanUpcomingTenderJson(
    Integer id,
    String department,
    String manualDepartment,
    String descriptionOfWork,
    QuarterYearJson estimatedTenderDate,
    String contractBand,
    String durationUnit,
    Integer duration,
    ContactJson contact
) {

  static ForwardWorkPlanUpcomingTenderJson from(ForwardWorkPlanUpcomingTender forwardWorkPlanUpcomingTender) {
    var id = forwardWorkPlanUpcomingTender.getId();
    var department = forwardWorkPlanUpcomingTender.getDepartmentType() != null
        ? forwardWorkPlanUpcomingTender.getDepartmentType().name()
        : null;
    var manualDepartment = forwardWorkPlanUpcomingTender.getManualDepartmentType();
    var descriptionOfWork = forwardWorkPlanUpcomingTender.getDescriptionOfWork();
    var estimatedTenderDate = QuarterYearJson.from(
        forwardWorkPlanUpcomingTender.getEstimatedTenderDateQuarter(),
        forwardWorkPlanUpcomingTender.getEstimatedTenderDateYear()
    );
    var contractBand = forwardWorkPlanUpcomingTender.getContractBand().name();
    var durationUnit = forwardWorkPlanUpcomingTender.getContractTermDurationPeriod().name();
    var duration = forwardWorkPlanUpcomingTender.getContractTermDuration();
    var contact = ContactJson.from(forwardWorkPlanUpcomingTender);

    return new ForwardWorkPlanUpcomingTenderJson(
        id,
        department,
        manualDepartment,
        descriptionOfWork,
        estimatedTenderDate,
        contractBand,
        durationUnit,
        duration,
        contact
    );
  }
}
