package uk.co.ogauthority.pathfinder.repository.pipeline;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import uk.co.ogauthority.pathfinder.model.entity.pipeline.Pipeline;

public interface PipelinesRepository extends CrudRepository<Pipeline, Integer> {

  List<Pipeline> findAllByNameContainingIgnoreCase(String searchTerm);
}
