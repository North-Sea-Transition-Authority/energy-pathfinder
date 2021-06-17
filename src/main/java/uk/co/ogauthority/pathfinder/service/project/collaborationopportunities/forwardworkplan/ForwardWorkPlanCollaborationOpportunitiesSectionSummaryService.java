package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesSectionSummaryService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class ForwardWorkPlanCollaborationOpportunitiesSectionSummaryService
    extends CollaborationOpportunitiesSectionSummaryService<ForwardWorkPlanCollaborationOpportunityView>
    implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH =
      "project/collaborationopportunities/forwardworkplan/forwardWorkPlanCollaborationOpportunitiesSectionSummary.ftl";
  public static final String PAGE_NAME = ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME;
  public static final String SECTION_ID = "collaboration-opportunities";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.WORK_PLAN_COLLABORATION_OPPORTUNITIES.getDisplayOrder();

  private final ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService;

  @Autowired
  public ForwardWorkPlanCollaborationOpportunitiesSectionSummaryService(
      ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService,
      DifferenceService differenceService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService
  ) {
    super(
        projectSectionSummaryCommonModelService,
        differenceService
    );
    this.forwardWorkPlanCollaborationOpportunitiesSummaryService = forwardWorkPlanCollaborationOpportunitiesSummaryService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return forwardWorkPlanCollaborationOpportunitiesSummaryService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    final var summaryModel = super.getSummaryModel(detail, ForwardWorkPlanCollaborationOpportunityView.class);
    return super.getProjectSectionSummary(summaryModel);
  }

  @Override
  protected List<ForwardWorkPlanCollaborationOpportunityView> getCurrentCollaborationOpportunityViews(ProjectDetail projectDetail) {
    return forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(projectDetail);
  }

  @Override
  protected List<ForwardWorkPlanCollaborationOpportunityView> getPreviousCollaborationOpportunityViews(ProjectDetail projectDetail) {
    return forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(
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
  protected Integer getViewDisplayOrder(ForwardWorkPlanCollaborationOpportunityView view) {
    return view.getDisplayOrder();
  }

  @Override
  protected List<UploadedFileView> getUploadedFileViews(ForwardWorkPlanCollaborationOpportunityView view) {
    return view.getUploadedFileViews();
  }
}