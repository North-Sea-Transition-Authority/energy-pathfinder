package uk.co.ogauthority.pathfinder.repository.project.projectinformation;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;

@Repository
public interface ProjectInformationRepository extends CrudRepository<ProjectInformation, Integer> {

  Optional<ProjectInformation> findByProjectDetail(ProjectDetail projectDetail);

  Optional<ProjectInformation> findByProjectDetail_ProjectAndProjectDetail_Version(Project project, Integer version);

  void deleteByProjectDetail(ProjectDetail projectDetail);

  @Query("SELECT pi.projectTitle FROM ProjectInformation pi WHERE pi.projectDetail = :detail")
  String findTitleByProjectDetail(ProjectDetail detail);
}
