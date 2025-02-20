package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDate;

class InfrastructureProjectDecommissioningScheduleJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String startDateType = "EXACT";
    private LocalDate exactStartDate = LocalDate.of(2025, 2, 18);
    private QuarterYearJson estimatedStartDate = null;
    private String cessationOfProductionDateType = "ESTIMATED";
    private LocalDate exactCessationOfProductionDate = null;
    private QuarterYearJson estimatedCessationOfProductionDate = QuarterYearJsonTestUtil.newBuilder().build();

    Builder withStartDateType(String startDateType) {
      this.startDateType = startDateType;
      return this;
    }

    Builder withExactStartDate(LocalDate exactStartDate) {
      this.exactStartDate = exactStartDate;
      return this;
    }

    Builder withEstimatedStartDate(QuarterYearJson estimatedStartDate) {
      this.estimatedStartDate = estimatedStartDate;
      return this;
    }

    Builder withCessationOfProductionDateType(String cessationOfProductionDateType) {
      this.cessationOfProductionDateType = cessationOfProductionDateType;
      return this;
    }

    Builder withExactCessationOfProductionDate(LocalDate exactCessationOfProductionDate) {
      this.exactCessationOfProductionDate = exactCessationOfProductionDate;
      return this;
    }

    Builder withEstimatedCessationOfProductionDate(QuarterYearJson estimatedCessationOfProductionDate) {
      this.estimatedCessationOfProductionDate = estimatedCessationOfProductionDate;
      return this;
    }

    InfrastructureProjectDecommissioningScheduleJson build() {
      return new InfrastructureProjectDecommissioningScheduleJson(
          startDateType,
          exactStartDate,
          estimatedStartDate,
          cessationOfProductionDateType,
          exactCessationOfProductionDate,
          estimatedCessationOfProductionDate
      );
    }
  }
}
