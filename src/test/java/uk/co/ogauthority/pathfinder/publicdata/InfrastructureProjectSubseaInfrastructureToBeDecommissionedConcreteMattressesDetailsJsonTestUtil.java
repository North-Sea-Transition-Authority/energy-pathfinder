package uk.co.ogauthority.pathfinder.publicdata;

class InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer numberToDecommission = 7;
    private Integer totalEstimatedMassMetricTonnes = 277;

    private Builder() {
    }

    Builder withNumberToDecommission(Integer numberToDecommission) {
      this.numberToDecommission = numberToDecommission;
      return this;
    }

    Builder withTotalEstimatedMassMetricTonnes(Integer totalEstimatedMassMetricTonnes) {
      this.totalEstimatedMassMetricTonnes = totalEstimatedMassMetricTonnes;
      return this;
    }

    InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson build() {
      return new InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson(
          numberToDecommission,
          totalEstimatedMassMetricTonnes
      );
    }
  }
}
