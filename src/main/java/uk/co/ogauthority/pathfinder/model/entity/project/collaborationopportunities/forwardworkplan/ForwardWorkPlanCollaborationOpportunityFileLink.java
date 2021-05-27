package uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityFileLinkCommon;
import uk.co.ogauthority.pathfinder.service.entityduplication.ChildEntity;

@Entity
@Table(name = "work_plan_collab_file_links")
public class ForwardWorkPlanCollaborationOpportunityFileLink
    extends CollaborationOpportunityFileLinkCommon
    implements ChildEntity<Integer, ForwardWorkPlanCollaborationOpportunity> {

  @ManyToOne
  @JoinColumn(name = "opportunity_id")
  private ForwardWorkPlanCollaborationOpportunity collaborationOpportunity;

  public ForwardWorkPlanCollaborationOpportunity getCollaborationOpportunity() {
    return collaborationOpportunity;
  }

  public void setCollaborationOpportunity(ForwardWorkPlanCollaborationOpportunity collaborationOpportunity) {
    this.collaborationOpportunity = collaborationOpportunity;
  }

  @Override
  public void clearId() {
    super.setId(null);
  }

  @Override
  public void setParent(ForwardWorkPlanCollaborationOpportunity parentEntity) {
    setCollaborationOpportunity(parentEntity);
  }

  @Override
  public ForwardWorkPlanCollaborationOpportunity getParent() {
    return getCollaborationOpportunity();
  }
}
