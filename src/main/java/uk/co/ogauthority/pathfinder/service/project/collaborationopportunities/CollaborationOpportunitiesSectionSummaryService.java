package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.CollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
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
  private final DifferenceService differenceService;

  @Autowired
  public CollaborationOpportunitiesSectionSummaryService(
      CollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService,
      DifferenceService differenceService) {
    this.collaborationOpportunitiesSummaryService = collaborationOpportunitiesSummaryService;
    this.differenceService = differenceService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return collaborationOpportunitiesSummaryService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);

    var currentCollaborationOpportunityViews = collaborationOpportunitiesSummaryService.getSummaryViews(detail);
    var previousCollaborationOpportunityViews = collaborationOpportunitiesSummaryService.getSummaryViews(
        detail.getProject(),
        detail.getVersion() - 1
    );

    List<Map<String, ?>> collaborationOpportunitiesDiffList = new ArrayList<>();

    currentCollaborationOpportunityViews.forEach(collaborationOpportunityView -> {

      var collaborationOpportunityModel = new HashMap<String, Object>();

      var previousCollaborationOpportunityView = previousCollaborationOpportunityViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(collaborationOpportunityView.getDisplayOrder()))
          .findFirst()
          .orElse(new CollaborationOpportunityView());

      var collaborationOpportunityDiffModel = differenceService.differentiate(
          collaborationOpportunityView,
          previousCollaborationOpportunityView,
          Set.of("summaryLinks", "uploadedFileViews")
      );

      var uploadedFileDiffModel = differenceService.differentiateComplexLists(
          collaborationOpportunityView.getUploadedFileViews(),
          previousCollaborationOpportunityView.getUploadedFileViews(),
          Set.of("fileUploadedTime"),
          UploadedFileView::getFileId,
          UploadedFileView::getFileId
      );

      collaborationOpportunityModel.put("collaborationOpportunityDiff", collaborationOpportunityDiffModel);
      collaborationOpportunityModel.put("collaborationOpportunityFiles", uploadedFileDiffModel);

      collaborationOpportunitiesDiffList.add(collaborationOpportunityModel);

    });

    summaryModel.put("collaborationOpportunityDiffModel", collaborationOpportunitiesDiffList);

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}