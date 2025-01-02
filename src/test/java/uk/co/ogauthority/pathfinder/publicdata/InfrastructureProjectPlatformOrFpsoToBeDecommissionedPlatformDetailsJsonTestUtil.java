package uk.co.ogauthority.pathfinder.publicdata;

class InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String name = "Test name";

    private Builder() {
    }

    Builder withName(String name) {
      this.name = name;
      return this;
    }

    InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson build() {
      return new InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson(name);
    }
  }
}
