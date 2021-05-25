package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure.InfrastructureCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure.InfrastructureCollaborationOpportunityViewUtil;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class InfrastructureCollaborationOpportunitiesSummaryService {
  public static final String ERROR_FIELD_NAME = "collaboration-opportunity-%d";
  public static final String EMPTY_LIST_ERROR = "You must add at least one collaboration opportunity";
  public static final String ERROR_MESSAGE = "Collaboration opportunity %d is incomplete";

  private final InfrastructureCollaborationOpportunitiesService infrastructureCollaborationOpportunitiesService;
  private final InfrastructureCollaborationOpportunityFileLinkService infrastructureCollaborationOpportunityFileLinkService;

  @Autowired
  public InfrastructureCollaborationOpportunitiesSummaryService(
      InfrastructureCollaborationOpportunitiesService infrastructureCollaborationOpportunitiesService,
      InfrastructureCollaborationOpportunityFileLinkService infrastructureCollaborationOpportunityFileLinkService
  ) {
    this.infrastructureCollaborationOpportunitiesService = infrastructureCollaborationOpportunitiesService;
    this.infrastructureCollaborationOpportunityFileLinkService = infrastructureCollaborationOpportunityFileLinkService;
  }

  public List<InfrastructureCollaborationOpportunityView> getSummaryViews(ProjectDetail detail) {
    return createCollaborationOpportunityViews(
        infrastructureCollaborationOpportunitiesService.getOpportunitiesForDetail(detail),
        ValidationType.NO_VALIDATION
    );
  }

  public List<InfrastructureCollaborationOpportunityView> getSummaryViews(Project project, Integer version) {
    return createCollaborationOpportunityViews(
        infrastructureCollaborationOpportunitiesService.getOpportunitiesForProjectVersion(project, version),
        ValidationType.NO_VALIDATION
    );
  }

  public List<InfrastructureCollaborationOpportunityView> getValidatedSummaryViews(ProjectDetail detail) {
    return createCollaborationOpportunityViews(
        infrastructureCollaborationOpportunitiesService.getOpportunitiesForDetail(detail),
        ValidationType.FULL
    );
  }

  public InfrastructureCollaborationOpportunityView getView(InfrastructureCollaborationOpportunity opportunity,
                                                            Integer displayOrder) {
    var uploadedFileViews =
        infrastructureCollaborationOpportunityFileLinkService.getFileUploadViewsLinkedToOpportunity(opportunity);
    return InfrastructureCollaborationOpportunityViewUtil.createView(opportunity, displayOrder, uploadedFileViews);
  }

  private InfrastructureCollaborationOpportunityView getView(InfrastructureCollaborationOpportunity opportunity,
                                                             Integer displayOrder,
                                                             boolean isValid) {
    var uploadedFileViews =
        infrastructureCollaborationOpportunityFileLinkService.getFileUploadViewsLinkedToOpportunity(opportunity);
    return InfrastructureCollaborationOpportunityViewUtil.createView(
        opportunity,
        displayOrder,
        uploadedFileViews,
        isValid
    );
  }

  public List<ErrorItem> getErrors(List<InfrastructureCollaborationOpportunityView> views) {
    return SummaryUtil.getErrors(new ArrayList<>(views), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public ValidationResult validateViews(List<InfrastructureCollaborationOpportunityView> views) {
    return SummaryUtil.validateViews(new ArrayList<>(views));
  }

  public List<InfrastructureCollaborationOpportunityView> createCollaborationOpportunityViews(
      List<InfrastructureCollaborationOpportunity> collaborationOpportunities,
      ValidationType validationType
  ) {
    return IntStream.range(0, collaborationOpportunities.size())
        .mapToObj(index -> {

          var opportunity = collaborationOpportunities.get(index);
          var displayIndex = index + 1;

          return validationType.equals(ValidationType.NO_VALIDATION)
              ? getView(opportunity, displayIndex)
              : getView(
                  opportunity,
                  displayIndex,
                  infrastructureCollaborationOpportunitiesService.isValid(opportunity, validationType)
                );
        })
        .collect(Collectors.toList());
  }

  public boolean canShowInTaskList(ProjectDetail detail) {
    return infrastructureCollaborationOpportunitiesService.canShowInTaskList(detail);
  }
}
