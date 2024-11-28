package uk.co.ogauthority.pathfinder.publicdata;

class InfrastructureProjectDetailsJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String operatorName = "BP";
    private String title = "Test project";
    private String summary = "Test summary";
    private String projectType = "DECOMMISSIONING";
    private String projectTypeSubCategory;

    private Builder() {
    }

    Builder withOperatorName(String operatorName) {
      this.operatorName = operatorName;
      return this;
    }

    Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    Builder withSummary(String summary) {
      this.summary = summary;
      return this;
    }

    Builder withProjectType(String projectType) {
      this.projectType = projectType;
      return this;
    }

    Builder withProjectTypeSubCategory(String projectTypeSubCategory) {
      this.projectTypeSubCategory = projectTypeSubCategory;
      return this;
    }

    InfrastructureProjectDetailsJson build() {
      return new InfrastructureProjectDetailsJson(operatorName, title, summary, projectType, projectTypeSubCategory);
    }
  }
}
