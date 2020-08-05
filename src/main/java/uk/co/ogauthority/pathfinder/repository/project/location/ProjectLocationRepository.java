package uk.co.ogauthority.pathfinder.repository.project.location;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;

public interface ProjectLocationRepository extends CrudRepository<ProjectLocation, Integer> {

  Optional<ProjectLocation> findByProjectDetail(ProjectDetail detail);
}
