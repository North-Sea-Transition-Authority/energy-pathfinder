package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityViewUtil;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesSummaryService;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class ForwardWorkPlanCollaborationOpportunitiesSummaryService extends
    CollaborationOpportunitiesSummaryService<
        ForwardWorkPlanCollaborationOpportunity,
        ForwardWorkPlanCollaborationOpportunityView
    > {

  public static final String ERROR_FIELD_NAME = "collaboration-opportunity-%d";
  public static final String EMPTY_LIST_ERROR = "You must add at least one collaboration opportunity";
  public static final String ERROR_MESSAGE = "Collaboration opportunity %d is incomplete";

  private final ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;
  private final ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService;

  @Autowired
  public ForwardWorkPlanCollaborationOpportunitiesSummaryService(
      ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService,
      ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService
  ) {
    this.forwardWorkPlanCollaborationOpportunityService = forwardWorkPlanCollaborationOpportunityService;
    this.forwardWorkPlanCollaborationOpportunityFileLinkService = forwardWorkPlanCollaborationOpportunityFileLinkService;
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

  public List<ErrorItem> getErrors(List<ForwardWorkPlanCollaborationOpportunityView> views) {
    return SummaryUtil.getErrors(new ArrayList<>(views), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public ValidationResult validateViews(List<ForwardWorkPlanCollaborationOpportunityView> views) {
    return SummaryUtil.validateViews(new ArrayList<>(views));
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

  public boolean canShowInTaskList(ProjectDetail projectDetail) {
    return forwardWorkPlanCollaborationOpportunityService.canShowInTaskList(projectDetail);
  }

  private List<UploadedFileView> getUploadedFileViews(ForwardWorkPlanCollaborationOpportunity opportunity) {
    return forwardWorkPlanCollaborationOpportunityFileLinkService.getFileUploadViewsLinkedToOpportunity(opportunity);
  }

  @Override
  public ForwardWorkPlanCollaborationOpportunityView getView(ForwardWorkPlanCollaborationOpportunity opportunity,
                                                             Integer displayOrder) {
    final var uploadedFileViews = getUploadedFileViews(opportunity);
    return ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        opportunity,
        displayOrder,
        uploadedFileViews
    );
  }

  @Override
  public ForwardWorkPlanCollaborationOpportunityView getView(ForwardWorkPlanCollaborationOpportunity opportunity,
                                                             Integer displayOrder,
                                                             boolean isValid) {
    final var uploadedFileViews = getUploadedFileViews(opportunity);
    return ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        opportunity,
        displayOrder,
        uploadedFileViews,
        isValid
    );
  }

  @Override
  protected boolean isValid(ForwardWorkPlanCollaborationOpportunity opportunity, ValidationType validationType) {
    return forwardWorkPlanCollaborationOpportunityService.isValid(
        opportunity,
        validationType
    );
  }
}
