package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetupForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationSetupView;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationSetupViewUtil;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetupRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ForwardWorkPlanCollaborationSetupService {

  private final ForwardWorkPlanCollaborationSetupRepository forwardWorkPlanCollaborationSetupRepository;
  private final ValidationService validationService;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public ForwardWorkPlanCollaborationSetupService(
      ForwardWorkPlanCollaborationSetupRepository forwardWorkPlanCollaborationSetupRepository,
      ValidationService validationService,
      EntityDuplicationService entityDuplicationService
  ) {
    this.forwardWorkPlanCollaborationSetupRepository = forwardWorkPlanCollaborationSetupRepository;
    this.validationService = validationService;
    this.entityDuplicationService = entityDuplicationService;
  }

  public ForwardWorkPlanCollaborationSetupForm getCollaborationSetupFormFromDetail(ProjectDetail projectDetail) {

    final var form = new ForwardWorkPlanCollaborationSetupForm();

    getForwardWorkPlanCollaborationSetupForDetail(projectDetail)
        .ifPresent(setup -> form.setHasCollaborationsToAdd(setup.getHasCollaborationToAdd()));

    return form;
  }

  protected Optional<ForwardWorkPlanCollaborationSetup> getCollaborationSetupFromDetail(ProjectDetail projectDetail) {
    return forwardWorkPlanCollaborationSetupRepository.findByProjectDetail(projectDetail);
  }

  public BindingResult validate(ForwardWorkPlanCollaborationSetupForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    return validationService.validate(form, bindingResult, validationType);
  }

  @Transactional
  public ForwardWorkPlanCollaborationSetup saveForwardWorkPlanCollaborationSetup(ForwardWorkPlanCollaborationSetupForm form,
                                                                                 ProjectDetail projectDetail) {
    final var forwardWorkPlanCollaborationSetup = getForwardWorkPlanCollaborationSetupForDetail(projectDetail)
        .orElse(new ForwardWorkPlanCollaborationSetup(projectDetail));

    populateForwardWorkPlanCollaborationSetupFromForm(forwardWorkPlanCollaborationSetup, form);

    return persistForwardWorkPlanCollaborationSetup(forwardWorkPlanCollaborationSetup);
  }

  protected ForwardWorkPlanCollaborationSetup persistForwardWorkPlanCollaborationSetup(ForwardWorkPlanCollaborationSetup entity) {
    return forwardWorkPlanCollaborationSetupRepository.save(entity);
  }

  protected ForwardWorkPlanCollaborationSetupView getCollaborationSetupView(ProjectDetail projectDetail) {
    final var collaborationSetup = getForwardWorkPlanCollaborationSetupForDetail(projectDetail)
        .orElse(new ForwardWorkPlanCollaborationSetup());

    return convertToCollaborationSetupView(collaborationSetup);
  }

  protected ForwardWorkPlanCollaborationSetupView getCollaborationSetupView(Project project, int version) {
    final var collaborationSetup = getForwardWorkPlanCollaborationSetup(
        project,
        version
    )
        .orElse(new ForwardWorkPlanCollaborationSetup());

    return convertToCollaborationSetupView(collaborationSetup);
  }

  @Transactional
  public void removeSectionData(ProjectDetail projectDetail) {
    getCollaborationSetupFromDetail(projectDetail)
        .ifPresent(forwardWorkPlanCollaborationSetupRepository::delete);
  }

  @Transactional
  public void copySectionData(ProjectDetail fromProjectDetail, ProjectDetail toProjectDetail) {
    entityDuplicationService.duplicateEntityAndSetNewParent(
        getCollaborationSetup(fromProjectDetail),
        toProjectDetail,
        ForwardWorkPlanCollaborationSetup.class
    );
  }

  ForwardWorkPlanCollaborationSetup getCollaborationSetup(ProjectDetail projectDetail) {
    return getCollaborationSetupFromDetail(projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format(
                "Could not find ForwardWorkPlanCollaborationSetup for project detail with ID %d",
                projectDetail.getId()
            )
        ));
  }

  private ForwardWorkPlanCollaborationSetupView convertToCollaborationSetupView(ForwardWorkPlanCollaborationSetup collaborationSetup) {
    return ForwardWorkPlanCollaborationSetupViewUtil.from(collaborationSetup);
  }

  private Optional<ForwardWorkPlanCollaborationSetup> getForwardWorkPlanCollaborationSetupForDetail(ProjectDetail projectDetail) {
    return forwardWorkPlanCollaborationSetupRepository.findByProjectDetail(projectDetail);
  }

  private Optional<ForwardWorkPlanCollaborationSetup> getForwardWorkPlanCollaborationSetup(Project project,
                                                                                           int version) {
    return forwardWorkPlanCollaborationSetupRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        project,
        version
    );
  }

  private void populateForwardWorkPlanCollaborationSetupFromForm(ForwardWorkPlanCollaborationSetup entity,
                                                                 ForwardWorkPlanCollaborationSetupForm form) {
    entity.setHasCollaborationToAdd(form.getHasCollaborationsToAdd());
  }
}
