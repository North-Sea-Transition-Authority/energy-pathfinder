package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupForm;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupViewUtil;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ForwardWorkPlanAwardedContractSetupService {

  private final ForwardWorkPlanAwardedContractSetupRepository repository;
  private final ValidationService validationService;

  @Autowired
  ForwardWorkPlanAwardedContractSetupService(ForwardWorkPlanAwardedContractSetupRepository repository,
                                             ValidationService validationService) {
    this.repository = repository;
    this.validationService = validationService;
  }

  public ForwardWorkPlanAwardedContractSetupForm getAwardedContractSetupFormFromDetail(ProjectDetail projectDetail) {
    var form = new ForwardWorkPlanAwardedContractSetupForm();

    getForwardWorkPlanAwardedContractSetup(projectDetail)
        .ifPresent(contractSetup -> form.setHasContractToAdd(contractSetup.getHasContractToAdd()));

    return form;
  }

  public Optional<ForwardWorkPlanAwardedContractSetup> getForwardWorkPlanAwardedContractSetup(ProjectDetail projectDetail) {
    return repository.findByProjectDetail(projectDetail);
  }

  public Optional<ForwardWorkPlanAwardedContractSetup> getForwardWorkPlanAwardedContractSetupForProjectVersion(
      Project project,
      int version
  ) {
    return repository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version);
  }

  public BindingResult validate(ForwardWorkPlanAwardedContractSetupForm form,
                                BindingResult bindingResult) {
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  public boolean isValid(ProjectDetail projectDetail) {
    var form = getAwardedContractSetupFormFromDetail(projectDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult);
    return !bindingResult.hasErrors();
  }

  @Transactional
  public void saveAwardedContractSetup(ForwardWorkPlanAwardedContractSetupForm form, ProjectDetail projectDetail) {
    var contractSetup = getForwardWorkPlanAwardedContractSetup(projectDetail)
        .orElse(new ForwardWorkPlanAwardedContractSetup(projectDetail));

    contractSetup.setHasContractToAdd(form.getHasContractToAdd());
    repository.save(contractSetup);
  }

  @Transactional
  public void deleteByProjectDetail(ProjectDetail projectDetail) {
    repository.deleteByProjectDetail(projectDetail);
  }

  protected ForwardWorkPlanAwardedContractSetupView getAwardedContractSetupView(ProjectDetail projectDetail) {
    var setUp = getForwardWorkPlanAwardedContractSetup(projectDetail)
        .orElse(new ForwardWorkPlanAwardedContractSetup());
    return ForwardWorkPlanAwardedContractSetupViewUtil.from(setUp);
  }

  protected ForwardWorkPlanAwardedContractSetupView getAwardedContractSetupView(Project project, int version) {
    var setUp = getForwardWorkPlanAwardedContractSetupForProjectVersion(project, version)
        .orElse(new ForwardWorkPlanAwardedContractSetup());
    return ForwardWorkPlanAwardedContractSetupViewUtil.from(setUp);
  }
}
