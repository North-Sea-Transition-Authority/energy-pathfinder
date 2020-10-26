package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedpipeline.PipelineRemovalPremise;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineForm;

public class DecommissionedPipelineTestUtil {

  private static final String MATERIAL_TYPE = "materialType";
  private static final InfrastructureStatus INFRASTRUCTURE_STATUS = InfrastructureStatus.READY_TO_DECOMMISSION;
  private static final Integer EARLIEST_DECOM_START = 2020;
  private static final Integer LATEST_DECOM_COMPLETION = 2021;
  private static final PipelineRemovalPremise REMOVAL_PREMISE = PipelineRemovalPremise.PARTIAL_REMOVAL_AND_BURY;

  private DecommissionedPipelineTestUtil() {
    throw new IllegalStateException("DecommissionedPipelineTestUtil is a utility class and should not be instantiated");
  }

  public static DecommissionedPipelineForm createDecommissionedPipelineForm() {
    var form = new DecommissionedPipelineForm();
    form.setPipeline("1");
    form.setMaterialType(MATERIAL_TYPE);
    form.setStatus(INFRASTRUCTURE_STATUS);
    form.setDecommissioningDate(
        new MinMaxDateInput(String.valueOf(EARLIEST_DECOM_START), String.valueOf(LATEST_DECOM_COMPLETION))
    );
    form.setRemovalPremise(REMOVAL_PREMISE);

    return form;
  }
}
