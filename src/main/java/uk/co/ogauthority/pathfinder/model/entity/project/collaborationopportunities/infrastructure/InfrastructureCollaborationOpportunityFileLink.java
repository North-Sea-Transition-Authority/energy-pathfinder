package uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityFileLinkCommon;
import uk.co.ogauthority.pathfinder.service.entityduplication.ChildEntity;

@Entity
@Table(name = "collaboration_op_file_links")
public class InfrastructureCollaborationOpportunityFileLink
    extends CollaborationOpportunityFileLinkCommon
    implements ChildEntity<Integer, InfrastructureCollaborationOpportunity> {

  @ManyToOne
  @JoinColumn(name = "opportunity_id")
  private InfrastructureCollaborationOpportunity collaborationOpportunity;

  public InfrastructureCollaborationOpportunity getCollaborationOpportunity() {
    return collaborationOpportunity;
  }

  public void setCollaborationOpportunity(InfrastructureCollaborationOpportunity collaborationOpportunity) {
    this.collaborationOpportunity = collaborationOpportunity;
  }

  @Override
  public void clearId() {
    super.setId(null);
  }

  @Override
  public void setParent(InfrastructureCollaborationOpportunity parentEntity) {
    setCollaborationOpportunity(parentEntity);
  }

  @Override
  public InfrastructureCollaborationOpportunity getParent() {
    return getCollaborationOpportunity();
  }

}
