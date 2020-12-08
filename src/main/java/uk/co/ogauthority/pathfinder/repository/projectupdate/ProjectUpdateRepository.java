package uk.co.ogauthority.pathfinder.repository.projectupdate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;

@Repository
public interface ProjectUpdateRepository extends CrudRepository<ProjectUpdate, Integer> {
}
