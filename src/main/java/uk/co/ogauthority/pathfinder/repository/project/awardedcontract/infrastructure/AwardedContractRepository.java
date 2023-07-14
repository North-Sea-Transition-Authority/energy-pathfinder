package uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.AwardedContract;

@Repository
public interface AwardedContractRepository extends CrudRepository<AwardedContract, Integer> {

  Optional<AwardedContract> findByIdAndProjectDetail(Integer awardedContractId, ProjectDetail projectDetail);

  List<AwardedContract> findByProjectDetailOrderByIdAsc(ProjectDetail projectDetail);

  List<AwardedContract> findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(Project project, Integer version);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);
}
