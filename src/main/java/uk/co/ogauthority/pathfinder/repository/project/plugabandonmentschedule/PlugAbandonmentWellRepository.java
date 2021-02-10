package uk.co.ogauthority.pathfinder.repository.project.plugabandonmentschedule;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentWell;

@Repository
public interface PlugAbandonmentWellRepository extends CrudRepository<PlugAbandonmentWell, Integer> {

  List<PlugAbandonmentWell> findAllByPlugAbandonmentSchedule(PlugAbandonmentSchedule plugAbandonmentSchedule);

  void deleteAllByPlugAbandonmentSchedule(PlugAbandonmentSchedule plugAbandonmentSchedule);
}
