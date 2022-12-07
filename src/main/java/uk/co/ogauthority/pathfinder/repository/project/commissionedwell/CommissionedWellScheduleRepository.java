package uk.co.ogauthority.pathfinder.repository.project.commissionedwell;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;

@Repository
public interface CommissionedWellScheduleRepository extends CrudRepository<CommissionedWellSchedule, Integer> {

  List<CommissionedWellSchedule> findByProjectDetailOrderByIdAsc(ProjectDetail projectDetail);

  List<CommissionedWellSchedule> findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(Project project,
                                                                                                 int version);
}
