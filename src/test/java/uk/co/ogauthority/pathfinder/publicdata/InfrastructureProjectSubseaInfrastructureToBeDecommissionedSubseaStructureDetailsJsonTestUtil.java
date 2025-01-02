package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaStructureMass;

class InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String totalEstimatedMass = SubseaStructureMass.GREATER_THAN_OR_EQUAL_400_TONNES.name();

    private Builder() {
    }

    Builder withTotalEstimatedMass(String totalEstimatedMass) {
      this.totalEstimatedMass = totalEstimatedMass;
      return this;
    }

    InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson build() {
      return new InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson(
          totalEstimatedMass
      );
    }
  }
}
