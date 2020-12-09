package uk.co.ogauthority.pathfinder.service.project.integratedrig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.integratedrig.IntegratedRigController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class IntegratedRigSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/integratedrig/integratedRigSectionSummary.ftl";
  public static final String PAGE_NAME = IntegratedRigController.SUMMARY_PAGE_NAME;
  public static final String SECTION_ID = "integrated-rigs";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.INTEGRATED_RIGS.getDisplayOrder();

  private final IntegratedRigSummaryService integratedRigSummaryService;

  @Autowired
  public IntegratedRigSectionSummaryService(IntegratedRigSummaryService integratedRigSummaryService) {
    this.integratedRigSummaryService = integratedRigSummaryService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return integratedRigSummaryService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {

    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);

    var integratedRigViews = integratedRigSummaryService.getIntegratedRigSummaryViews(detail);
    summaryModel.put("integratedRigViews", integratedRigViews);

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );

  }
}
