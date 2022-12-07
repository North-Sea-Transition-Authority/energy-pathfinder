package uk.co.ogauthority.pathfinder.repository.project.projectcontributor;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;

@Repository
public interface ProjectContributorRepository extends CrudRepository<ProjectContributor, Integer> {

  List<ProjectContributor> findAllByProjectDetail(ProjectDetail projectDetail);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);

  List<ProjectContributor> findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(Project project,
                                                                                           Integer version);
}