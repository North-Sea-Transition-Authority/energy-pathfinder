package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupForm;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupRepository;

@Service
public class ForwardWorkPlanAwardedContractSetupService {

  private final ForwardWorkPlanAwardedContractSetupRepository repository;

  ForwardWorkPlanAwardedContractSetupService(ForwardWorkPlanAwardedContractSetupRepository repository) {
    this.repository = repository;
  }

  public ForwardWorkPlanAwardedContractSetupForm getForwardWorkPlanAwardedContractSetupFormFromDetail(ProjectDetail projectDetail) {
    var form = new ForwardWorkPlanAwardedContractSetupForm();

    getForwardWorkPlanAwardedContractSetup(projectDetail)
        .ifPresent(contractSetup -> form.setHasContractToAdd(contractSetup.getHasContractToAdd()));

    return form;
  }

  public Optional<ForwardWorkPlanAwardedContractSetup> getForwardWorkPlanAwardedContractSetup(ProjectDetail projectDetail) {
    return repository.findByProjectDetail(projectDetail);
  }

  @Transactional
  public void saveAwardedContractSetup(ForwardWorkPlanAwardedContractSetupForm form, ProjectDetail projectDetail) {
    var contractSetup = getForwardWorkPlanAwardedContractSetup(projectDetail)
        .orElse(new ForwardWorkPlanAwardedContractSetup(projectDetail));

    contractSetup.setHasContractToAdd(form.getHasContractToAdd());
    repository.save(contractSetup);
  }
}
