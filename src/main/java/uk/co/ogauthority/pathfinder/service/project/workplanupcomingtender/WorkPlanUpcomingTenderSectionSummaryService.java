package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.WorkPlanUpcomingTenderView;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
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
  private final WorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService;
  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;
  private final ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;

  @Autowired
  public WorkPlanUpcomingTenderSectionSummaryService(
      WorkPlanUpcomingTenderService workPlanUpcomingTenderService,
      DifferenceService differenceService,
      WorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService,
      ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService
  ) {
    this.workPlanUpcomingTenderService = workPlanUpcomingTenderService;
    this.differenceService = differenceService;
    this.workPlanUpcomingTenderSummaryService = workPlanUpcomingTenderSummaryService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
    this.forwardWorkPlanTenderSetupService = forwardWorkPlanTenderSetupService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return workPlanUpcomingTenderService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {

    final var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    var upcomingTenderViews = workPlanUpcomingTenderSummaryService.getSummaryViews(detail);

    summaryModel.put("upcomingTendersDiffModel", getUpcomingTenderDifferenceModel(
        detail,
        upcomingTenderViews
    ));

    summaryModel.put("workPlanTenderSetupDiffModel", getUpcomingTenderSetupDifferenceModel(detail));

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
    var previousUpcomingTenderViews = workPlanUpcomingTenderSummaryService.getSummaryViews(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    );

    return differenceService.differentiateComplexLists(
        currentUpcomingTenderViews,
        previousUpcomingTenderViews,
        Set.of("summaryLinks"),
        WorkPlanUpcomingTenderView::getDisplayOrder,
        WorkPlanUpcomingTenderView::getDisplayOrder
    );
  }

  private Map<String, Object> getUpcomingTenderSetupDifferenceModel(ProjectDetail projectDetail) {

    final var currentWorkPlanTenderSetupView = forwardWorkPlanTenderSetupService
        .getTenderSetupView(projectDetail);

    final var previousWorkPlanTenderSetupView = forwardWorkPlanTenderSetupService.getTenderSetupView(
        projectDetail.getProject(),
        projectDetail.getVersion()
    );

    return differenceService.differentiate(
        currentWorkPlanTenderSetupView,
        previousWorkPlanTenderSetupView
    );
  }
}