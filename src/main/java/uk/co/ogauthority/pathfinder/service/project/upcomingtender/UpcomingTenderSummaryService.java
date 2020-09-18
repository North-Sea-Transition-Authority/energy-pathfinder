package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderViewUtil;

@Service
public class UpcomingTenderSummaryService {
  public static final String ERROR_FIELD_NAME = "upcoming-tender-%d";
  public static final String ERROR_MESSAGE = "Upcoming tender %d is incomplete";

  private final UpcomingTenderService upcomingTenderService;

  @Autowired
  public UpcomingTenderSummaryService(UpcomingTenderService upcomingTenderService) {
    this.upcomingTenderService = upcomingTenderService;
  }

  public List<UpcomingTenderView> getSummaryViews(ProjectDetail detail) {
    return createUpcomingTenderViews(
        upcomingTenderService.getUpcomingTendersForDetail(detail),
        ValidationType.NO_VALIDATION
    );
  }

  public List<UpcomingTenderView> getValidatedSummaryViews(ProjectDetail detail) {
    return createUpcomingTenderViews(
        upcomingTenderService.getUpcomingTendersForDetail(detail),
        ValidationType.FULL
    );
  }

  public UpcomingTenderView getUpcomingTenderView(UpcomingTender upcomingTender, Integer displayOrder) {
    return UpcomingTenderViewUtil.createUpComingTenderView(upcomingTender, displayOrder);
  }

  public List<ErrorItem> getErrors(List<UpcomingTenderView> views) {
    return views.stream().filter(v -> !v.isValid()).map(v ->
        new ErrorItem(
          v.getDisplayOrder(),
          String.format(ERROR_FIELD_NAME, v.getDisplayOrder()),
          String.format(ERROR_MESSAGE, v.getDisplayOrder())
        )
    ).collect(Collectors.toList());
  }

  /**
   * Create views for each tender in the list, validate depending on validation type.
   * @param upcomingTenders List of Tenders
   * @param validationType ValidationType (generally expected FULL or NO_VALIDATION)
   * @return A list of views validated if necessary.
   */
  public List<UpcomingTenderView> createUpcomingTenderViews(List<UpcomingTender> upcomingTenders, ValidationType validationType) {
    List<UpcomingTenderView> views = new ArrayList<>();
    for (int i = 0; i < upcomingTenders.size(); i++) {
      var displayOrder = i + 1;

      views.add(validationType.equals(ValidationType.NO_VALIDATION)
          ? UpcomingTenderViewUtil.createUpComingTenderView(upcomingTenders.get(i), displayOrder)
          : UpcomingTenderViewUtil.createUpComingTenderView(
              upcomingTenders.get(i),
              displayOrder,
              upcomingTenderService.isValid(upcomingTenders.get(i), validationType))
      );
    }
    return views;
  }

}
