package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityViewUtil;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class CollaborationOpportunitiesSummaryService {
  public static final String ERROR_FIELD_NAME = "collaboration-opportunity-%d";
  public static final String EMPTY_LIST_ERROR = "You must add at least one collaboration opportunity";
  public static final String ERROR_MESSAGE = "Collaboration opportunity %d is incomplete";

  private final CollaborationOpportunitiesService collaborationOpportunitiesService;

  @Autowired
  public CollaborationOpportunitiesSummaryService(CollaborationOpportunitiesService collaborationOpportunitiesService) {
    this.collaborationOpportunitiesService = collaborationOpportunitiesService;
  }

  public List<CollaborationOpportunityView> getSummaryViews(ProjectDetail detail) {
    return createCollaborationOpportunityViews(
        collaborationOpportunitiesService.getOpportunitiesForDetail(detail),
        ValidationType.NO_VALIDATION
    );
  }

  public List<CollaborationOpportunityView> getValidatedSummaryViews(ProjectDetail detail) {
    return createCollaborationOpportunityViews(
        collaborationOpportunitiesService.getOpportunitiesForDetail(detail),
        ValidationType.FULL
    );
  }

  public CollaborationOpportunityView getView(CollaborationOpportunity opportunity, Integer displayOrder) {
    return CollaborationOpportunityViewUtil.createView(opportunity, displayOrder);
  }

  public List<ErrorItem> getErrors(List<CollaborationOpportunityView> views) {
    return SummaryUtil.getErrors(new ArrayList<>(views), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public ValidationResult validateViews(List<CollaborationOpportunityView> views) {
    return SummaryUtil.validateViews(new ArrayList<>(views));
  }

  public List<CollaborationOpportunityView> createCollaborationOpportunityViews(
      List<CollaborationOpportunity> collaborationOpportunities,
      ValidationType validationType
  ) {
    return IntStream.range(0, collaborationOpportunities.size())
        .mapToObj(index -> {

          var opportunity = collaborationOpportunities.get(index);
          var displayIndex = index + 1;

          return validationType.equals(ValidationType.NO_VALIDATION)
              ? CollaborationOpportunityViewUtil.createView(opportunity, displayIndex)
              : CollaborationOpportunityViewUtil.createView(
                  opportunity,
                  displayIndex,
                  collaborationOpportunitiesService.isValid(opportunity, validationType)
                );
        })
        .collect(Collectors.toList());
  }


}
