package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.ForwardWorkPlanUpcomingTenderView;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.ForwardWorkPlanUpcomingTenderViewUtil;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class ForwardWorkPlanUpcomingTenderSummaryService {

  private final ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  @Autowired
  public ForwardWorkPlanUpcomingTenderSummaryService(
      ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService) {
    this.workPlanUpcomingTenderService = workPlanUpcomingTenderService;
  }

  public List<ForwardWorkPlanUpcomingTenderView> getSummaryViews(ProjectDetail projectDetail) {
    return createUpcomingTenderViews(
        workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail),
        ValidationType.NO_VALIDATION
    );
  }

  public List<ForwardWorkPlanUpcomingTenderView> getSummaryViews(Project project, Integer version) {
    return createUpcomingTenderViews(
        workPlanUpcomingTenderService.getUpcomingTendersForProjectAndVersion(project, version),
        ValidationType.NO_VALIDATION
    );
  }

  public List<ForwardWorkPlanUpcomingTenderView> getValidatedSummaryViews(ProjectDetail projectDetail) {
    return createUpcomingTenderViews(
        workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail),
        ValidationType.FULL
    );
  }

  public ForwardWorkPlanUpcomingTenderView getUpcomingTenderView(ForwardWorkPlanUpcomingTender workPlanUpcomingTender,
                                                                 Integer displayOrder) {
    return ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(workPlanUpcomingTender, displayOrder);
  }

  private ForwardWorkPlanUpcomingTenderView getUpcomingTenderView(ForwardWorkPlanUpcomingTender workPlanUpcomingTender,
                                                                  Integer displayOrder,
                                                                  boolean isValid) {
    return ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(workPlanUpcomingTender, displayOrder, isValid);
  }

  public ValidationResult validateViews(List<ForwardWorkPlanUpcomingTenderView> views) {
    return SummaryUtil.validateViews(new ArrayList<>(views));
  }

  private List<ForwardWorkPlanUpcomingTenderView> createUpcomingTenderViews(List<ForwardWorkPlanUpcomingTender> workPlanUpcomingTenders,
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
