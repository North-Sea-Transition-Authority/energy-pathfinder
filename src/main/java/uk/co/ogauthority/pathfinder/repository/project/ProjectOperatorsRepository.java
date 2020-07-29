package uk.co.ogauthority.pathfinder.repository.project;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;

@Repository
public interface ProjectOperatorsRepository extends CrudRepository<ProjectOperator, Integer> {

}
