package uk.co.ogauthority.pathfinder.repository.project.projectInformation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.projectInformation.ProjectInformation;

@Repository
public interface ProjectInformationRepository extends CrudRepository<ProjectInformation, Integer> {
}
