package uk.co.ogauthority.pathfinder.repository.project.decommissionedpipeline;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;

@Repository
public interface DecommissionedPipelineRepository extends CrudRepository<DecommissionedPipeline, Integer> {
}
