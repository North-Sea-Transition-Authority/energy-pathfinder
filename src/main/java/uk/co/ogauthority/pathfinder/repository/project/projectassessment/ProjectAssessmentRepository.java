package uk.co.ogauthority.pathfinder.repository.project.projectassessment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.projectassessment.ProjectAssessment;

@Repository
public interface ProjectAssessmentRepository extends CrudRepository<ProjectAssessment, Integer> {
}
