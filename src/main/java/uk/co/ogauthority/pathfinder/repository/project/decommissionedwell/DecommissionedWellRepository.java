package uk.co.ogauthority.pathfinder.repository.project.decommissionedwell;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedwell.DecommissionedWell;

@Repository
public interface DecommissionedWellRepository extends CrudRepository<DecommissionedWell, Integer> {

  Optional<DecommissionedWell> findByIdAndProjectDetail(Integer decommissionedWellId, ProjectDetail projectDetail);

  List<DecommissionedWell> findByProjectDetailOrderByIdAsc(ProjectDetail projectDetail);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);
}
