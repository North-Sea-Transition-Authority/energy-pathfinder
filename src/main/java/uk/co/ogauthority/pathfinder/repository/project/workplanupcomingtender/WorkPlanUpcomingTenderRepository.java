package uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;

@Repository
public interface WorkPlanUpcomingTenderRepository extends CrudRepository<WorkPlanUpcomingTender, Integer> {

}
