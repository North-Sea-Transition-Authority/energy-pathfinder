package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.TestCollaborationOpportunityViewCommon;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;

@Service
public class TestCollaborationOpportunitySectionSummaryService
    extends CollaborationOpportunitiesSectionSummaryService<TestCollaborationOpportunityViewCommon> {

  @Autowired
  public TestCollaborationOpportunitySectionSummaryService(
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService,
      DifferenceService differenceService
  ) {
    super(projectSectionSummaryCommonModelService, differenceService);
  }

  @Override
  protected List<TestCollaborationOpportunityViewCommon> getCurrentCollaborationOpportunityViews(
      ProjectDetail projectDetail
  ) {
    final var view = new TestCollaborationOpportunityViewCommon();
    view.setDisplayOrder(1);
    return List.of(view);
  }

  @Override
  protected List<TestCollaborationOpportunityViewCommon> getPreviousCollaborationOpportunityViews(
      ProjectDetail projectDetail
  ) {
    return getCurrentCollaborationOpportunityViews(projectDetail);
  }

  @Override
  protected String getTemplatePath() {
    return "template path";
  }

  @Override
  protected String getPageName() {
    return null;
  }

  @Override
  protected String getSectionId() {
    return "page name";
  }

  @Override
  protected Integer getDisplayOrder() {
    return 100;
  }

  @Override
  protected SidebarSectionLink getSectionLink() {
    return SidebarSectionLink.createAnchorLink("text", "link");
  }

  @Override
  protected Integer getViewDisplayOrder(TestCollaborationOpportunityViewCommon view) {
    return view.getDisplayOrder();
  }

  @Override
  protected List<UploadedFileView> getUploadedFileViews(TestCollaborationOpportunityViewCommon view) {
    return view.getUploadedFileViews();
  }
}
