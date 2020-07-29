package uk.co.ogauthority.pathfinder.repository.project;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetails;

@Repository
public interface ProjectDetailsRepository extends CrudRepository<ProjectDetails, Integer> {

  Optional<ProjectDetails> findByProjectIdAndIsCurrentVersionIsTrue(Integer projectId);
}
