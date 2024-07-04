package uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ForwardWorkPlanCollaborationOpportunityFileLink)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ForwardWorkPlanCollaborationOpportunityFileLink that = (ForwardWorkPlanCollaborationOpportunityFileLink) o;
    return Objects.equals(collaborationOpportunity, that.collaborationOpportunity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), collaborationOpportunity);
  }
}
