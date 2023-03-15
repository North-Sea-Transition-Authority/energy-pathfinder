package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;
import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedpipeline.PipelineRemovalPremise;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineForm;
import uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline.DecommissionedPipelineView;
import uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline.DecommissionedPipelineViewUtil;

public class DecommissionedPipelineTestUtil {
  private static final InfrastructureStatus INFRASTRUCTURE_STATUS = InfrastructureStatus.READY_TO_DECOMMISSION;
  private static final Integer EARLIEST_DECOM_START = 2020;
  private static final Integer LATEST_DECOM_COMPLETION = LocalDate.now().getYear();
  private static final PipelineRemovalPremise REMOVAL_PREMISE = PipelineRemovalPremise.FULL_REMOVAL;

  private DecommissionedPipelineTestUtil() {
    throw new IllegalStateException("DecommissionedPipelineTestUtil is a utility class and should not be instantiated");
  }

  public static DecommissionedPipeline createDecommissionedPipeline() {
    var decommissionedPipeline = new DecommissionedPipeline();
    decommissionedPipeline.setProjectDetail(ProjectUtil.getProjectDetails());
    decommissionedPipeline.setPipeline(PipelineTestUtil.getPipeline());
    decommissionedPipeline.setStatus(INFRASTRUCTURE_STATUS);
    decommissionedPipeline.setEarliestRemovalYear(String.valueOf(EARLIEST_DECOM_START));
    decommissionedPipeline.setLatestRemovalYear(String.valueOf(LATEST_DECOM_COMPLETION));
    decommissionedPipeline.setRemovalPremise(REMOVAL_PREMISE);

    return decommissionedPipeline;
  }

  public static DecommissionedPipelineForm createDecommissionedPipelineForm() {
    var form = new DecommissionedPipelineForm();
    form.setPipeline("1");
    form.setStatus(INFRASTRUCTURE_STATUS);
    form.setDecommissioningDate(
        new MinMaxDateInput(String.valueOf(EARLIEST_DECOM_START), String.valueOf(LATEST_DECOM_COMPLETION))
    );
    form.setRemovalPremise(REMOVAL_PREMISE);

    return form;
  }

  public static DecommissionedPipelineView createDecommissionedPipelineView() {
    var decommissionedPipeline = createDecommissionedPipeline();
    return DecommissionedPipelineViewUtil.from(decommissionedPipeline, 1, true);
  }

  public static DecommissionedPipelineView createDecommissionedPipelineView(Integer displayOrder, boolean isValid) {
    var decommissionedPipelineView = createDecommissionedPipelineView();
    decommissionedPipelineView.setDisplayOrder(displayOrder);
    decommissionedPipelineView.setIsValid(isValid);
    return decommissionedPipelineView;
  }
}
