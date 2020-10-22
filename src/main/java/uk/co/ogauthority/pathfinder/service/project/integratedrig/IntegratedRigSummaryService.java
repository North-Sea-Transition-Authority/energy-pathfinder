package uk.co.ogauthority.pathfinder.service.project.integratedrig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.integratedrig.IntegratedRigView;
import uk.co.ogauthority.pathfinder.model.view.integratedrig.IntegratedRigViewUtil;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class IntegratedRigSummaryService {

  public static final String ERROR_FIELD_NAME = "integrated-rig-%d";
  public static final String ERROR_MESSAGE = "Integrated rig %d is incomplete";
  public static final String EMPTY_LIST_ERROR = "You must add at least one integrated rig";

  private final IntegratedRigService integratedRigService;

  @Autowired
  public IntegratedRigSummaryService(IntegratedRigService integratedRigService) {
    this.integratedRigService = integratedRigService;
  }

  public List<IntegratedRigView> getIntegratedRigSummaryViews(ProjectDetail projectDetail) {
    return constructIntegratedRigViews(projectDetail, ValidationType.NO_VALIDATION);
  }

  public IntegratedRigView getIntegratedRigSummaryView(Integer integratedRigId,
                                                       ProjectDetail projectDetail,
                                                       Integer displayOrder) {
    var subseaInfrastructure = integratedRigService.getIntegratedRig(
        integratedRigId,
        projectDetail
    );
    return IntegratedRigViewUtil.from(subseaInfrastructure, displayOrder);
  }

  public List<IntegratedRigView> getValidatedIntegratedRigSummaryViews(ProjectDetail projectDetail) {
    return constructIntegratedRigViews(projectDetail, ValidationType.FULL);
  }

  public List<ErrorItem> getIntegratedRigViewErrors(List<IntegratedRigView> integratedRigViews) {
    return SummaryUtil.getErrors(new ArrayList<>(integratedRigViews), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public ValidationResult validateViews(List<IntegratedRigView> integratedRigViews) {
    return SummaryUtil.validateViews(new ArrayList<>(integratedRigViews));
  }

  private List<IntegratedRigView> constructIntegratedRigViews(ProjectDetail projectDetail,
                                                              ValidationType validationType) {
    var integratedRigs = integratedRigService.getIntegratedRigs(projectDetail);

    return IntStream.range(0, integratedRigs.size())
        .mapToObj(index -> {

          IntegratedRigView integratedRigView;
          var integratedRig = integratedRigs.get(index);
          var displayIndex = index + 1;

          if (validationType.equals(ValidationType.NO_VALIDATION)) {
            integratedRigView = IntegratedRigViewUtil.from(integratedRig, displayIndex);
          } else {
            var isValid = integratedRigService.isValid(integratedRig, validationType);
            integratedRigView = IntegratedRigViewUtil.from(integratedRig, displayIndex, isValid);
          }

          return integratedRigView;

        })
        .collect(Collectors.toList());
  }
}
