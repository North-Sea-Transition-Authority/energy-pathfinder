package uk.co.ogauthority.pathfinder.repository.project;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;

@Repository
public interface ProjectOperatorRepository extends CrudRepository<ProjectOperator, Integer> {

  Optional<ProjectOperator> findByProjectDetail(ProjectDetail detail);

  Optional<ProjectOperator> findByProjectDetail_ProjectAndProjectDetail_Version(Project project, Integer version);

  void deleteByProjectDetail(ProjectDetail projectDetail);
}
