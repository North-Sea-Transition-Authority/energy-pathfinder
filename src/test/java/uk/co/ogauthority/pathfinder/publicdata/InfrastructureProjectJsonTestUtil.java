package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDateTime;
import java.util.Set;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.PlatformFpsoInfrastructureType;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaInfrastructureType;

class InfrastructureProjectJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer id = 1;
    private InfrastructureProjectDetailsJson details = InfrastructureProjectDetailsJsonTestUtil.newBuilder().build();
    private ContactJson contact = ContactJsonTestUtil.newBuilder().build();
    private InfrastructureProjectFirstProductionDateJson firstProductionDate =
        InfrastructureProjectFirstProductionDateJsonTestUtil.newBuilder().build();
    private InfrastructureProjectLocationJson location = InfrastructureProjectLocationJsonTestUtil.newBuilder().build();
    private Set<InfrastructureProjectUpcomingTenderJson> upcomingTenders = Set.of(
        InfrastructureProjectUpcomingTenderJsonTestUtil.newBuilder().withId(1).build(),
        InfrastructureProjectUpcomingTenderJsonTestUtil.newBuilder().withId(2).build()
    );
    private Set<InfrastructureProjectAwardedContractJson> awardedContracts = Set.of(
        InfrastructureProjectAwardedContractJsonTestUtil.newBuilder().withId(1).build(),
        InfrastructureProjectAwardedContractJsonTestUtil.newBuilder().withId(2).build()
    );
    private Set<InfrastructureProjectCollaborationOpportunityJson> collaborationOpportunities = Set.of(
        InfrastructureProjectCollaborationOpportunityJsonTestUtil.newBuilder().withId(1).build(),
        InfrastructureProjectCollaborationOpportunityJsonTestUtil.newBuilder().withId(2).build()
    );
    private InfrastructureProjectCampaignJson campaign =
        InfrastructureProjectCampaignJsonTestUtil.newBuilder().build();
    private Set<InfrastructureProjectWellScheduleJson> wellCommissioningSchedules = Set.of(
        InfrastructureProjectWellScheduleJsonTestUtil.newBuilder().withId(1).build(),
        InfrastructureProjectWellScheduleJsonTestUtil.newBuilder().withId(2).build()
    );
    private Set<InfrastructureProjectWellScheduleJson> wellDecommissioningSchedules = Set.of(
        InfrastructureProjectWellScheduleJsonTestUtil.newBuilder().withId(1).build(),
        InfrastructureProjectWellScheduleJsonTestUtil.newBuilder().withId(2).build()
    );
    private Set<InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson> platformOrFpsosToBeDecommissioned = Set.of(
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedJsonTestUtil.newBuilder()
            .withId(1)
            .withType(PlatformFpsoInfrastructureType.PLATFORM.name())
            .withPlatformDetails(InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJsonTestUtil.newBuilder()
                .build())
            .withFpsoDetails(null)
            .build(),
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedJsonTestUtil.newBuilder()
            .withId(2)
            .withType(PlatformFpsoInfrastructureType.FPSO.name())
            .withPlatformDetails(null)
            .withFpsoDetails(InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJsonTestUtil.newBuilder()
                .build())
            .build()
    );
    private Set<InfrastructureProjectIntegratedRigToBeDecommissionedJson> integratedRigsToBeDecommissioned = Set.of(
        InfrastructureProjectIntegratedRigToBeDecommissionedJsonTestUtil.newBuilder().withId(1).build(),
        InfrastructureProjectIntegratedRigToBeDecommissionedJsonTestUtil.newBuilder().withId(2).build()
    );
    private Set<InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson> subseaInfrastructuresToBeDecommissioned = Set.of(
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedJsonTestUtil.newBuilder()
            .withId(1)
            .withType(SubseaInfrastructureType.CONCRETE_MATTRESSES.name())
            .withConcreteMattressesDetails(
                InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJsonTestUtil.newBuilder()
                    .build()
            )
            .withSubseaStructureDetails(null)
            .withOtherDetails(null)
            .build(),
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedJsonTestUtil.newBuilder()
            .withId(2)
            .withType(SubseaInfrastructureType.SUBSEA_STRUCTURE.name())
            .withConcreteMattressesDetails(null)
            .withSubseaStructureDetails(
                InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJsonTestUtil.newBuilder().build()
            )
            .withOtherDetails(null)
            .build(),
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedJsonTestUtil.newBuilder()
            .withId(3)
            .withType(SubseaInfrastructureType.OTHER.name())
            .withConcreteMattressesDetails(null)
            .withSubseaStructureDetails(null)
            .withOtherDetails(
                InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJsonTestUtil.newBuilder().build()
            )
            .build()
    );
    private Set<InfrastructureProjectPipelineToBeDecommissionedJson> pipelinesToBeDecommissioned = Set.of(
        InfrastructureProjectPipelineToBeDecommissionedJsonTestUtil.newBuilder().withId(1).build(),
        InfrastructureProjectPipelineToBeDecommissionedJsonTestUtil.newBuilder().withId(2).build()
    );
    private LocalDateTime submittedOn = LocalDateTime.of(2024, 10, 29, 11, 20, 38, 424521789);

    private Builder() {
    }

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withDetails(InfrastructureProjectDetailsJson details) {
      this.details = details;
      return this;
    }

    Builder withContact(ContactJson contact) {
      this.contact = contact;
      return this;
    }

    Builder withFirstProductionDate(InfrastructureProjectFirstProductionDateJson firstProductionDate) {
      this.firstProductionDate = firstProductionDate;
      return this;
    }

    Builder withLocation(InfrastructureProjectLocationJson location) {
      this.location = location;
      return this;
    }

    Builder withUpcomingTenders(Set<InfrastructureProjectUpcomingTenderJson> upcomingTenders) {
      this.upcomingTenders = upcomingTenders;
      return this;
    }

    Builder withAwardedContracts(Set<InfrastructureProjectAwardedContractJson> awardedContracts) {
      this.awardedContracts = awardedContracts;
      return this;
    }

    Builder withCollaborationOpportunities(Set<InfrastructureProjectCollaborationOpportunityJson> collaborationOpportunities) {
      this.collaborationOpportunities = collaborationOpportunities;
      return this;
    }

    Builder withCampaign(InfrastructureProjectCampaignJson campaign) {
      this.campaign = campaign;
      return this;
    }

    Builder withWellCommissioningSchedules(Set<InfrastructureProjectWellScheduleJson> wellCommissioningSchedules) {
      this.wellCommissioningSchedules = wellCommissioningSchedules;
      return this;
    }

    Builder withWellDecommissioningSchedules(Set<InfrastructureProjectWellScheduleJson> wellDecommissioningSchedules) {
      this.wellDecommissioningSchedules = wellDecommissioningSchedules;
      return this;
    }

    Builder withPlatformOrFpsosToBeDecommissioned(
        Set<InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson> platformOrFpsosToBeDecommissioned
    ) {
      this.platformOrFpsosToBeDecommissioned = platformOrFpsosToBeDecommissioned;
      return this;
    }

    Builder withIntegratedRigsToBeDecommissioned(
        Set<InfrastructureProjectIntegratedRigToBeDecommissionedJson> integratedRigsToBeDecommissioned
    ) {
      this.integratedRigsToBeDecommissioned = integratedRigsToBeDecommissioned;
      return this;
    }

    Builder withSubseaInfrastructuresToBeDecommissioned(
        Set<InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson> subseaInfrastructuresToBeDecommissioned
    ) {
      this.subseaInfrastructuresToBeDecommissioned = subseaInfrastructuresToBeDecommissioned;
      return this;
    }

    Builder withPipelinesToBeDecommissioned(
        Set<InfrastructureProjectPipelineToBeDecommissionedJson> pipelinesToBeDecommissioned
    ) {
      this.pipelinesToBeDecommissioned = pipelinesToBeDecommissioned;
      return this;
    }

    Builder withSubmittedOn(LocalDateTime submittedOn) {
      this.submittedOn = submittedOn;
      return this;
    }

    InfrastructureProjectJson build() {
      return new InfrastructureProjectJson(
          id,
          details,
          contact,
          firstProductionDate,
          location,
          upcomingTenders,
          awardedContracts,
          collaborationOpportunities,
          campaign,
          wellCommissioningSchedules,
          wellDecommissioningSchedules,
          platformOrFpsosToBeDecommissioned,
          integratedRigsToBeDecommissioned,
          subseaInfrastructuresToBeDecommissioned,
          pipelinesToBeDecommissioned,
          submittedOn
      );
    }
  }
}
