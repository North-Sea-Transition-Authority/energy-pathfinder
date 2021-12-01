package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.infrastructure.InfrastructureCollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure.InfrastructureCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesSectionSummaryService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class InfrastructureCollaborationOpportunitiesSectionSummaryService
    extends CollaborationOpportunitiesSectionSummaryService<InfrastructureCollaborationOpportunityView>
    implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH =
      "project/collaborationopportunities/infrastructure/infrastructureCollaborationOpportunitiesSectionSummary.ftl";
  public static final String PAGE_NAME = InfrastructureCollaborationOpportunitiesController.PAGE_NAME;
  public static final String SECTION_ID = "collaboration-opportunities";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.COLLABORATION_OPPORTUNITIES.getDisplayOrder();

  private final InfrastructureCollaborationOpportunitiesSummaryService infrastructureCollaborationOpportunitiesSummaryService;

  @Autowired
  public InfrastructureCollaborationOpportunitiesSectionSummaryService(
      InfrastructureCollaborationOpportunitiesSummaryService infrastructureCollaborationOpportunitiesSummaryService,
      DifferenceService differenceService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService
  ) {
    super(
        projectSectionSummaryCommonModelService,
        differenceService
    );
    this.infrastructureCollaborationOpportunitiesSummaryService = infrastructureCollaborationOpportunitiesSummaryService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return infrastructureCollaborationOpportunitiesSummaryService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    final var summaryModel = super.getSummaryModel(detail, InfrastructureCollaborationOpportunityView.class);
    return super.getProjectSectionSummary(summaryModel);
  }

  @Override
  protected List<InfrastructureCollaborationOpportunityView> getCurrentCollaborationOpportunityViews(
      ProjectDetail projectDetail
  ) {
    return infrastructureCollaborationOpportunitiesSummaryService.getSummaryViews(projectDetail);
  }

  @Override
  protected List<InfrastructureCollaborationOpportunityView> getPreviousCollaborationOpportunityViews(
      ProjectDetail projectDetail
  ) {
    return infrastructureCollaborationOpportunitiesSummaryService.getSummaryViews(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    );
  }

  @Override
  protected String getTemplatePath() {
    return TEMPLATE_PATH;
  }

  @Override
  protected String getPageName() {
    return PAGE_NAME;
  }

  @Override
  protected String getSectionId() {
    return SECTION_ID;
  }

  @Override
  protected Integer getDisplayOrder() {
    return DISPLAY_ORDER;
  }

  @Override
  protected SidebarSectionLink getSectionLink() {
    return SECTION_LINK;
  }

  @Override
  protected Integer getViewDisplayOrder(InfrastructureCollaborationOpportunityView view) {
    return view.getDisplayOrder();
  }

  @Override
  protected List<UploadedFileView> getUploadedFileViews(InfrastructureCollaborationOpportunityView view) {
    return view.getUploadedFileViews();
  }
}