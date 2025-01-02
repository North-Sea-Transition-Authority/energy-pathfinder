package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedpipeline.PipelineRemovalPremise;

class InfrastructureProjectPipelineToBeDecommissionedJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer id = 1;
    private String name = "Test name";
    private String status = InfrastructureStatus.IN_USE.name();
    private StartEndYearJson decommissioningPeriod = StartEndYearJsonTestUtil.newBuilder().build();
    private String decommissioningPremise = PipelineRemovalPremise.MAJOR_INTERVENTION.name();

    private Builder() {
    }

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withName(String name) {
      this.name = name;
      return this;
    }

    Builder withStatus(String status) {
      this.status = status;
      return this;
    }

    Builder withDecommissioningPeriod(StartEndYearJson decommissioningPeriod) {
      this.decommissioningPeriod = decommissioningPeriod;
      return this;
    }

    Builder withDecommissioningPremise(String decommissioningPremise) {
      this.decommissioningPremise = decommissioningPremise;
      return this;
    }

    InfrastructureProjectPipelineToBeDecommissionedJson build() {
      return new InfrastructureProjectPipelineToBeDecommissionedJson(
          id,
          name,
          status,
          decommissioningPeriod,
          decommissioningPremise
      );
    }
  }
}
