package uk.co.ogauthority.pathfinder.repository.project.decommissionedpipeline;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;

@Repository
public interface DecommissionedPipelineRepository extends CrudRepository<DecommissionedPipeline, Integer> {

  Optional<DecommissionedPipeline> findByIdAndProjectDetail(Integer decommissionedPipelineId, ProjectDetail projectDetail);

  List<DecommissionedPipeline> findByProjectDetailOrderByIdAsc(ProjectDetail projectDetail);

  List<DecommissionedPipeline> findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(Project project, Integer version);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);
}
