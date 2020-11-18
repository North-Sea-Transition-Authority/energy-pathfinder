package uk.co.ogauthority.pathfinder.repository.project.tasks;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.tasks.ProjectTaskListSetup;

@Repository
public interface ProjectTaskListSetupRepository extends CrudRepository<ProjectTaskListSetup, Integer> {

  Optional<ProjectTaskListSetup> findByProjectDetail(ProjectDetail detail);

}
