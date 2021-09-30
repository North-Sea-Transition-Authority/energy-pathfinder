package uk.co.ogauthority.pathfinder.service.projecttransfer;

import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projecttransfer.ProjectTransfer;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferForm;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferFormValidator;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferValidationHint;
import uk.co.ogauthority.pathfinder.repository.projecttransfer.ProjectTransferRepository;
import uk.co.ogauthority.pathfinder.service.email.OperatorEmailService;
import uk.co.ogauthority.pathfinder.service.project.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ProjectTransferService {

  private final ProjectTransferRepository projectTransferRepository;
  private final ProjectOperatorService projectOperatorService;
  private final ProjectUpdateService projectUpdateService;
  private final CancelDraftProjectVersionService cancelDraftProjectVersionService;
  private final ValidationService validationService;
  private final ProjectTransferFormValidator projectTransferFormValidator;
  private final OperatorEmailService operatorEmailService;

  @Autowired
  public ProjectTransferService(
      ProjectTransferRepository projectTransferRepository,
      ProjectOperatorService projectOperatorService,
      ProjectUpdateService projectUpdateService,
      CancelDraftProjectVersionService cancelDraftProjectVersionService,
      ValidationService validationService,
      ProjectTransferFormValidator projectTransferFormValidator,
      OperatorEmailService operatorEmailService
  ) {
    this.projectTransferRepository = projectTransferRepository;
    this.projectOperatorService = projectOperatorService;
    this.projectUpdateService = projectUpdateService;
    this.cancelDraftProjectVersionService = cancelDraftProjectVersionService;
    this.validationService = validationService;
    this.projectTransferFormValidator = projectTransferFormValidator;
    this.operatorEmailService = operatorEmailService;
  }

  @Transactional
  public ProjectTransfer transferProject(ProjectDetail latestSubmittedProjectDetail,
                                         AuthenticatedUserAccount user,
                                         ProjectTransferForm form) {
    cancelDraftProjectVersionService.cancelDraftIfExists(latestSubmittedProjectDetail.getProject().getId());

    var newProjectDetail = projectUpdateService.createNewProjectVersion(latestSubmittedProjectDetail, user);

    final var projectOperatorForm = new ProjectOperatorForm();
    projectOperatorForm.setOperator(form.getNewOrganisationGroup());
    projectOperatorForm.setIsPublishedAsOperator(form.isPublishedAsOperator());
    projectOperatorForm.setPublishableOrganisation(form.getPublishableOrganisation());

    final var projectOperator = projectOperatorService.createOrUpdateProjectOperator(
        newProjectDetail,
        projectOperatorForm
    );

    var fromOrganisationGroup = projectOperatorService.getProjectOperatorByProjectDetailOrError(latestSubmittedProjectDetail)
        .getOrganisationGroup();
    final var toOrganisationGroup = projectOperator.getOrganisationGroup();

    var projectTransfer = new ProjectTransfer();
    projectTransfer.setProjectDetail(newProjectDetail);
    projectTransfer.setFromOrganisationGroup(fromOrganisationGroup);
    projectTransfer.setToOrganisationGroup(toOrganisationGroup);
    projectTransfer.setIsPublishedAsOperator(projectOperator.isPublishedAsOperator());
    projectTransfer.setPublishableOrganisationUnit(projectOperator.getPublishableOrganisationUnit());
    projectTransfer.setTransferReason(form.getTransferReason());
    projectTransfer.setTransferredInstant(Instant.now());
    projectTransfer.setTransferredByWuaId(user.getWuaId());
    projectTransfer = projectTransferRepository.save(projectTransfer);

    operatorEmailService.sendProjectTransferEmails(
        newProjectDetail,
        fromOrganisationGroup,
        toOrganisationGroup,
        form.getTransferReason()
    );

    return projectTransfer;
  }

  public Optional<ProjectTransfer> getProjectTransfer(ProjectDetail projectDetail) {
    return projectTransferRepository.findByProjectDetail(projectDetail);
  }

  public BindingResult validate(ProjectTransferForm form, BindingResult bindingResult, ProjectDetail projectDetail) {
    var organisationGroup = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)
        .getOrganisationGroup();
    projectTransferFormValidator.validate(form, bindingResult, new ProjectTransferValidationHint(organisationGroup));
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }
}
