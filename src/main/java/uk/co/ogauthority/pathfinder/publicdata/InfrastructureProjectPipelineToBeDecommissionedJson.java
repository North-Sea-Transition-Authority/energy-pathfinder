package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;

record InfrastructureProjectPipelineToBeDecommissionedJson(
    Integer id,
    String name,
    String status,
    StartEndYearJson decommissioningPeriod,
    String decommissioningPremise
) {

  static InfrastructureProjectPipelineToBeDecommissionedJson from(DecommissionedPipeline decommissionedPipeline) {
    var id = decommissionedPipeline.getId();
    var name = decommissionedPipeline.getPipeline().getName();
    var status = decommissionedPipeline.getStatus().name();
    var decommissioningPeriod = StartEndYearJson.from(
        decommissionedPipeline.getEarliestRemovalYear(),
        decommissionedPipeline.getLatestRemovalYear()
    );
    var decommissioningPremise = decommissionedPipeline.getRemovalPremise().name();

    return new InfrastructureProjectPipelineToBeDecommissionedJson(
        id,
        name,
        status,
        decommissioningPeriod,
        decommissioningPremise
    );
  }
}
