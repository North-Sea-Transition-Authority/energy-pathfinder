package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigIntentionToReactivate;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigStatus;

class InfrastructureProjectIntegratedRigToBeDecommissionedJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer id = 1;
    private String structureName = "Test structure name";
    private String name = "Test name";
    private String status = IntegratedRigStatus.WARM.name();
    private String intentionToReactivate = IntegratedRigIntentionToReactivate.YES.name();

    private Builder() {
    }

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withStructureName(String structureName) {
      this.structureName = structureName;
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

    Builder withIntentionToReactivate(String intentionToReactivate) {
      this.status = intentionToReactivate;
      return this;
    }

    InfrastructureProjectIntegratedRigToBeDecommissionedJson build() {
      return new InfrastructureProjectIntegratedRigToBeDecommissionedJson(
          id,
          structureName,
          name,
          status,
          intentionToReactivate
      );
    }
  }
}
