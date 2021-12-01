package uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan;

import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityCommon;

@Entity
@Table(name = "work_plan_collaborations")
public class ForwardWorkPlanCollaborationOpportunity extends CollaborationOpportunityCommon {

  public ForwardWorkPlanCollaborationOpportunity(ProjectDetail projectDetail) {
    super(projectDetail);
  }

  public ForwardWorkPlanCollaborationOpportunity() {}
}
