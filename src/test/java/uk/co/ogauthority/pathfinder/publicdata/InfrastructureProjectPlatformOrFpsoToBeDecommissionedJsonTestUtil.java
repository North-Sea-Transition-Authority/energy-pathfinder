package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.FuturePlans;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.PlatformFpsoInfrastructureType;

class InfrastructureProjectPlatformOrFpsoToBeDecommissionedJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer id = 1;
    private String type = PlatformFpsoInfrastructureType.PLATFORM.name();
    private InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson platformDetails =
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJsonTestUtil.newBuilder().build();
    private InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson fpsoDetails;
    private Integer topsidesOrFloatingUnitMassMetricTonnes = 100;
    private StartEndYearJson removalPeriod = StartEndYearJsonTestUtil.newBuilder().build();
    private Boolean substructureRemovalInScope = true;
    private InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson substructureRemovalDetails
        = InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJsonTestUtil.newBuilder().build();
    private String futurePlans = FuturePlans.RECYCLE.name();

    private Builder() {
    }

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withType(String type) {
      this.type = type;
      return this;
    }

    Builder withPlatformDetails(InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson platformDetails) {
      this.platformDetails = platformDetails;
      return this;
    }

    Builder withFpsoDetails(InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson fpsoDetails) {
      this.fpsoDetails = fpsoDetails;
      return this;
    }

    Builder withTopsidesOrFloatingUnitMassMetricTonnes(Integer topsidesOrFloatingUnitMassMetricTonnes) {
      this.topsidesOrFloatingUnitMassMetricTonnes = topsidesOrFloatingUnitMassMetricTonnes;
      return this;
    }

    Builder withRemovalPeriod(StartEndYearJson startEndYearJson){
      this.removalPeriod = startEndYearJson;
      return this;
    }

    Builder withSubstructureRemovalInScope(Boolean substructureRemovalInScope) {
      this.substructureRemovalInScope = substructureRemovalInScope;
      return this;
    }

    Builder withSubstructureRemovalDetails(
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson substructureRemovalDetails
    ) {
      this.substructureRemovalDetails = substructureRemovalDetails;
      return this;
    }

    Builder withFuturePlans(String futurePlans) {
      this.futurePlans = futurePlans;
      return this;
    }

    InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson build() {
      return new InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson(
          id,
          type,
          platformDetails,
          fpsoDetails,
          topsidesOrFloatingUnitMassMetricTonnes,
          removalPeriod,
          substructureRemovalInScope,
          substructureRemovalDetails,
          futurePlans
      );
    }
  }
}
