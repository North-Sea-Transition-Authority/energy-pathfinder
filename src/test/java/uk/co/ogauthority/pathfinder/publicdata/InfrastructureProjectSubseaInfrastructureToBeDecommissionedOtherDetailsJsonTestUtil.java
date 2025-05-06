package uk.co.ogauthority.pathfinder.publicdata;

class InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String type = "Test type";
    private Integer totalEstimatedMassMetricTonnes = 47;

    private Builder() {
    }

    Builder withType(String type) {
      this.type = type;
      return this;
    }

    Builder withTotalEstimatedMassMetricTonnes(Integer totalEstimatedMassMetricTonnes) {
      this.totalEstimatedMassMetricTonnes = totalEstimatedMassMetricTonnes;
      return this;
    }

    InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson build() {
      return new InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson(
          type,
          totalEstimatedMassMetricTonnes
      );
    }
  }
}
