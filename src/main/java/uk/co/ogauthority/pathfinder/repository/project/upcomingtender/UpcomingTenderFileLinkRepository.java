package uk.co.ogauthority.pathfinder.repository.project.upcomingtender;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTenderFileLink;

@Repository
public interface UpcomingTenderFileLinkRepository extends CrudRepository<UpcomingTenderFileLink, Integer> {

  List<UpcomingTenderFileLink> findAllByUpcomingTender(UpcomingTender upcomingTender);

  Optional<UpcomingTenderFileLink> findByProjectDetailFile(ProjectDetailFile projectDetailFile);

  List<UpcomingTenderFileLink> findAllByProjectDetailFile_ProjectDetail(ProjectDetail projectDetail);
}
