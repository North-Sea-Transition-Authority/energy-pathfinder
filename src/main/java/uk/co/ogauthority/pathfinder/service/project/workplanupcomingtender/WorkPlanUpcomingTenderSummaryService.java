package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.WorkPlanUpcomingTenderView;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.WorkPlanUpcomingTenderViewUtil;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class WorkPlanUpcomingTenderSummaryService {

  private final WorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  @Autowired
  public WorkPlanUpcomingTenderSummaryService(
      WorkPlanUpcomingTenderService workPlanUpcomingTenderService) {
    this.workPlanUpcomingTenderService = workPlanUpcomingTenderService;
  }

  public List<WorkPlanUpcomingTenderView> getSummaryViews(ProjectDetail projectDetail) {
    return createUpcomingTenderViews(
        workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail),
        ValidationType.NO_VALIDATION
    );
  }

  public List<WorkPlanUpcomingTenderView> getValidatedSummaryViews(ProjectDetail projectDetail) {
    return createUpcomingTenderViews(
        workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail),
        ValidationType.FULL
    );
  }

  public WorkPlanUpcomingTenderView getUpcomingTenderView(WorkPlanUpcomingTender workPlanUpcomingTender, Integer displayOrder) {
    return WorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(workPlanUpcomingTender, displayOrder);
  }

  private WorkPlanUpcomingTenderView getUpcomingTenderView(WorkPlanUpcomingTender workPlanUpcomingTender,
                                                           Integer displayOrder,
                                                           boolean isValid) {
    return WorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(workPlanUpcomingTender, displayOrder, isValid);
  }

  public ValidationResult validateViews(List<WorkPlanUpcomingTenderView> views) {
    return SummaryUtil.validateViews(new ArrayList<>(views));
  }

  public List<WorkPlanUpcomingTenderView> createUpcomingTenderViews(List<WorkPlanUpcomingTender> workPlanUpcomingTenders,
                                                                    ValidationType validationType) {
    List<WorkPlanUpcomingTenderView> views = new ArrayList<>();
    for (int i = 0; i < workPlanUpcomingTenders.size(); i++) {
      var displayOrder = i + 1;

      var workPlanUpcomingTender = workPlanUpcomingTenders.get(i);

      views.add(validationType.equals(ValidationType.NO_VALIDATION)
          ? getUpcomingTenderView(workPlanUpcomingTender, displayOrder)
          : getUpcomingTenderView(
              workPlanUpcomingTender,
              displayOrder,
              workPlanUpcomingTenderService.isValid(workPlanUpcomingTender, validationType)
          )
      );
    }
    return views;
  }
}
