package uk.co.ogauthority.pathfinder.repository.project.decommissioningschedule;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissioningschedule.DecommissioningSchedule;

@Repository
public interface DecommissioningScheduleRepository extends CrudRepository<DecommissioningSchedule, Integer> {

  Optional<DecommissioningSchedule> findByProjectDetail(ProjectDetail projectDetail);

  Optional<DecommissioningSchedule> findByProjectDetail_ProjectAndProjectDetail_Version(Project project, Integer version);

  void deleteByProjectDetail(ProjectDetail projectDetail);
}
