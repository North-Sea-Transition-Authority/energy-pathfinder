package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.CollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class CollaborationOpportunitiesSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/collaborationopportunities/collaborationOpportunitiesSectionSummary.ftl";
  public static final String PAGE_NAME = CollaborationOpportunitiesController.PAGE_NAME;
  public static final String SECTION_ID = "collaboration-opportunities";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.COLLABORATION_OPPORTUNITIES.getDisplayOrder();

  private final CollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService;
  private final CollaborationOpportunitiesService collaborationOpportunitiesService;

  @Autowired
  public CollaborationOpportunitiesSectionSummaryService(
      CollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService,
      CollaborationOpportunitiesService collaborationOpportunitiesService) {
    this.collaborationOpportunitiesSummaryService = collaborationOpportunitiesSummaryService;
    this.collaborationOpportunitiesService = collaborationOpportunitiesService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return collaborationOpportunitiesService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);

    summaryModel.put("collaborationOpportunityViews", collaborationOpportunitiesSummaryService.getSummaryViews(detail));

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}