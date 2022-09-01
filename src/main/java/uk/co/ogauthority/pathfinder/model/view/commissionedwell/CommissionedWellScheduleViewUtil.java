package uk.co.ogauthority.pathfinder.model.view.commissionedwell;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.controller.project.commissionedwell.CommissionedWellController;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class CommissionedWellScheduleViewUtil {

  public static final String DEFAULT_YEAR_TEXT = StringDisplayUtil.NOT_SET_TEXT;
  public static final String EARLIEST_START_YEAR_TEXT = "Earliest start year";
  public static final String LATEST_COMPLETION_YEAR_TEXT = "Latest completion year";

  private CommissionedWellScheduleViewUtil() {
    throw new IllegalStateException("CommissionedWellScheduleViewUtil is a utility class and should not be instantiated.");
  }

  public static CommissionedWellScheduleView from(CommissionedWellSchedule commissionedWellSchedule,
                                                  List<CommissionedWell> commissionedWells,
                                                  Integer displayOrder) {
    return from(commissionedWellSchedule, commissionedWells, displayOrder, true);
  }

  public static CommissionedWellScheduleView from(CommissionedWellSchedule commissionedWellSchedule,
                                                  List<CommissionedWell> commissionedWells,
                                                  Integer displayOrder,
                                                  boolean isValid) {
    var commissionedWellScheduleView = new CommissionedWellScheduleView();
    commissionedWellScheduleView.setDisplayOrder(displayOrder);
    commissionedWellScheduleView.setId(commissionedWellSchedule.getId());

    var projectId = commissionedWellSchedule.getProjectDetail().getProject().getId();
    commissionedWellScheduleView.setProjectId(projectId);

    var earliestStartYear = (commissionedWellSchedule.getEarliestStartYear() != null)
        ? String.valueOf(commissionedWellSchedule.getEarliestStartYear())
        : DEFAULT_YEAR_TEXT;

    var latestCompletionYear = (commissionedWellSchedule.getLatestCompletionYear() != null)
        ? String.valueOf(commissionedWellSchedule.getLatestCompletionYear())
        : DEFAULT_YEAR_TEXT;

    commissionedWellScheduleView.setEarliestStartYear(String.format("%s: %s", EARLIEST_START_YEAR_TEXT, earliestStartYear));
    commissionedWellScheduleView.setLatestCompletionYear(String.format("%s: %s", LATEST_COMPLETION_YEAR_TEXT, latestCompletionYear));

    commissionedWellScheduleView.setWells(commissionedWells.stream()
        .sorted(Comparator.comparing(commissionedWell -> commissionedWell.getWellbore().getSortKey()))
        .map(commissionedWell -> commissionedWell.getWellbore().getRegistrationNo())
        .collect(Collectors.toList()));

    var summaryLinks = new ArrayList<SummaryLink>();
    summaryLinks.add(getEditSummaryLink(projectId, commissionedWellSchedule.getId()));
    summaryLinks.add(getDeleteSummaryLink(projectId, commissionedWellSchedule.getId(), displayOrder));
    commissionedWellScheduleView.setSummaryLinks(summaryLinks);

    commissionedWellScheduleView.setIsValid(isValid);

    return commissionedWellScheduleView;
  }

  private static SummaryLink getEditSummaryLink(Integer projectId, Integer commissionedWellScheduleId) {
    return new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(CommissionedWellController.class).getCommissionedWellSchedule(
            projectId,
            commissionedWellScheduleId,
            null
        ))
    );
  }

  private static SummaryLink getDeleteSummaryLink(Integer projectId,
                                                  Integer commissionedWellScheduleId,
                                                  Integer displayOrder) {
    return new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(CommissionedWellController.class).removeCommissionedWellScheduleConfirmation(
            projectId,
            commissionedWellScheduleId,
            displayOrder,
            null
        ))
    );
  }
}
