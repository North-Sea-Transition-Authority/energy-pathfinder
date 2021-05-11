package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
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

  public List<WorkPlanUpcomingTenderView> getSummaryViews(Project project, Integer version) {
    return createUpcomingTenderViews(
        workPlanUpcomingTenderService.getUpcomingTendersForProjectAndVersion(project, version),
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

  private List<WorkPlanUpcomingTenderView> createUpcomingTenderViews(List<WorkPlanUpcomingTender> workPlanUpcomingTenders,
                                                                    ValidationType validationType) {
    return IntStream.range(0, workPlanUpcomingTenders.size())
      .mapToObj(index -> {
        var displayOrder = index + 1;
        var workPlanUpcomingTender = workPlanUpcomingTenders.get(index);
        return validationType.equals(ValidationType.NO_VALIDATION)
            ? getUpcomingTenderView(workPlanUpcomingTender, displayOrder)
            : getUpcomingTenderView(
                workPlanUpcomingTender,
                displayOrder,
                workPlanUpcomingTenderService.isValid(workPlanUpcomingTender, validationType)
            );
      })
        .collect(Collectors.toList());
  }
}
