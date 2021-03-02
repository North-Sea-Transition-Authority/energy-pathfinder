package uk.co.ogauthority.pathfinder.repository.project.subseainfrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;

@Repository
public interface SubseaInfrastructureRepository extends CrudRepository<SubseaInfrastructure, Integer> {

  Optional<SubseaInfrastructure> findByIdAndProjectDetail(Integer subseaInfrastructureId, ProjectDetail projectDetail);

  List<SubseaInfrastructure> findByProjectDetailOrderByIdAsc(ProjectDetail projectDetail);

  List<SubseaInfrastructure> findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(Project project, Integer version);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);
}
