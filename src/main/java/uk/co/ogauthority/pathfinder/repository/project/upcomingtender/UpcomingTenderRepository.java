package uk.co.ogauthority.pathfinder.repository.project.upcomingtender;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;

@Repository
public interface UpcomingTenderRepository extends CrudRepository<UpcomingTender, Integer> {

  List<UpcomingTender> findByProjectDetail(ProjectDetail detail);



}
