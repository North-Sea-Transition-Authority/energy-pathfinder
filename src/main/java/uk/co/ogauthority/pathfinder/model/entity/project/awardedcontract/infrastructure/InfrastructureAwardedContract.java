package uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure;

import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContractCommon;

@Entity
@Table(name = "awarded_contracts")
public class InfrastructureAwardedContract extends AwardedContractCommon {

  public InfrastructureAwardedContract(ProjectDetail projectDetail) {
    super(projectDetail);
  }

  public InfrastructureAwardedContract() {}
}
