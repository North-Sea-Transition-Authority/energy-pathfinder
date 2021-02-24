package uk.co.ogauthority.pathfinder.model.view.plugabandonmentschedule;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule.PlugAbandonmentScheduleController;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentWell;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class PlugAbandonmentScheduleViewUtil {

  public static final String DEFAULT_YEAR_TEXT = StringDisplayUtil.NOT_SET_TEXT;
  public static final String EARLIEST_START_YEAR_TEXT = "Earliest start year: %s";
  public static final String LATEST_COMPLETION_YEAR_TEXT = "Latest completion year: %s";

  private PlugAbandonmentScheduleViewUtil() {
    throw new IllegalStateException("PlugAbandonmentScheduleViewUtil is a utility class and should not be instantiated.");
  }

  public static PlugAbandonmentScheduleView from(PlugAbandonmentSchedule plugAbandonmentSchedule,
                                                 List<PlugAbandonmentWell> plugAbandonmentWells,
                                                 Integer displayOrder) {
    return from(plugAbandonmentSchedule, plugAbandonmentWells, displayOrder, true);
  }

  public static PlugAbandonmentScheduleView from(PlugAbandonmentSchedule plugAbandonmentSchedule,
                                                 List<PlugAbandonmentWell> plugAbandonmentWells,
                                                 Integer displayOrder,
                                                 boolean isValid) {
    var plugAbandonmentScheduleView = new PlugAbandonmentScheduleView();
    plugAbandonmentScheduleView.setDisplayOrder(displayOrder);
    plugAbandonmentScheduleView.setId(plugAbandonmentSchedule.getId());

    var projectId = plugAbandonmentSchedule.getProjectDetail().getProject().getId();
    plugAbandonmentScheduleView.setProjectId(projectId);

    var earliestStartYear = (plugAbandonmentSchedule.getEarliestStartYear() != null)
        ? String.valueOf(plugAbandonmentSchedule.getEarliestStartYear())
        : DEFAULT_YEAR_TEXT;

    var latestCompletionYear = (plugAbandonmentSchedule.getLatestCompletionYear() != null)
        ? String.valueOf(plugAbandonmentSchedule.getLatestCompletionYear())
        : DEFAULT_YEAR_TEXT;

    plugAbandonmentScheduleView.setEarliestStartYear(String.format(EARLIEST_START_YEAR_TEXT, earliestStartYear));
    plugAbandonmentScheduleView.setLatestCompletionYear(String.format(LATEST_COMPLETION_YEAR_TEXT, latestCompletionYear));

    plugAbandonmentScheduleView.setWells(plugAbandonmentWells.stream()
        .sorted(Comparator.comparing(plugAbandonmentWell -> plugAbandonmentWell.getWellbore().getSortKey()))
        .map(plugAbandonmentWell -> plugAbandonmentWell.getWellbore().getRegistrationNo())
        .collect(Collectors.toList()));

    var summaryLinks = new ArrayList<SummaryLink>();
    summaryLinks.add(getEditSummaryLink(projectId, plugAbandonmentSchedule.getId()));
    summaryLinks.add(getDeleteSummaryLink(projectId, plugAbandonmentSchedule.getId(), displayOrder));
    plugAbandonmentScheduleView.setSummaryLinks(summaryLinks);

    plugAbandonmentScheduleView.setIsValid(isValid);

    return plugAbandonmentScheduleView;
  }

  private static SummaryLink getEditSummaryLink(Integer projectId, Integer plugAbandonmentScheduleId) {
    return new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(PlugAbandonmentScheduleController.class).getPlugAbandonmentSchedule(
            projectId,
            plugAbandonmentScheduleId,
            null
        ))
    );
  }

  private static SummaryLink getDeleteSummaryLink(Integer projectId,
                                                  Integer plugAbandonmentScheduleId,
                                                  Integer displayOrder) {
    return new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(PlugAbandonmentScheduleController.class).removePlugAbandonmentScheduleConfirmation(
            projectId,
            plugAbandonmentScheduleId,
            displayOrder,
            null
        ))
    );
  }
}
