package uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;

@Repository
public interface ForwardWorkPlanCollaborationOpportunityRepository extends
    CrudRepository<ForwardWorkPlanCollaborationOpportunity, Integer> {

  List<ForwardWorkPlanCollaborationOpportunity> findAllByProjectDetailOrderByIdAsc(ProjectDetail detail);

  List<ForwardWorkPlanCollaborationOpportunity> findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
      Project project,
      int version
  );
}
