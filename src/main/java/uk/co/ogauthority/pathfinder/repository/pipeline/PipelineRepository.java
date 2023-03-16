package uk.co.ogauthority.pathfinder.repository.pipeline;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.pipeline.Pipeline;

@Repository
public interface PipelineRepository extends CrudRepository<Pipeline, Integer> {

  List<Pipeline> findAllByNameContainingIgnoreCaseAndHistoricStatusFalse(String searchTerm);
}
