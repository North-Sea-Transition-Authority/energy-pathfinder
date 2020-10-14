package uk.co.ogauthority.pathfinder.repository.project.subseainfrastructure;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;

@Repository
public interface SubseaInfrastructureRepository extends CrudRepository<SubseaInfrastructure, Integer> {

  Optional<SubseaInfrastructure> findByIdAndProjectDetail(Integer subseaInfrastructureId, ProjectDetail projectDetail);
}
