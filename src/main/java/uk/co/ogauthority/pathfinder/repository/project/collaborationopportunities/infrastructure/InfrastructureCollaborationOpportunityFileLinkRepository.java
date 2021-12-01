package uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFileLink;

@Repository
public interface InfrastructureCollaborationOpportunityFileLinkRepository
    extends CrudRepository<InfrastructureCollaborationOpportunityFileLink, Integer> {

  List<InfrastructureCollaborationOpportunityFileLink> findAllByCollaborationOpportunity(
      InfrastructureCollaborationOpportunity infrastructureCollaborationOpportunity
  );

  Optional<InfrastructureCollaborationOpportunityFileLink> findByProjectDetailFile(ProjectDetailFile projectDetailFile);

  List<InfrastructureCollaborationOpportunityFileLink> findAllByProjectDetailFile_ProjectDetail(ProjectDetail projectDetail);

}
