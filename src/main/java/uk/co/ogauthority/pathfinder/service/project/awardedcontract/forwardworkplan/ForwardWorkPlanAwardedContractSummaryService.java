package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryForm;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupRepository;

@Service
public class ForwardWorkPlanAwardedContractSummaryService {

  private final ForwardWorkPlanAwardedContractSetupRepository repository;

  @Autowired
  public ForwardWorkPlanAwardedContractSummaryService(ForwardWorkPlanAwardedContractSetupRepository repository) {
    this.repository = repository;
  }

  public ForwardWorkPlanAwardedContractSummaryForm getForm(ProjectDetail projectDetail) {
    var awardedContractSetup = getForwardWorkPlanAwardedContractSetup(projectDetail);
    var form = new ForwardWorkPlanAwardedContractSummaryForm();

    awardedContractSetup.ifPresent(
        forwardWorkPlanAwardedContractSetup ->
            form.setHasOtherContractsToAdd(forwardWorkPlanAwardedContractSetup.getHasOtherContractToAdd())
    );
    return form;
  }

  @Transactional
  public void saveAwardedContractSummary(ForwardWorkPlanAwardedContractSummaryForm form, ProjectDetail projectDetail){
    var awardedContractSetup = getForwardWorkPlanAwardedContractSetup(projectDetail)
        .orElse(new ForwardWorkPlanAwardedContractSetup(projectDetail));

    awardedContractSetup.setHasOtherContractToAdd(form.getHasOtherContractsToAdd());
    repository.save(awardedContractSetup);
  }

  private Optional<ForwardWorkPlanAwardedContractSetup> getForwardWorkPlanAwardedContractSetup(ProjectDetail projectDetail) {
    return repository.findByProjectDetail(projectDetail);
  }
}
