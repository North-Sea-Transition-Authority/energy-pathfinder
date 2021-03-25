package uk.co.ogauthority.pathfinder.repository.projectupdate;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;

@Repository
public interface ProjectUpdateRepository extends CrudRepository<ProjectUpdate, Integer> {
  Optional<ProjectUpdate> findByToDetail(ProjectDetail toDetail);
}
