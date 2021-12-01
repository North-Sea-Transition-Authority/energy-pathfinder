package uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetup;

@Repository
public interface ForwardWorkPlanCollaborationSetupRepository extends CrudRepository<ForwardWorkPlanCollaborationSetup, Integer> {

  Optional<ForwardWorkPlanCollaborationSetup> findByProjectDetail(ProjectDetail projectDetail);

  Optional<ForwardWorkPlanCollaborationSetup> findByProjectDetail_ProjectAndProjectDetail_Version(Project project,
                                                                                                  int version);

}
