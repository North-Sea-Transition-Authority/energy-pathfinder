package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.SubstructureRemovalPremise;

class InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String premise = SubstructureRemovalPremise.FULL.name();
    private Integer estimatedMassMetricTonnes = 50;
    private StartEndYearJson period = StartEndYearJsonTestUtil.newBuilder().build();

    private Builder() {
    }

    Builder withPremise(String premise) {
      this.premise = premise;
      return this;
    }

    Builder withEstimatedMassMetricTonnes(Integer estimatedMassMetricTonnes) {
      this.estimatedMassMetricTonnes = estimatedMassMetricTonnes;
      return this;
    }

    Builder withPeriod(StartEndYearJson period) {
      this.period = period;
      return this;
    }

    InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson build() {
      return new InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson(
          premise,
          estimatedMassMetricTonnes,
          period
      );
    }
  }
}
