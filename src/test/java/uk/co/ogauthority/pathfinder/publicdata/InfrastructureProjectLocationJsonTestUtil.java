package uk.co.ogauthority.pathfinder.publicdata;

import java.util.List;

class InfrastructureProjectLocationJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private CoordinateJson centreOfInterestLatitude = CoordinateJsonTestUtil.newBuilder()
        .withDegrees(51)
        .withMinutes(30)
        .withSeconds(32.4)
        .withHemisphere("NORTH")
        .build();
    private CoordinateJson centreOfInterestLongitude = CoordinateJsonTestUtil.newBuilder()
        .withDegrees(0)
        .withMinutes(7)
        .withSeconds(19.2)
        .withHemisphere("WEST")
        .build();
    private InfrastructureProjectFieldJson field = InfrastructureProjectFieldJsonTestUtil.newBuilder().build();
    private Integer maximumWaterDepthMeters = 60;
    private List<String> licenceBlocks = List.of("12/34, 12/56");

    private Builder() {
    }

    Builder withCentreOfInterestLatitude(CoordinateJson centreOfInterestLatitude) {
      this.centreOfInterestLatitude = centreOfInterestLatitude;
      return this;
    }

    Builder withCentreOfInterestLongitude(CoordinateJson centreOfInterestLongitude) {
      this.centreOfInterestLongitude = centreOfInterestLongitude;
      return this;
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
      return new InfrastructureProjectLocationJson(
          centreOfInterestLatitude,
          centreOfInterestLongitude,
          field,
          maximumWaterDepthMeters,
          licenceBlocks
      );
    }
  }
}
