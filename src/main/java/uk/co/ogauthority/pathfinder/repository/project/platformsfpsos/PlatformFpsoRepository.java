package uk.co.ogauthority.pathfinder.repository.project.platformsfpsos;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;

@Repository
public interface PlatformFpsoRepository extends CrudRepository<PlatformFpso, Integer> {
  List<PlatformFpso> findAllByProjectDetailOrderByIdAsc(ProjectDetail detail);

  List<PlatformFpso> findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(Project project, Integer version);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);

  Optional<PlatformFpso> findByIdAndProjectDetail(Integer commissionedWellScheduleId, ProjectDetail projectDetail);
}
