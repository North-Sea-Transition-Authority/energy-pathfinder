package uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanTenderSetup;

@Repository
public interface ForwardWorkPlanTenderSetupRepository extends CrudRepository<ForwardWorkPlanTenderSetup, Integer> {

  Optional<ForwardWorkPlanTenderSetup> findByProjectDetail(ProjectDetail projectDetail);

  Optional<ForwardWorkPlanTenderSetup> findByProjectDetail_ProjectAndProjectDetail_Version(Project project,
                                                                                           int version);
}