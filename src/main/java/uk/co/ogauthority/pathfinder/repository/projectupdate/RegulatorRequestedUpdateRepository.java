package uk.co.ogauthority.pathfinder.repository.projectupdate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorRequestedUpdate;

@Repository
public interface RegulatorRequestedUpdateRepository extends CrudRepository<RegulatorRequestedUpdate, Integer> {

  void deleteByProjectUpdate(ProjectUpdate projectUpdate);
}
