package uk.co.ogauthority.pathfinder.repository.project.commissionedwell;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;

@Repository
public interface CommissionedWellRepository extends CrudRepository<CommissionedWell, Integer> {

  void deleteAllByCommissionedWellSchedule(CommissionedWellSchedule commissionedWellSchedule);

  void deleteAllByCommissionedWellScheduleIn(List<CommissionedWellSchedule> commissionedWellSchedules);

  List<CommissionedWell> findByCommissionedWellScheduleIn(List<CommissionedWellSchedule> commissionedWellSchedules);

  List<CommissionedWell> findByCommissionedWellSchedule(CommissionedWellSchedule commissionedWellSchedule);
}
