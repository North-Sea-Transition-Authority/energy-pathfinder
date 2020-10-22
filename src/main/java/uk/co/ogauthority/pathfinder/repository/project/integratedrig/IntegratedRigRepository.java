package uk.co.ogauthority.pathfinder.repository.project.integratedrig;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.integratedrig.IntegratedRig;

@Repository
public interface IntegratedRigRepository extends CrudRepository<IntegratedRig, Integer> {
}
