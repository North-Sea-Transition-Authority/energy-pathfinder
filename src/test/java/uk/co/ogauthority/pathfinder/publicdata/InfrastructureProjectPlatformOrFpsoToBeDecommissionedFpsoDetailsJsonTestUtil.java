package uk.co.ogauthority.pathfinder.publicdata;

class InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String name = "Test name";
    private String type = "Test type";
    private String dimensions = "Test dimensions";

    private Builder() {
    }

    Builder withName(String name) {
      this.name = name;
      return this;
    }

    Builder withType(String type) {
      this.type = type;
      return this;
    }

    Builder withDimensions(String dimensions) {
      this.dimensions = dimensions;
      return this;
    }

    InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson build() {
      return new InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson(
          name,
          type,
          dimensions
      );
    }
  }
}
