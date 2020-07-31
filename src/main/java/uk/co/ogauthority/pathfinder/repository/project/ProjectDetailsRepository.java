package uk.co.ogauthority.pathfinder.repository.project;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;

@Repository
public interface ProjectDetailsRepository extends CrudRepository<ProjectDetail, Integer> {

  Optional<ProjectDetail> findByProjectIdAndIsCurrentVersionIsTrue(Integer projectId);
}
