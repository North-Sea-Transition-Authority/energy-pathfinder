package uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityFileLink;

@Repository
public interface CollaborationOpportunityFileLinkRepository
    extends CrudRepository<CollaborationOpportunityFileLink, Integer> {

  List<CollaborationOpportunityFileLink> findAllByCollaborationOpportunity(
      CollaborationOpportunity collaborationOpportunity
  );

  Optional<CollaborationOpportunityFileLink> findByProjectDetailFile(ProjectDetailFile projectDetailFile);

  List<CollaborationOpportunityFileLink> findAllByProjectDetailFile_ProjectDetail(ProjectDetail projectDetail);

}
