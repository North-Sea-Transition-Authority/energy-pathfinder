package uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan;

import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContractCommon;

@Entity
@Table(name = "work_plan_awarded_contracts")
public class ForwardWorkPlanAwardedContract extends AwardedContractCommon {

  public ForwardWorkPlanAwardedContract(ProjectDetail projectDetail) {
    super(projectDetail);
  }

  public ForwardWorkPlanAwardedContract() {}
}
