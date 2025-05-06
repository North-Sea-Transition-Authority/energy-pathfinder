package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;

class ForwardWorkPlanUpcomingTenderJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer id = 1;
    private String department = Function.DRILLING.name();
    private String manualDepartment;
    private String descriptionOfWork = "Test description of work";
    private QuarterYearJson estimatedTenderDate = QuarterYearJsonTestUtil.newBuilder().build();
    private String contractBand = ContractBand.GREATER_THAN_OR_EQUAL_TO_25M.name();
    private String durationUnit = DurationPeriod.DAYS.name();
    private Integer duration = 2;
    private ContactJson contact = ContactJsonTestUtil.newBuilder().build();

    private Builder() {
    }

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withDepartment(String department) {
      this.department = department;
      return this;
    }

    Builder withManualDepartment(String manualDepartment) {
      this.manualDepartment = manualDepartment;
      return this;
    }

    Builder withDescriptionOfWork(String descriptionOfWork) {
      this.descriptionOfWork = descriptionOfWork;
      return this;
    }

    Builder withEstimatedTenderDate(QuarterYearJson estimatedTenderDate) {
      this.estimatedTenderDate = estimatedTenderDate;
      return this;
    }

    Builder withContractBand(String contractBand) {
      this.contractBand = contractBand;
      return this;
    }

    Builder withDurationUnit(String durationUnit) {
      this.durationUnit = durationUnit;
      return this;
    }

    Builder withDuration(Integer duration) {
      this.duration = duration;
      return this;
    }

    Builder withContact(ContactJson contact) {
      this.contact = contact;
      return this;
    }

    ForwardWorkPlanUpcomingTenderJson build() {
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
}
