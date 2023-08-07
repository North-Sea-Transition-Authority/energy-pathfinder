package uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContract;

@Repository
public interface ForwardWorkPlanAwardedContractRepository extends CrudRepository<ForwardWorkPlanAwardedContract, Integer> {

  Optional<ForwardWorkPlanAwardedContract> findByIdAndProjectDetail(Integer awardedContractId, ProjectDetail projectDetail);

  List<ForwardWorkPlanAwardedContract> findByProjectDetailOrderByIdAsc(ProjectDetail projectDetail);

  List<ForwardWorkPlanAwardedContract> findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(Project project, Integer version);

  void deleteAllByProjectDetail(ProjectDetail projectDetail);

  boolean existsByProjectDetail(ProjectDetail projectDetail);
}
