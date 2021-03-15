package uk.co.ogauthority.pathfinder.service.project.decommissioningschedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.decommissioningschedule.DecommissioningScheduleController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.decommissioningschedule.DecommissioningScheduleView;
import uk.co.ogauthority.pathfinder.model.view.decommissioningschedule.DecommissioningScheduleViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class DecommissioningScheduleSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/decommissioningschedule/decommissioningScheduleSectionSummary.ftl";
  public static final String PAGE_NAME = DecommissioningScheduleController.PAGE_NAME;
  public static final String SECTION_ID = "decommissioningSchedule";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.DECOMMISSIONING_SCHEDULE.getDisplayOrder();

  private final DecommissioningScheduleService decommissioningScheduleService;
  private final DifferenceService differenceService;

  @Autowired
  public DecommissioningScheduleSectionSummaryService(
      DecommissioningScheduleService decommissioningScheduleService,
      DifferenceService differenceService) {
    this.decommissioningScheduleService = decommissioningScheduleService;
    this.differenceService = differenceService;
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);

    var decommissioningScheduleView = decommissioningScheduleService.getDecommissioningSchedule(detail)
        .map(DecommissioningScheduleViewUtil::from)
        .orElse(new DecommissioningScheduleView());

    summaryModel.put("decommissioningScheduleDiffModel", getDecommissioningScheduleDifferenceModel(
        detail,
        decommissioningScheduleView
    ));

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private Map<String, Object> getDecommissioningScheduleDifferenceModel(
      ProjectDetail projectDetail,
      DecommissioningScheduleView currentDecommissioningScheduleView
  ) {

    var previousDecommissioningScheduleView = decommissioningScheduleService.getDecommissioningScheduleByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )
        .map(DecommissioningScheduleViewUtil::from)
        .orElse(new DecommissioningScheduleView());

    return differenceService.differentiate(currentDecommissioningScheduleView, previousDecommissioningScheduleView);
  }
}
