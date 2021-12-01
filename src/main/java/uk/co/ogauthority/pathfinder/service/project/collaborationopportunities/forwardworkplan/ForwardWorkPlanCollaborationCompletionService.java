package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationCompletionForm;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ForwardWorkPlanCollaborationCompletionService {

  private final ForwardWorkPlanCollaborationSetupService forwardWorkPlanCollaborationSetupService;
  private final ValidationService validationService;

  @Autowired
  public ForwardWorkPlanCollaborationCompletionService(
      ForwardWorkPlanCollaborationSetupService forwardWorkPlanCollaborationSetupService,
      ValidationService validationService
  ) {
    this.forwardWorkPlanCollaborationSetupService = forwardWorkPlanCollaborationSetupService;
    this.validationService = validationService;
  }

  public ForwardWorkPlanCollaborationCompletionForm getForwardWorkPlanCollaborationCompletionFormFromDetail(ProjectDetail projectDetail) {

    final var form = new ForwardWorkPlanCollaborationCompletionForm();

    forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(projectDetail)
        .ifPresent(setup -> form.setHasOtherCollaborationsToAdd(setup.getHasOtherCollaborationToAdd()));

    return form;
  }

  public BindingResult validate(ForwardWorkPlanCollaborationCompletionForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    return validationService.validate(form, bindingResult, validationType);
  }

  @Transactional
  public ForwardWorkPlanCollaborationSetup saveCollaborationCompletionForm(ForwardWorkPlanCollaborationCompletionForm form,
                                                                           ProjectDetail projectDetail) {
    final var forwardWorkPlanCollaborationSetup = forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(
        projectDetail
    )
        .orElse(new ForwardWorkPlanCollaborationSetup());

    forwardWorkPlanCollaborationSetup.setHasOtherCollaborationToAdd(form.getHasOtherCollaborationsToAdd());

    return forwardWorkPlanCollaborationSetupService.persistForwardWorkPlanCollaborationSetup(forwardWorkPlanCollaborationSetup);
  }

  @Transactional
  public void resetHasOtherCollaborationsToAdd(ForwardWorkPlanCollaborationCompletionForm form,
                                               ProjectDetail projectDetail) {
    form.setHasOtherCollaborationsToAdd(null);
    saveCollaborationCompletionForm(form, projectDetail);
  }
}
