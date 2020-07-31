package uk.co.ogauthority.pathfinder.repository.project;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Integer> {
}
