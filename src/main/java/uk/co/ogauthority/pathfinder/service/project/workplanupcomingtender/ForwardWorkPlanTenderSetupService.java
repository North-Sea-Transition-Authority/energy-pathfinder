package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanTenderSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderSetupForm;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.ForwardWorkPlanTenderSetupView;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.ForwardWorkPlanTenderSetupViewUtil;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.ForwardWorkPlanTenderSetupRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ForwardWorkPlanTenderSetupService {

  private final ForwardWorkPlanTenderSetupRepository forwardWorkPlanTenderSetupRepository;
  private final ValidationService validationService;

  @Autowired
  public ForwardWorkPlanTenderSetupService(ForwardWorkPlanTenderSetupRepository forwardWorkPlanTenderSetupRepository,
                                           ValidationService validationService) {
    this.forwardWorkPlanTenderSetupRepository = forwardWorkPlanTenderSetupRepository;
    this.validationService = validationService;
  }

  protected Optional<ForwardWorkPlanTenderSetup> getForwardWorkPlanTenderSetupForDetail(ProjectDetail projectDetail) {
    return forwardWorkPlanTenderSetupRepository.findByProjectDetail(projectDetail);
  }

  protected Optional<ForwardWorkPlanTenderSetup> getForwardWorkPlanTenderSetupForProjectAndVersion(Project project,
                                                                                                   int version) {
    return forwardWorkPlanTenderSetupRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        project,
        version
    );
  }

  public ForwardWorkPlanTenderSetupForm getForwardWorkPlanTenderSetupFormFromDetail(ProjectDetail projectDetail) {

    final var form = new ForwardWorkPlanTenderSetupForm();

    getForwardWorkPlanTenderSetupForDetail(projectDetail)
        .ifPresent(workPlanTenderSetup -> form.setHasTendersToAdd(workPlanTenderSetup.getHasTendersToAdd()));

    return form;
  }

  public BindingResult validate(ForwardWorkPlanTenderSetupForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    return validationService.validate(form, bindingResult, validationType);
  }

  @Transactional
  public ForwardWorkPlanTenderSetup saveForwardWorkPlanTenderSetup(ForwardWorkPlanTenderSetupForm form,
                                                                   ProjectDetail projectDetail) {
    final var forwardWorkPlanSetup = getForwardWorkPlanTenderSetupForDetail(projectDetail)
        .orElse(new ForwardWorkPlanTenderSetup(projectDetail));

    populateForwardWorkPlanTenderSetupFromForm(forwardWorkPlanSetup, form);

    return persistForwardWorkPlanTenderSetup(forwardWorkPlanSetup);
  }

  protected ForwardWorkPlanTenderSetup persistForwardWorkPlanTenderSetup(
      ForwardWorkPlanTenderSetup forwardWorkPlanTenderSetup
  ) {
    return forwardWorkPlanTenderSetupRepository.save(forwardWorkPlanTenderSetup);
  }

  protected ForwardWorkPlanTenderSetupView getTenderSetupView(ProjectDetail projectDetail) {
    final var forwardWorkPlanTenderSetup = getForwardWorkPlanTenderSetupForDetail(projectDetail)
        .orElse(new ForwardWorkPlanTenderSetup());

    return ForwardWorkPlanTenderSetupViewUtil.from(forwardWorkPlanTenderSetup);
  }

  protected ForwardWorkPlanTenderSetupView getTenderSetupView(Project project, int version) {
    final var forwardWorkPlanTenderSetup = getForwardWorkPlanTenderSetupForProjectAndVersion(
        project,
        version
    ).orElse(new ForwardWorkPlanTenderSetup());

    return ForwardWorkPlanTenderSetupViewUtil.from(forwardWorkPlanTenderSetup);
  }

  private void populateForwardWorkPlanTenderSetupFromForm(ForwardWorkPlanTenderSetup entity,
                                                          ForwardWorkPlanTenderSetupForm form) {
    entity.setHasTendersToAdd(form.getHasTendersToAdd());
  }
}
