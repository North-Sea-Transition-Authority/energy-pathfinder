package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityViewUtil;

@Service
public class CollaborationOpportunitiesSummaryService {

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
                  false //TODO update with isValid method call
                );
        })
        .collect(Collectors.toList());
  }


}
