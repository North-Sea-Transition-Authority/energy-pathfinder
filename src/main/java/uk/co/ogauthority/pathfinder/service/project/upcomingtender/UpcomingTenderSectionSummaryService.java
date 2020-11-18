package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class UpcomingTenderSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/upcomingtender/upcomingTenderSectionSummary.ftl";
  public static final String PAGE_NAME = UpcomingTendersController.PAGE_NAME;
  public static final String SECTION_ID = "upcoming-tenders";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.UPCOMING_TENDERS.getDisplayOrder();

  private final UpcomingTenderSummaryService upcomingTenderSummaryService;

  @Autowired
  public UpcomingTenderSectionSummaryService(UpcomingTenderSummaryService upcomingTenderSummaryService) {
    this.upcomingTenderSummaryService = upcomingTenderSummaryService;
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);

    var upcomingTenderViews = upcomingTenderSummaryService.getSummaryViews(detail);
    summaryModel.put("upcomingTenderViews", upcomingTenderViews);

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}
