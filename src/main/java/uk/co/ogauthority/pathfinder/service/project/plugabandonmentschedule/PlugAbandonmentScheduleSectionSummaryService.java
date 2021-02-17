package uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule.PlugAbandonmentScheduleController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.plugabandonmentschedule.PlugAbandonmentScheduleView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class PlugAbandonmentScheduleSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/plugabandonmentschedule/plugAbandonmentScheduleSectionSummary.ftl";
  public static final String PAGE_NAME = PlugAbandonmentScheduleController.TASK_LIST_NAME;
  public static final String SECTION_ID = "plug-abandonment-schedules";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.WELLS.getDisplayOrder();

  private final PlugAbandonmentScheduleSummaryService plugAbandonmentScheduleSummaryService;
  private final DifferenceService differenceService;

  @Autowired
  public PlugAbandonmentScheduleSectionSummaryService(PlugAbandonmentScheduleSummaryService plugAbandonmentScheduleSummaryService,
                                                      DifferenceService differenceService) {
    this.plugAbandonmentScheduleSummaryService = plugAbandonmentScheduleSummaryService;
    this.differenceService = differenceService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return plugAbandonmentScheduleSummaryService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);

    var plugAbandonmentScheduleViews = plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryViews(detail);
    summaryModel.put("plugAbandonmentScheduleDiffModel", getPlugAbandonmentScheduleDifferenceModel(
        detail,
        plugAbandonmentScheduleViews
    ));

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private List<Map<String, ?>> getPlugAbandonmentScheduleDifferenceModel(
      ProjectDetail projectDetail,
      List<PlugAbandonmentScheduleView> currentPlugAbandonmentScheduleViews
  ) {
    var previousViews = plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryViewsByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    );

    return differenceService.differentiateComplexLists(
        currentPlugAbandonmentScheduleViews,
        previousViews,
        Set.of("summaryLinks"),
        PlugAbandonmentScheduleView::getDisplayOrder,
        PlugAbandonmentScheduleView::getDisplayOrder
    );
  }
}
