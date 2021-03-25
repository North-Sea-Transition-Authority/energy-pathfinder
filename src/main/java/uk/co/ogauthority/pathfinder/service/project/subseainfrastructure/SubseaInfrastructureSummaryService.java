package uk.co.ogauthority.pathfinder.service.project.subseainfrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.subseainfrastructure.SubseaInfrastructureView;
import uk.co.ogauthority.pathfinder.model.view.subseainfrastructure.SubseaInfrastructureViewUtil;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class SubseaInfrastructureSummaryService {

  public static final String ERROR_FIELD_NAME = "subsea-infrastructure-%d";
  public static final String ERROR_MESSAGE = "Subsea infrastructure %d is incomplete";
  public static final String EMPTY_LIST_ERROR = "You must add at least one subsea infrastructure";

  private final SubseaInfrastructureService subseaInfrastructureService;

  @Autowired
  public SubseaInfrastructureSummaryService(SubseaInfrastructureService subseaInfrastructureService) {
    this.subseaInfrastructureService = subseaInfrastructureService;
  }

  public List<SubseaInfrastructureView> getSubseaInfrastructureSummaryViews(ProjectDetail projectDetail) {
    return constructSubseaInfrastructureViews(projectDetail, ValidationType.NO_VALIDATION);
  }

  public SubseaInfrastructureView getSubseaInfrastructureSummaryView(Integer subseaInfrastructureId,
                                                                     ProjectDetail projectDetail,
                                                                     Integer displayOrder) {
    var subseaInfrastructure = subseaInfrastructureService.getSubseaInfrastructure(
        subseaInfrastructureId,
        projectDetail
    );
    return SubseaInfrastructureViewUtil.from(subseaInfrastructure, displayOrder);
  }

  public List<SubseaInfrastructureView> getValidatedSubseaInfrastructureSummaryViews(ProjectDetail projectDetail) {
    return constructSubseaInfrastructureViews(projectDetail, ValidationType.FULL);
  }

  public List<ErrorItem> getSubseaInfrastructureViewErrors(List<SubseaInfrastructureView> subseaInfrastructureViews) {
    return SummaryUtil.getErrors(new ArrayList<>(subseaInfrastructureViews), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public ValidationResult validateViews(List<SubseaInfrastructureView> subseaInfrastructureViews) {
    return SummaryUtil.validateViews(new ArrayList<>(subseaInfrastructureViews));
  }

  private List<SubseaInfrastructureView> constructSubseaInfrastructureViews(ProjectDetail projectDetail,
                                                                            ValidationType validationType) {
    var subseaInfrastructures = subseaInfrastructureService.getSubseaInfrastructures(projectDetail);

    return IntStream.range(0, subseaInfrastructures.size())
        .mapToObj(index -> {

          SubseaInfrastructureView subseaInfrastructureView;
          var subseaInfrastructure = subseaInfrastructures.get(index);
          var displayIndex = index + 1;

          if (validationType.equals(ValidationType.NO_VALIDATION)) {
            subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, displayIndex);
          } else {
            var isValid = subseaInfrastructureService.isValid(subseaInfrastructure, validationType);
            subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, displayIndex, isValid);
          }

          return subseaInfrastructureView;

        })
        .collect(Collectors.toList());
  }
}
