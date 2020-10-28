package uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline.DecommissionedPipelineView;
import uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline.DecommissionedPipelineViewUtil;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class DecommissionedPipelineSummaryService {

  public static final String ERROR_FIELD_NAME = "pipeline-%d";
  public static final String ERROR_MESSAGE = "Pipeline %d is incomplete";
  public static final String EMPTY_LIST_ERROR = "You must add at least one pipeline";

  private final DecommissionedPipelineService decommissionedPipelineService;

  @Autowired
  public DecommissionedPipelineSummaryService(DecommissionedPipelineService decommissionedPipelineService) {
    this.decommissionedPipelineService = decommissionedPipelineService;
  }

  public List<DecommissionedPipelineView> getDecommissionedPipelineSummaryViews(ProjectDetail projectDetail) {
    return constructDecommissionedPipelineViews(projectDetail, ValidationType.NO_VALIDATION);
  }

  public DecommissionedPipelineView getDecommissionedPipelineSummaryView(Integer decommissionedPipelineId,
                                                                         ProjectDetail projectDetail,
                                                                         Integer displayOrder) {
    var decommissionedPipeline = decommissionedPipelineService.getDecommissionedPipelineOrError(
        decommissionedPipelineId,
        projectDetail
    );
    return DecommissionedPipelineViewUtil.from(decommissionedPipeline, displayOrder);
  }

  public List<DecommissionedPipelineView> getValidatedDecommissionedPipelineSummaryViews(ProjectDetail projectDetail) {
    return constructDecommissionedPipelineViews(projectDetail, ValidationType.FULL);
  }

  public List<ErrorItem> getDecommissionedPipelineViewErrors(List<DecommissionedPipelineView> decommissionedPipelineViews) {
    return SummaryUtil.getErrors(new ArrayList<>(decommissionedPipelineViews), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public ValidationResult validateViews(List<DecommissionedPipelineView> decommissionedPipelineViews) {
    return SummaryUtil.validateViews(new ArrayList<>(decommissionedPipelineViews));
  }

  private List<DecommissionedPipelineView> constructDecommissionedPipelineViews(ProjectDetail projectDetail,
                                                                                ValidationType validationType) {
    var decommissionedPipelines = decommissionedPipelineService.getDecommissionedPipelines(projectDetail);

    return IntStream.range(0, decommissionedPipelines.size())
        .mapToObj(index -> {

          DecommissionedPipelineView decommissionedPipelineView;
          var decommissionedPipeline = decommissionedPipelines.get(index);
          var displayIndex = index + 1;

          if (validationType.equals(ValidationType.NO_VALIDATION)) {
            decommissionedPipelineView = DecommissionedPipelineViewUtil.from(decommissionedPipeline, displayIndex);
          } else {
            var isValid = decommissionedPipelineService.isValid(decommissionedPipeline, validationType);
            decommissionedPipelineView = DecommissionedPipelineViewUtil.from(decommissionedPipeline, displayIndex, isValid);
          }

          return decommissionedPipelineView;

        })
        .collect(Collectors.toList());
  }
}
