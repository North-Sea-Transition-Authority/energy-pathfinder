package uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityCommon;

@Entity
@Table(name = "collaboration_opportunities")
public class InfrastructureCollaborationOpportunity extends CollaborationOpportunityCommon {

  public InfrastructureCollaborationOpportunity() {
  }

  public InfrastructureCollaborationOpportunity(ProjectDetail projectDetail) {
    super(projectDetail);
  }
}
