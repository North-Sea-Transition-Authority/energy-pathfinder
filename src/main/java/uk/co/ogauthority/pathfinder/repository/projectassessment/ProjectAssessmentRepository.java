package uk.co.ogauthority.pathfinder.repository.projectassessment;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectassessment.ProjectAssessment;

@Repository
public interface ProjectAssessmentRepository extends CrudRepository<ProjectAssessment, Integer> {

  Optional<ProjectAssessment> findByProjectDetail(ProjectDetail projectDetail);
}
