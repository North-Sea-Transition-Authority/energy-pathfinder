package uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;

@Repository
public interface ForwardWorkPlanUpcomingTenderRepository extends CrudRepository<ForwardWorkPlanUpcomingTender, Integer> {

  List<ForwardWorkPlanUpcomingTender> findByProjectDetailOrderByIdAsc(ProjectDetail projectDetail);

  List<ForwardWorkPlanUpcomingTender> findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
      Project project,
      Integer version
  );

}
