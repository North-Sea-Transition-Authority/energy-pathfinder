package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.WorkPlanUpcomingTenderView;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.WorkPlanUpcomingTenderViewUtil;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class WorkPlanUpcomingTenderSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/workplanupcomingtender/workPlanUpcomingTenderSectionSummary.ftl";
  public static final String PAGE_NAME = WorkPlanUpcomingTenderController.PAGE_NAME;
  public static final String SECTION_ID = "upcomingTender";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.WORK_PLAN_UPCOMING_TENDERS.getDisplayOrder();

  private final WorkPlanUpcomingTenderService workPlanUpcomingTenderService;
  private final DifferenceService differenceService;

  @Autowired
  public WorkPlanUpcomingTenderSectionSummaryService(
      WorkPlanUpcomingTenderService workPlanUpcomingTenderService,
      DifferenceService differenceService) {
    this.workPlanUpcomingTenderService = workPlanUpcomingTenderService;
    this.differenceService = differenceService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return workPlanUpcomingTenderService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);
    var upcomingTenders = workPlanUpcomingTenderService.getUpcomingTendersForDetail(detail);
    var upcomingTenderViews = getUpcomingTenderViews(upcomingTenders);
    summaryModel.put("upcomingTendersDiffModel", getUpcomingTenderDifferenceModel(
        detail,
        upcomingTenderViews
    ));
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private List<Map<String, ?>> getUpcomingTenderDifferenceModel(
      ProjectDetail projectDetail,
      List<WorkPlanUpcomingTenderView> currentUpcomingTenderViews
  ) {
    var previousUpcomingTenders = workPlanUpcomingTenderService.getUpcomingTendersForProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    );
    var previousUpcomingTenderViews = getUpcomingTenderViews(previousUpcomingTenders);

    return differenceService.differentiateComplexLists(
        currentUpcomingTenderViews,
        previousUpcomingTenderViews,
        Set.of("summaryLinks"),
        WorkPlanUpcomingTenderView::getDisplayOrder,
        WorkPlanUpcomingTenderView::getDisplayOrder
    );
  }

  private List<WorkPlanUpcomingTenderView> getUpcomingTenderViews(List<WorkPlanUpcomingTender> upcomingTenders) {
    return IntStream.range(0, upcomingTenders.size())
        .mapToObj(index -> {
          var upcomingTender = upcomingTenders.get(index);
          var displayIndex = index + 1;

          return WorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(upcomingTender, displayIndex);
        })
        .collect(Collectors.toList());
  }
}