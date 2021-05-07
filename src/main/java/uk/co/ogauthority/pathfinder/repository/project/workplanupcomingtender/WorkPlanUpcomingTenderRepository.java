package uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;

@Repository
public interface WorkPlanUpcomingTenderRepository extends CrudRepository<WorkPlanUpcomingTender, Integer> {

  List<WorkPlanUpcomingTender> findByProjectDetailOrderByIdAsc(ProjectDetail projectDetail);

  List<WorkPlanUpcomingTender> findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
      Project project,
      Integer version
  );

}
