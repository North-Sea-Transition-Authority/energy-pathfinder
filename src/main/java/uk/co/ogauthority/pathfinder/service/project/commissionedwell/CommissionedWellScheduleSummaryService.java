package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.commissionedwell.CommissionedWellScheduleView;
import uk.co.ogauthority.pathfinder.model.view.commissionedwell.CommissionedWellScheduleViewUtil;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class CommissionedWellScheduleSummaryService {

  private final CommissionedWellScheduleService commissionedWellScheduleService;
  private final CommissionedWellService commissionedWellService;
  private final CommissionedWellScheduleValidationService commissionedWellScheduleValidationService;

  @Autowired
  public CommissionedWellScheduleSummaryService(CommissionedWellScheduleService commissionedWellScheduleService,
                                                CommissionedWellService commissionedWellService,
                                                CommissionedWellScheduleValidationService commissionedWellScheduleValidationService) {
    this.commissionedWellScheduleService = commissionedWellScheduleService;
    this.commissionedWellService = commissionedWellService;
    this.commissionedWellScheduleValidationService = commissionedWellScheduleValidationService;
  }

  public List<CommissionedWellScheduleView> getCommissionedWellScheduleViews(ProjectDetail projectDetail) {
    return getCommissionedWellScheduleViews(projectDetail, ValidationType.NO_VALIDATION);
  }

  private List<CommissionedWellScheduleView> getCommissionedWellScheduleViews(ProjectDetail projectDetail,
                                                                              ValidationType validationType) {

    var commissionedWellSchedules = commissionedWellScheduleService.getCommissionedWellSchedules(projectDetail);
    var commissionedWells = commissionedWellService.getCommissionedWellsForSchedules(commissionedWellSchedules);

    return getCommissionedWellScheduleViews(commissionedWellSchedules, commissionedWells, validationType);
  }

  private List<CommissionedWellScheduleView> getCommissionedWellScheduleViews(List<CommissionedWellSchedule> commissionedWellSchedules,
                                                                              List<CommissionedWell> commissionedWellsForSchedules,
                                                                              ValidationType validationType) {
    var commissionedWellScheduleViews = new ArrayList<CommissionedWellScheduleView>();

    IntStream.range(0, commissionedWellSchedules.size()).forEach(index -> {
      var commissionedWellSchedule = commissionedWellSchedules.get(index);

      var commissionedWellsForSchedule = commissionedWellsForSchedules
          .stream()
          .filter(commissionedWell -> commissionedWell.getCommissionedWellSchedule().equals(commissionedWellSchedule))
          .collect(Collectors.toUnmodifiableList());

      var commissionedWellScheduleView = constructCommissionedWellScheduleView(
          commissionedWellSchedule,
          commissionedWellsForSchedule,
          index + 1,
          validationType
      );

      commissionedWellScheduleViews.add(commissionedWellScheduleView);
    });

    return commissionedWellScheduleViews;
  }

  public List<CommissionedWellScheduleView> getValidatedCommissionedWellScheduleViews(ProjectDetail projectDetail) {
    return getCommissionedWellScheduleViews(projectDetail, ValidationType.FULL);
  }

  public CommissionedWellScheduleView getCommissionedWellScheduleView(CommissionedWellSchedule commissionedWellSchedule,
                                                                      int displayOrder) {
    return constructCommissionedWellScheduleView(
        commissionedWellSchedule,
        commissionedWellService.getCommissionedWellsForSchedule(commissionedWellSchedule),
        displayOrder,
        ValidationType.NO_VALIDATION
    );

  }

  public ValidationResult determineViewValidationResult(List<CommissionedWellScheduleView> commissionedWellScheduleViews) {
    return SummaryUtil.validateViews(new ArrayList<>(commissionedWellScheduleViews));
  }

  boolean canShowInTaskList(ProjectDetail projectDetail) {
    return commissionedWellScheduleService.canShowInTaskList(projectDetail);
  }

  List<CommissionedWellScheduleView> getCommissionedWellScheduleViewViewsByProjectAndVersion(Project project,
                                                                                             Integer version) {

    var commissionedWellSchedules = commissionedWellScheduleService.getCommissionedWellSchedulesByProjectAndVersion(
        project,
        version
    );

    var commissionedWellsForSchedule = commissionedWellService.getCommissionedWellsForSchedules(commissionedWellSchedules);

    return getCommissionedWellScheduleViews(
        commissionedWellSchedules,
        commissionedWellsForSchedule,
        ValidationType.NO_VALIDATION
    );
  }

  private CommissionedWellScheduleView constructCommissionedWellScheduleView(CommissionedWellSchedule commissionedWellSchedule,
                                                                             List<CommissionedWell> commissionedWells,
                                                                             int displayOrder,
                                                                             ValidationType validationType) {
    if (ValidationType.NO_VALIDATION.equals(validationType)) {
      return CommissionedWellScheduleViewUtil.from(
          commissionedWellSchedule,
          commissionedWells,
          displayOrder,
          true
      );
    } else {

      var isFormValid = commissionedWellScheduleValidationService.isFormValid(
          commissionedWellScheduleService.getForm(commissionedWellSchedule, commissionedWells),
          validationType
      );

      return CommissionedWellScheduleViewUtil.from(
          commissionedWellSchedule,
          commissionedWells,
          displayOrder,
          isFormValid
      );
    }
  }

}
