package uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLink;

@Repository
public interface ForwardWorkPlanCollaborationOpportunityFileLinkRepository
    extends CrudRepository<ForwardWorkPlanCollaborationOpportunityFileLink, Integer> {

  List<ForwardWorkPlanCollaborationOpportunityFileLink> findAllByCollaborationOpportunity(
      ForwardWorkPlanCollaborationOpportunity forwardWorkPlanCollaborationOpportunity
  );

  Optional<ForwardWorkPlanCollaborationOpportunityFileLink> findByProjectDetailFile(ProjectDetailFile projectDetailFile);

  List<ForwardWorkPlanCollaborationOpportunityFileLink> findAllByProjectDetailFile_ProjectDetail(
      ProjectDetail projectDetail
  );
}
