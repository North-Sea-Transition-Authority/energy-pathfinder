package uk.co.ogauthority.pathfinder.repository.projectupdate;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;

@Repository
public interface RegulatorUpdateRequestRepository extends CrudRepository<RegulatorUpdateRequest, Integer> {

  Optional<RegulatorUpdateRequest> findByProjectDetail(ProjectDetail projectDetail);

  boolean existsByProjectDetail(ProjectDetail projectDetail);
}
