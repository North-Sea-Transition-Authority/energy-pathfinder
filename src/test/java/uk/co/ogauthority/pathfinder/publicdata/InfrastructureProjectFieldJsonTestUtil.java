package uk.co.ogauthority.pathfinder.publicdata;

class InfrastructureProjectFieldJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String fieldName = "MERCURY";
    private String fieldType = "CARBON_STORAGE";
    private String ukcsArea = "CNS";

    private Builder() {
    }

    Builder withFieldName(String fieldName) {
      this.fieldName = fieldName;
      return this;
    }

    Builder withFieldType(String fieldType) {
      this.fieldType = fieldType;
      return this;
    }

    Builder withUkcsArea(String ukcsArea) {
      this.ukcsArea = ukcsArea;
      return this;
    }

    InfrastructureProjectFieldJson build() {
      return new InfrastructureProjectFieldJson(fieldName, fieldType, ukcsArea);
    }
  }
}
