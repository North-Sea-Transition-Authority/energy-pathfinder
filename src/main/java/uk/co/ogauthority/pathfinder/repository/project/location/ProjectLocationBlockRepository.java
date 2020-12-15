package uk.co.ogauthority.pathfinder.repository.project.location;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;

@Repository
public interface ProjectLocationBlockRepository extends CrudRepository<ProjectLocationBlock, Integer> {
  List<ProjectLocationBlock> findAllByProjectLocation(ProjectLocation projectLocation);

  List<ProjectLocationBlock> findAllByProjectLocationOrderByBlockReference(ProjectLocation projectLocation);

  void deleteAllByProjectLocation(ProjectLocation projectLocation);
}
