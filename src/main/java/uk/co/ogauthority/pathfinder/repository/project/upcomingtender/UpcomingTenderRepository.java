package uk.co.ogauthority.pathfinder.repository.project.upcomingtender;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;

@Repository
public interface UpcomingTenderRepository extends CrudRepository<UpcomingTender, Integer> {

  List<UpcomingTender> findByProjectDetailOrderByIdAsc(ProjectDetail detail);

  List<UpcomingTender> findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(Project project,
                                                                                       Integer version);

  List<UpcomingTender> findAllByProjectDetail_IdIn(List<Integer> projectDetailIds);

  Optional<UpcomingTender> findByIdAndProjectDetail(Integer upcomingTenderId, ProjectDetail projectDetail);

  List<UpcomingTender> findAllByAddedByOrganisationGroupInAndProjectDetail_IsCurrentVersionIsTrueAndProjectDetail_StatusNotIn(
      List<Integer> addedByOrganisationGroupIds, Set<ProjectStatus> statuses);
}
