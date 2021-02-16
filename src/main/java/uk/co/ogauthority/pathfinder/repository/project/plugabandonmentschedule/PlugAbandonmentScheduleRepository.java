package uk.co.ogauthority.pathfinder.repository.project.plugabandonmentschedule;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;

@Repository
public interface PlugAbandonmentScheduleRepository extends CrudRepository<PlugAbandonmentSchedule, Integer> {

  Optional<PlugAbandonmentSchedule> findByIdAndProjectDetail(Integer plugAbandonmentScheduleId, ProjectDetail projectDetail);

  List<PlugAbandonmentSchedule> findByProjectDetailOrderByIdAsc(ProjectDetail projectDetail);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);
}
