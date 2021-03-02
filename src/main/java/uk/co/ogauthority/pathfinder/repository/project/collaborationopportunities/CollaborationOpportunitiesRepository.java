package uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;

@Repository
public interface CollaborationOpportunitiesRepository extends CrudRepository<CollaborationOpportunity, Integer> {

  List<CollaborationOpportunity> findAllByProjectDetailOrderByIdAsc(ProjectDetail detail);

  List<CollaborationOpportunity> findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(Project project, Integer version);
}
