package uk.co.ogauthority.pathfinder.publicdata;

import java.util.List;

class PublicDataJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Builder() {
    }

    private List<InfrastructureProjectJson> infrastructureProjects = List.of(
        InfrastructureProjectJsonTestUtil.newBuilder()
            .withId(1)
            .build(),
        InfrastructureProjectJsonTestUtil.newBuilder()
            .withId(2)
            .build()
    );

    Builder withInfrastructureProjects(List<InfrastructureProjectJson> infrastructureProjects) {
      this.infrastructureProjects = infrastructureProjects;
      return this;
    }

    PublicDataJson build() {
      return new PublicDataJson(infrastructureProjects);
    }
  }
}
