package uk.co.ogauthority.pathfinder.publicdata;

import java.util.List;

class InfrastructureProjectWellScheduleJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer id = 1;
    private StartEndYearJson period = StartEndYearJsonTestUtil.newBuilder().build();
    private List<WellboreJson> wellbores = List.of(
        WellboreJsonTestUtil.newBuilder().withRegistrationNumber("48/14-G7").build(),
        WellboreJsonTestUtil.newBuilder().withRegistrationNumber("L46/17-A5 ").build()
    );

    private Builder() {
    }

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withPeriod(StartEndYearJson period) {
      this.period = period;
      return this;
    }

    Builder withWellbores(List<WellboreJson> wellbores) {
      this.wellbores = wellbores;
      return this;
    }

    InfrastructureProjectWellScheduleJson build() {
      return new InfrastructureProjectWellScheduleJson(
          id,
          period,
          wellbores
      );
    }
  }
}
