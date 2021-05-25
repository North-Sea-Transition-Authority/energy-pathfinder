package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.config.file.FileDeleteResult;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadFileWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class InfrastructureCollaborationOpportunitiesService implements ProjectFormSectionService {


  private final SearchSelectorService searchSelectorService;
  private final FunctionService functionService;
  private final ValidationService validationService;
  private final InfrastructureCollaborationOpportunityFormValidator infrastructureCollaborationOpportunityFormValidator;
  private final InfrastructureCollaborationOpportunitiesRepository infrastructureCollaborationOpportunitiesRepository;
  private final InfrastructureCollaborationOpportunityFileLinkService infrastructureCollaborationOpportunityFileLinkService;
  private final ProjectDetailFileService projectDetailFileService;
  private final ProjectSetupService projectSetupService;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public InfrastructureCollaborationOpportunitiesService(
      SearchSelectorService searchSelectorService,
      FunctionService functionService,
      ValidationService validationService,
      InfrastructureCollaborationOpportunityFormValidator infrastructureCollaborationOpportunityFormValidator,
      InfrastructureCollaborationOpportunitiesRepository infrastructureCollaborationOpportunitiesRepository,
      InfrastructureCollaborationOpportunityFileLinkService infrastructureCollaborationOpportunityFileLinkService,
      ProjectDetailFileService projectDetailFileService,
      ProjectSetupService projectSetupService,
      EntityDuplicationService entityDuplicationService
  ) {
    this.searchSelectorService = searchSelectorService;
    this.functionService = functionService;
    this.validationService = validationService;
    this.infrastructureCollaborationOpportunityFormValidator = infrastructureCollaborationOpportunityFormValidator;
    this.infrastructureCollaborationOpportunitiesRepository = infrastructureCollaborationOpportunitiesRepository;
    this.infrastructureCollaborationOpportunityFileLinkService = infrastructureCollaborationOpportunityFileLinkService;
    this.projectDetailFileService = projectDetailFileService;
    this.projectSetupService = projectSetupService;
    this.entityDuplicationService = entityDuplicationService;
  }


  public BindingResult validate(InfrastructureCollaborationOpportunityForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    var collaborationOpportunityValidationHint = new InfrastructureCollaborationOpportunityValidationHint();
    infrastructureCollaborationOpportunityFormValidator.validate(form, bindingResult, collaborationOpportunityValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  public boolean isValid(InfrastructureCollaborationOpportunity opportunity, ValidationType validationType) {
    var form = getForm(opportunity);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  @Transactional
  public InfrastructureCollaborationOpportunity createCollaborationOpportunity(
      ProjectDetail detail,
      InfrastructureCollaborationOpportunityForm form,
      AuthenticatedUserAccount authenticatedUserAccount
  ) {
    var opportunity = new InfrastructureCollaborationOpportunity(detail);
    setCommonFields(opportunity, form);
    opportunity = infrastructureCollaborationOpportunitiesRepository.save(opportunity);

    infrastructureCollaborationOpportunityFileLinkService.updateCollaborationOpportunityFileLinks(
        opportunity,
        form,
        authenticatedUserAccount
    );

    return opportunity;
  }

  @Transactional
  public InfrastructureCollaborationOpportunity updateCollaborationOpportunity(
      InfrastructureCollaborationOpportunity opportunity,
      InfrastructureCollaborationOpportunityForm form,
      AuthenticatedUserAccount authenticatedUserAccount
  ) {
    infrastructureCollaborationOpportunityFileLinkService.updateCollaborationOpportunityFileLinks(
        opportunity,
        form,
        authenticatedUserAccount
    );
    setCommonFields(opportunity, form);
    return infrastructureCollaborationOpportunitiesRepository.save(opportunity);
  }

  private void setCommonFields(InfrastructureCollaborationOpportunity opportunity,
                               InfrastructureCollaborationOpportunityForm form) {
    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        form.getFunction(),
        Function.values(),
        opportunity::setManualFunction,
        opportunity::setFunction
    );

    opportunity.setDescriptionOfWork(form.getDescriptionOfWork());
    opportunity.setUrgentResponseNeeded(form.getUrgentResponseNeeded());

    var contactDetailForm = form.getContactDetail();
    opportunity.setContactName(contactDetailForm.getName());
    opportunity.setPhoneNumber(contactDetailForm.getPhoneNumber());
    opportunity.setJobTitle(contactDetailForm.getJobTitle());
    opportunity.setEmailAddress(contactDetailForm.getEmailAddress());
  }

  @Transactional
  public void delete(InfrastructureCollaborationOpportunity opportunity) {
    infrastructureCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(opportunity);
    infrastructureCollaborationOpportunitiesRepository.delete(opportunity);
  }

  public List<RestSearchItem> findFunctionsLikeWithManualEntry(String searchTerm) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.COLLABORATION_OPPORTUNITY);
  }

  public List<InfrastructureCollaborationOpportunity> getOpportunitiesForDetail(ProjectDetail detail) {
    return infrastructureCollaborationOpportunitiesRepository.findAllByProjectDetailOrderByIdAsc(detail);
  }

  public List<InfrastructureCollaborationOpportunity> getOpportunitiesForProjectVersion(Project project, Integer version) {
    return infrastructureCollaborationOpportunitiesRepository.findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        project,
        version
    );
  }

  public Map<String, String> getPreSelectedCollaborationFunction(InfrastructureCollaborationOpportunityForm form) {
    return searchSelectorService.getPreSelectedSearchSelectorValue(form.getFunction(), Function.values());
  }

  public InfrastructureCollaborationOpportunity getOrError(Integer opportunityId) {
    return infrastructureCollaborationOpportunitiesRepository.findById(opportunityId)
        .orElseThrow(
            () -> new PathfinderEntityNotFoundException(
                String.format("Unable to find collaborationOpportunity with ID %d", opportunityId)
          )
        );
  }

  public InfrastructureCollaborationOpportunityForm getForm(InfrastructureCollaborationOpportunity opportunity) {
    var form = new InfrastructureCollaborationOpportunityForm();

    if (opportunity.getFunction() != null) {
      form.setFunction(opportunity.getFunction().name());
    } else if (opportunity.getManualFunction() != null) {
      form.setFunction(SearchSelectorService.getValueWithManualEntryPrefix(opportunity.getManualFunction()));
    }

    form.setUrgentResponseNeeded(opportunity.getUrgentResponseNeeded());
    form.setDescriptionOfWork(opportunity.getDescriptionOfWork());
    form.setContactDetail(new ContactDetailForm(opportunity));
    form.setUploadedFileWithDescriptionForms(getUploadedFilesFormsByCollaborationOpportunity(opportunity));

    return form;
  }

  /**
   * Remove an uploaded collaboration opportunity file.
   * @param fileId The file id to remove
   * @param projectDetail the project detail the file is linked to
   * @param webUserAccount the logged in user
   * @return a FileDeleteResult to indicate a success or failure of the removal
   */
  public FileDeleteResult deleteCollaborationOpportunityFile(String fileId,
                                                             ProjectDetail projectDetail,
                                                             WebUserAccount webUserAccount) {
    var file = projectDetailFileService.getProjectDetailFileByProjectDetailAndFileId(
        projectDetail,
        fileId
    );

    if (file.getFileLinkStatus().equals(FileLinkStatus.FULL)) {
      // if fully linked we need to remove the collaboration opportunity file link
      infrastructureCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLink(file);
    }

    return projectDetailFileService.processFileDeletion(file, webUserAccount);
  }

  private List<UploadFileWithDescriptionForm> getUploadedFilesFormsByCollaborationOpportunity(
      InfrastructureCollaborationOpportunity infrastructureCollaborationOpportunity
  ) {
    return infrastructureCollaborationOpportunityFileLinkService.getAllByCollaborationOpportunity(
        infrastructureCollaborationOpportunity)
        .stream()
        .map(collaborationOpportunityFileLink -> {
          var uploadedFileView = projectDetailFileService.getUploadedFileView(
              infrastructureCollaborationOpportunity.getProjectDetail(),
              collaborationOpportunityFileLink.getProjectDetailFile().getFileId(),
              InfrastructureCollaborationOpportunityFileLinkService.FILE_PURPOSE,
              FileLinkStatus.FULL
          );
          return new UploadFileWithDescriptionForm(
              uploadedFileView.getFileId(),
              uploadedFileView.getFileDescription(),
              uploadedFileView.getFileUploadedTime()
          );
        })
        .collect(Collectors.toList());
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    var opportunities =  getOpportunitiesForDetail(detail);
    return !opportunities.isEmpty() && opportunities.stream()
        .allMatch(ut -> isValid(ut, ValidationType.FULL));
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.COLLABORATION_OPPORTUNITIES);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    final var collaborationOpportunities = getOpportunitiesForDetail(projectDetail);
    infrastructureCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(collaborationOpportunities);
    infrastructureCollaborationOpportunitiesRepository.deleteAll(collaborationOpportunities);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

    final var duplicatedOpportunityEntities = entityDuplicationService.duplicateEntitiesAndSetNewParent(
        getOpportunitiesForDetail(fromDetail),
        toDetail,
        InfrastructureCollaborationOpportunity.class
    );

    final var duplicatedOpportunityEntityMap = entityDuplicationService.createDuplicatedEntityPairingMap(
        duplicatedOpportunityEntities
    );

    infrastructureCollaborationOpportunityFileLinkService.copyCollaborationOpportunityFileLinkData(
        fromDetail,
        toDetail,
        duplicatedOpportunityEntityMap
    );
  }
}
