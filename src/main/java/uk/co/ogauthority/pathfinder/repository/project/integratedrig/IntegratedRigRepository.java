package uk.co.ogauthority.pathfinder.repository.project.integratedrig;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.integratedrig.IntegratedRig;

@Repository
public interface IntegratedRigRepository extends CrudRepository<IntegratedRig, Integer> {

  Optional<IntegratedRig> findByIdAndProjectDetail(Integer integratedRigId, ProjectDetail projectDetail);

  List<IntegratedRig> findByProjectDetailOrderByIdAsc(ProjectDetail projectDetail);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);
}
