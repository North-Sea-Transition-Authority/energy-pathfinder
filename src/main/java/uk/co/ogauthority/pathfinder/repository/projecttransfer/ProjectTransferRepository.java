package uk.co.ogauthority.pathfinder.repository.projecttransfer;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projecttransfer.ProjectTransfer;

@Repository
public interface ProjectTransferRepository extends CrudRepository<ProjectTransfer, Integer> {

  Optional<ProjectTransfer> findByProjectDetail(ProjectDetail projectDetail);
}
