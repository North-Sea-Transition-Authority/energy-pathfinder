package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.commissionedwell.CommissionedWellController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.commissionedwell.CommissionedWellScheduleView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class CommissionedWellScheduleSectionSummaryService implements ProjectSectionSummaryService {

  static final String TEMPLATE_PATH = "project/commissionedwell/commissionedWellScheduleSectionSummary.ftl";
  static final String PAGE_NAME = CommissionedWellController.TASK_LIST_NAME;
  static final String SECTION_ID = "commissioned-well-schedules";
  static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  static final int DISPLAY_ORDER = ProjectTask.WELLS.getDisplayOrder();

  private final CommissionedWellScheduleSummaryService commissionedWellScheduleSummaryService;
  private final DifferenceService differenceService;
  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Autowired
  public CommissionedWellScheduleSectionSummaryService(
      CommissionedWellScheduleSummaryService commissionedWellScheduleSummaryService,
      DifferenceService differenceService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService
  ) {
    this.commissionedWellScheduleSummaryService = commissionedWellScheduleSummaryService;
    this.differenceService = differenceService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return commissionedWellScheduleSummaryService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {

    var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    var commissionedWellScheduleViews = commissionedWellScheduleSummaryService.getCommissionedWellScheduleViews(detail);
    summaryModel.put("commissionedWellScheduleDiffModel", getCommissionedWellScheduleDifferenceModel(
        detail,
        commissionedWellScheduleViews
    ));

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private List<Map<String, ?>> getCommissionedWellScheduleDifferenceModel(
      ProjectDetail projectDetail,
      List<CommissionedWellScheduleView> currentCommissionedWellScheduleViews
  ) {
    var previousViews = commissionedWellScheduleSummaryService.getCommissionedWellScheduleViewViewsByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    );

    return differenceService.differentiateComplexLists(
        currentCommissionedWellScheduleViews,
        previousViews,
        Set.of("summaryLinks"),
        CommissionedWellScheduleView::getDisplayOrder,
        CommissionedWellScheduleView::getDisplayOrder
    );
  }
}
