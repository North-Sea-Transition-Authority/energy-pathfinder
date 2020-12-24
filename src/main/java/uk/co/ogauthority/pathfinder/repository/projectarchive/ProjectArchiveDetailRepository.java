package uk.co.ogauthority.pathfinder.repository.projectarchive;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectarchive.ProjectArchiveDetail;

@Repository
public interface ProjectArchiveDetailRepository extends CrudRepository<ProjectArchiveDetail, Integer> {

  Optional<ProjectArchiveDetail> findByProjectDetail(ProjectDetail projectDetail);
}
