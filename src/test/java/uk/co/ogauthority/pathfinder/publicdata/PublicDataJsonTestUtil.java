package uk.co.ogauthority.pathfinder.publicdata;

import java.util.Set;

class PublicDataJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Set<InfrastructureProjectJson> infrastructureProjects = Set.of(
        InfrastructureProjectJsonTestUtil.newBuilder()
            .withId(1)
            .build(),
        InfrastructureProjectJsonTestUtil.newBuilder()
            .withId(2)
            .build()
    );

    private Builder() {
    }

    Builder withInfrastructureProjects(Set<InfrastructureProjectJson> infrastructureProjects) {
      this.infrastructureProjects = infrastructureProjects;
      return this;
    }

    PublicDataJson build() {
      return new PublicDataJson(infrastructureProjects);
    }
  }
}
