package uk.co.ogauthority.pathfinder.repository.project.projectinformation;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;

@Repository
public interface ProjectInformationRepository extends CrudRepository<ProjectInformation, Integer> {
  Optional<ProjectInformation> findByProjectDetail(ProjectDetail projectDetail);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);
}
