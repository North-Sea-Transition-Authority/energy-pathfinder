package uk.co.ogauthority.pathfinder.repository.project.awardedcontract;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContract;

@Repository
public interface AwardedContractRepository extends CrudRepository<AwardedContract, Integer> {

  Optional<AwardedContract> findByIdAndProjectDetail(Integer awardedContractId, ProjectDetail projectDetail);

  List<AwardedContract> findByProjectDetailOrderByIdAsc(ProjectDetail projectDetail);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);
}
