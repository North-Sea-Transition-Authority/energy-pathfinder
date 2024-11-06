package uk.co.ogauthority.pathfinder.publicdata;

import java.util.List;

class InfrastructureProjectLocationJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private InfrastructureProjectFieldJson field = InfrastructureProjectFieldJsonTestUtil.newBuilder().build();
    private Integer maximumWaterDepthMeters = 60;
    private List<String> licenceBlocks = List.of("12/34, 12/56");

    private Builder() {
    }

    Builder withField(InfrastructureProjectFieldJson field) {
      this.field = field;
      return this;
    }

    Builder withMaximumWaterDepthMeters(Integer maximumWaterDepthMeters) {
      this.maximumWaterDepthMeters = maximumWaterDepthMeters;
      return this;
    }

    Builder withLicenceBlocks(List<String> licenceBlocks) {
      this.licenceBlocks = licenceBlocks;
      return this;
    }

    InfrastructureProjectLocationJson build() {
      return new InfrastructureProjectLocationJson(field, maximumWaterDepthMeters, licenceBlocks);
    }
  }
}
