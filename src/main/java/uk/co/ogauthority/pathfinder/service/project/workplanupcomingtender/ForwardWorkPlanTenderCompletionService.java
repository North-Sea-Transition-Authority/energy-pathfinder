package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanTenderSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderCompletionForm;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ForwardWorkPlanTenderCompletionService {

  private final ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;
  private final ValidationService validationService;

  @Autowired
  public ForwardWorkPlanTenderCompletionService(ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService,
                                                ValidationService validationService) {
    this.forwardWorkPlanTenderSetupService = forwardWorkPlanTenderSetupService;
    this.validationService = validationService;
  }

  public ForwardWorkPlanTenderCompletionForm getForwardWorkPlanTenderCompletionFormFromDetail(
      ProjectDetail projectDetail
  ) {

    final var form = new ForwardWorkPlanTenderCompletionForm();

    forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(projectDetail)
        .ifPresent(workPlanTenderSetup -> form.setHasOtherTendersToAdd(workPlanTenderSetup.getHasOtherTendersToAdd()));

    return form;
  }

  public BindingResult validate(ForwardWorkPlanTenderCompletionForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    return validationService.validate(form, bindingResult, validationType);
  }

  @Transactional
  public ForwardWorkPlanTenderSetup saveForwardWorkPlanTenderCompletionForm(ForwardWorkPlanTenderCompletionForm form,
                                                                            ProjectDetail projectDetail) {
    final var forwardWorkPlanTenderSetup = forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(
        projectDetail
    )
        .orElse(new ForwardWorkPlanTenderSetup());

    forwardWorkPlanTenderSetup.setHasOtherTendersToAdd(form.getHasOtherTendersToAdd());

    return forwardWorkPlanTenderSetupService.persistForwardWorkPlanTenderSetup(forwardWorkPlanTenderSetup);
  }

  @Transactional
  public ForwardWorkPlanTenderSetup resetHasOtherTendersToAdd(ForwardWorkPlanTenderCompletionForm form,
                                                              ProjectDetail projectDetail) {
    form.setHasOtherTendersToAdd(null);
    return saveForwardWorkPlanTenderCompletionForm(form, projectDetail);
  }
}
