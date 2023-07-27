package uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;

@Repository
public interface ForwardWorkPlanAwardedContractSetupRepository extends CrudRepository<ForwardWorkPlanAwardedContractSetup, Integer> {

  Optional<ForwardWorkPlanAwardedContractSetup> findByProjectDetail(ProjectDetail projectDetail);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);
}
