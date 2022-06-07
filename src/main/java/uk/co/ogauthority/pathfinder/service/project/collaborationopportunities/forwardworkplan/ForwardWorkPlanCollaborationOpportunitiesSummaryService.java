package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesSummaryService;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class ForwardWorkPlanCollaborationOpportunitiesSummaryService extends
    CollaborationOpportunitiesSummaryService<
        ForwardWorkPlanCollaborationOpportunity,
        ForwardWorkPlanCollaborationOpportunityView
    > {

  private final ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;
  private final ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService;
  private final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Autowired
  public ForwardWorkPlanCollaborationOpportunitiesSummaryService(
      ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService,
      ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService,
      ProjectSectionItemOwnershipService projectSectionItemOwnershipService) {
    this.forwardWorkPlanCollaborationOpportunityService = forwardWorkPlanCollaborationOpportunityService;
    this.forwardWorkPlanCollaborationOpportunityFileLinkService = forwardWorkPlanCollaborationOpportunityFileLinkService;
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
  }

  public List<ForwardWorkPlanCollaborationOpportunityView> getSummaryViews(ProjectDetail projectDetail) {
    return createCollaborationOpportunityViews(
        forwardWorkPlanCollaborationOpportunityService.getOpportunitiesForDetail(projectDetail),
        ValidationType.NO_VALIDATION
    );
  }

  public List<ForwardWorkPlanCollaborationOpportunityView> getSummaryViews(Project project, Integer version) {
    return createCollaborationOpportunityViews(
        forwardWorkPlanCollaborationOpportunityService.getOpportunitiesForProjectVersion(project, version),
        ValidationType.NO_VALIDATION
    );
  }

  public List<ForwardWorkPlanCollaborationOpportunityView> getValidatedSummaryViews(ProjectDetail projectDetail) {
    return createCollaborationOpportunityViews(
        forwardWorkPlanCollaborationOpportunityService.getOpportunitiesForDetail(projectDetail),
        ValidationType.FULL
    );
  }

  public ValidationResult validateViews(List<ForwardWorkPlanCollaborationOpportunityView> views) {
    return SummaryUtil.validateViews(new ArrayList<>(views));
  }

  public boolean canShowInTaskList(ProjectDetail projectDetail) {
    return forwardWorkPlanCollaborationOpportunityService.isTaskValidForProjectDetail(projectDetail);
  }

  @Override
  public ForwardWorkPlanCollaborationOpportunityView getView(ForwardWorkPlanCollaborationOpportunity opportunity,
                                                             Integer displayOrder) {

    return getForwardWorkPlanCollaborationOpportunityViewBuilder(
        opportunity,
        displayOrder
    )
        .build();
  }

  @Override
  public ForwardWorkPlanCollaborationOpportunityView getView(ForwardWorkPlanCollaborationOpportunity opportunity,
                                                             Integer displayOrder,
                                                             boolean isValid) {
    return getForwardWorkPlanCollaborationOpportunityViewBuilder(
        opportunity,
        displayOrder
    )
        .isValid(isValid)
        .build();
  }

  @Override
  protected boolean isValid(ForwardWorkPlanCollaborationOpportunity opportunity, ValidationType validationType) {
    return forwardWorkPlanCollaborationOpportunityService.isValid(
        opportunity,
        validationType
    );
  }

  private List<ForwardWorkPlanCollaborationOpportunityView> createCollaborationOpportunityViews(
      List<ForwardWorkPlanCollaborationOpportunity> collaborationOpportunities,
      ValidationType validationType
  ) {
    return super.constructCollaborationOpportunityViews(
        collaborationOpportunities,
        validationType
    );
  }

  private List<UploadedFileView> getUploadedFileViews(ForwardWorkPlanCollaborationOpportunity opportunity) {
    return forwardWorkPlanCollaborationOpportunityFileLinkService.getFileUploadViewsLinkedToOpportunity(opportunity);
  }

  private ForwardWorkPlanCollaborationOpportunityViewBuilder getForwardWorkPlanCollaborationOpportunityViewBuilder(
      ForwardWorkPlanCollaborationOpportunity opportunity,
      Integer displayOrder) {
    var uploadedFileViews = getUploadedFileViews(opportunity);
    var includeSummaryLinks = projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        opportunity.getProjectDetail(),
        new OrganisationGroupIdWrapper(opportunity.getAddedByOrganisationGroup())
    );
    return new ForwardWorkPlanCollaborationOpportunityViewBuilder(
        opportunity,
        displayOrder,
        uploadedFileViews
    )
        .includeSummaryLinks(includeSummaryLinks);
  }
}
