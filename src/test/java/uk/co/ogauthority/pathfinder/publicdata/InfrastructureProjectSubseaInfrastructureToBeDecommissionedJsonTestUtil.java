package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaInfrastructureType;

class InfrastructureProjectSubseaInfrastructureToBeDecommissionedJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer id = 1;
    private String surfaceInfrastructureHostName = "Test surface infrastructure host name";
    private String description = "Test description";
    private String status = InfrastructureStatus.IN_USE.name();
    private String type = SubseaInfrastructureType.CONCRETE_MATTRESSES.name();
    private InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson concreteMattressesDetails =
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJsonTestUtil.newBuilder().build();
    private InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson subseaStructureDetails;
    private InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson otherDetails;
    private StartEndYearJson decommissioningPeriod = StartEndYearJsonTestUtil.newBuilder().build();

    private Builder() {
    }

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withSurfaceInfrastructureHostName(String surfaceInfrastructureHostName) {
      this.surfaceInfrastructureHostName = surfaceInfrastructureHostName;
      return this;
    }

    Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    Builder withStatus(String status) {
      this.status = status;
      return this;
    }

    Builder withType(String type) {
      this.type = type;
      return this;
    }

    Builder withConcreteMattressesDetails(
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson concreteMattressesDetails
    ) {
      this.concreteMattressesDetails = concreteMattressesDetails;
      return this;
    }

    Builder withSubseaStructureDetails(
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson subseaStructureDetails
    ) {
      this.subseaStructureDetails = subseaStructureDetails;
      return this;
    }

    Builder withOtherDetails(InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson otherDetails) {
      this.otherDetails = otherDetails;
      return this;
    }

    Builder withDecommissioningPeriod(StartEndYearJson decommissioningPeriod) {
      this.decommissioningPeriod = decommissioningPeriod;
      return this;
    }

    InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson build() {
      return new InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson(
          id,
          surfaceInfrastructureHostName,
          description,
          status,
          type,
          concreteMattressesDetails,
          subseaStructureDetails,
          otherDetails,
          decommissioningPeriod
      );
    }
  }
}
