package uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;

@Repository
public interface InfrastructureCollaborationOpportunitiesRepository
    extends CrudRepository<InfrastructureCollaborationOpportunity, Integer> {

  List<InfrastructureCollaborationOpportunity> findAllByProjectDetailOrderByIdAsc(ProjectDetail detail);

  List<InfrastructureCollaborationOpportunity> findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
      Project project,
      Integer version
  );
}
