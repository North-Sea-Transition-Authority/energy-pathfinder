package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadFileWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.CollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class CollaborationOpportunitiesService implements ProjectFormSectionService {


  private final SearchSelectorService searchSelectorService;
  private final FunctionService functionService;
  private final ValidationService validationService;
  private final CollaborationOpportunityFormValidator collaborationOpportunityFormValidator;
  private final CollaborationOpportunitiesRepository collaborationOpportunitiesRepository;
  private final CollaborationOpportunityFileLinkService collaborationOpportunityFileLinkService;
  private final ProjectDetailFileService projectDetailFileService;
  private final ProjectSetupService projectSetupService;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public CollaborationOpportunitiesService(SearchSelectorService searchSelectorService,
                                           FunctionService functionService,
                                           ValidationService validationService,
                                           CollaborationOpportunityFormValidator collaborationOpportunityFormValidator,
                                           CollaborationOpportunitiesRepository collaborationOpportunitiesRepository,
                                           CollaborationOpportunityFileLinkService collaborationOpportunityFileLinkService,
                                           ProjectDetailFileService projectDetailFileService,
                                           ProjectSetupService projectSetupService,
                                           EntityDuplicationService entityDuplicationService) {
    this.searchSelectorService = searchSelectorService;
    this.functionService = functionService;
    this.validationService = validationService;
    this.collaborationOpportunityFormValidator = collaborationOpportunityFormValidator;
    this.collaborationOpportunitiesRepository = collaborationOpportunitiesRepository;
    this.collaborationOpportunityFileLinkService = collaborationOpportunityFileLinkService;
    this.projectDetailFileService = projectDetailFileService;
    this.projectSetupService = projectSetupService;
    this.entityDuplicationService = entityDuplicationService;
  }


  public BindingResult validate(CollaborationOpportunityForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    var collaborationOpportunityValidationHint = new CollaborationOpportunityValidationHint();
    collaborationOpportunityFormValidator.validate(form, bindingResult, collaborationOpportunityValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  public boolean isValid(CollaborationOpportunity opportunity, ValidationType validationType) {
    var form = getForm(opportunity);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  @Transactional
  public CollaborationOpportunity createCollaborationOpportunity(ProjectDetail detail,
                                                                 CollaborationOpportunityForm form,
                                                                 AuthenticatedUserAccount authenticatedUserAccount) {
    var opportunity = new CollaborationOpportunity(detail);
    setCommonFields(opportunity, form);
    opportunity = collaborationOpportunitiesRepository.save(opportunity);

    collaborationOpportunityFileLinkService.updateCollaborationOpportunityFileLinks(
        opportunity,
        form,
        authenticatedUserAccount
    );

    return opportunity;
  }

  @Transactional
  public CollaborationOpportunity updateCollaborationOpportunity(CollaborationOpportunity opportunity,
                                                                 CollaborationOpportunityForm form,
                                                                 AuthenticatedUserAccount authenticatedUserAccount) {
    collaborationOpportunityFileLinkService.updateCollaborationOpportunityFileLinks(
        opportunity,
        form,
        authenticatedUserAccount
    );
    setCommonFields(opportunity, form);
    return collaborationOpportunitiesRepository.save(opportunity);
  }

  private void setCommonFields(CollaborationOpportunity opportunity, CollaborationOpportunityForm form) {
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
  public void delete(CollaborationOpportunity opportunity) {
    collaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(opportunity);
    collaborationOpportunitiesRepository.delete(opportunity);
  }

  public List<RestSearchItem> findFunctionsLikeWithManualEntry(String searchTerm) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.COLLABORATION_OPPORTUNITY);
  }

  public List<CollaborationOpportunity> getOpportunitiesForDetail(ProjectDetail detail) {
    return collaborationOpportunitiesRepository.findAllByProjectDetailOrderByIdAsc(detail);
  }

  public List<CollaborationOpportunity> getOpportunitiesForProjectVersion(Project project, Integer version) {
    return collaborationOpportunitiesRepository.findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version);
  }

  public Map<String, String> getPreSelectedCollaborationFunction(CollaborationOpportunityForm form) {
    return searchSelectorService.getPreSelectedSearchSelectorValue(form.getFunction(), Function.values());
  }

  public CollaborationOpportunity getOrError(Integer opportunityId) {
    return collaborationOpportunitiesRepository.findById(opportunityId)
        .orElseThrow(
            () -> new PathfinderEntityNotFoundException(
                String.format("Unable to find collaborationOpportunity with ID %d", opportunityId)
          )
        );
  }

  public CollaborationOpportunityForm getForm(CollaborationOpportunity opportunity) {
    var form = new CollaborationOpportunityForm();

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
      collaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLink(file);
    }

    return projectDetailFileService.processFileDeletion(file, webUserAccount);
  }

  private List<UploadFileWithDescriptionForm> getUploadedFilesFormsByCollaborationOpportunity(
      CollaborationOpportunity collaborationOpportunity
  ) {
    return collaborationOpportunityFileLinkService.getAllByCollaborationOpportunity(collaborationOpportunity)
        .stream()
        .map(collaborationOpportunityFileLink -> {
          var uploadedFileView = projectDetailFileService.getUploadedFileView(
              collaborationOpportunity.getProjectDetail(),
              collaborationOpportunityFileLink.getProjectDetailFile().getFileId(),
              CollaborationOpportunityFileLinkService.FILE_PURPOSE,
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
    collaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(collaborationOpportunities);
    collaborationOpportunitiesRepository.deleteAll(collaborationOpportunities);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

    final var duplicatedOpportunityEntities = entityDuplicationService.duplicateEntitiesAndSetNewParent(
        getOpportunitiesForDetail(fromDetail),
        toDetail,
        CollaborationOpportunity.class
    );

    final var duplicatedOpportunityEntityMap = entityDuplicationService.createDuplicatedEntityPairingMap(
        duplicatedOpportunityEntities
    );

    collaborationOpportunityFileLinkService.copyCollaborationOpportunityFileLinkData(
        fromDetail,
        toDetail,
        duplicatedOpportunityEntityMap
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }
}
