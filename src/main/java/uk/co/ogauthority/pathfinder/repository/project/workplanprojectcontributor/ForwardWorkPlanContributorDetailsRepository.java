package uk.co.ogauthority.pathfinder.repository.project.workplanprojectcontributor;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanprojectcontribution.ForwardWorkPlanContributorDetails;

public interface ForwardWorkPlanContributorDetailsRepository extends CrudRepository<ForwardWorkPlanContributorDetails, Integer> {

  Optional<ForwardWorkPlanContributorDetails> findByProjectDetail(ProjectDetail projectDetail);

  void deleteByProjectDetail(ProjectDetail projectDetail);

  Optional<ForwardWorkPlanContributorDetails> findByProjectDetail_ProjectAndProjectDetail_Version(
      Project project,
      Integer version
  );
}
