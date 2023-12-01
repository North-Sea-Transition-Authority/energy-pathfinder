package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import java.time.LocalDate;
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
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadFileWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.RemindableProject;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;

@Service
public class UpcomingTenderService implements ProjectFormSectionService {

  private final UpcomingTenderRepository upcomingTenderRepository;
  private final ValidationService validationService;
  private final UpcomingTenderFormValidator upcomingTenderFormValidator;
  private final FunctionService functionService;
  private final SearchSelectorService searchSelectorService;
  private final ProjectDetailFileService projectDetailFileService;
  private final UpcomingTenderFileLinkService upcomingTenderFileLinkService;
  private final ProjectSetupService projectSetupService;
  private final EntityDuplicationService entityDuplicationService;
  private final TeamService teamService;
  private final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Autowired
  public UpcomingTenderService(UpcomingTenderRepository upcomingTenderRepository,
                               ValidationService validationService,
                               UpcomingTenderFormValidator upcomingTenderFormValidator,
                               FunctionService functionService,
                               SearchSelectorService searchSelectorService,
                               ProjectDetailFileService projectDetailFileService,
                               UpcomingTenderFileLinkService upcomingTenderFileLinkService,
                               ProjectSetupService projectSetupService,
                               EntityDuplicationService entityDuplicationService,
                               TeamService teamService,
                               ProjectSectionItemOwnershipService projectSectionItemOwnershipService) {
    this.upcomingTenderRepository = upcomingTenderRepository;
    this.validationService = validationService;
    this.upcomingTenderFormValidator = upcomingTenderFormValidator;
    this.functionService = functionService;
    this.searchSelectorService = searchSelectorService;
    this.projectDetailFileService = projectDetailFileService;
    this.upcomingTenderFileLinkService = upcomingTenderFileLinkService;
    this.projectSetupService = projectSetupService;
    this.entityDuplicationService = entityDuplicationService;
    this.teamService = teamService;
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
  }

  @Transactional
  public UpcomingTender createUpcomingTender(ProjectDetail detail,
                                             UpcomingTenderForm form,
                                             AuthenticatedUserAccount userAccount) {
    var upcomingTender = new UpcomingTender(detail);
    setCommonFields(upcomingTender, form);
    var portalOrganisationGroup = teamService.getContributorPortalOrganisationGroup(userAccount);
    upcomingTender.setAddedByOrganisationGroup(portalOrganisationGroup.getOrgGrpId());
    upcomingTender = upcomingTenderRepository.save(upcomingTender);

    upcomingTenderFileLinkService.updateUpcomingTenderFileLinks(upcomingTender, form, userAccount);

    return upcomingTender;
  }

  @Transactional
  public UpcomingTender updateUpcomingTender(UpcomingTender upcomingTender,
                                             UpcomingTenderForm form,
                                             AuthenticatedUserAccount userAccount) {
    upcomingTenderFileLinkService.updateUpcomingTenderFileLinks(upcomingTender, form, userAccount);
    setCommonFields(upcomingTender, form);

    return upcomingTenderRepository.save(upcomingTender);
  }

  private void setCommonFields(UpcomingTender upcomingTender, UpcomingTenderForm form) {
    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        form.getTenderFunction(),
        Function.values(),
        upcomingTender::setManualTenderFunction,
        upcomingTender::setTenderFunction
    );

    upcomingTender.setDescriptionOfWork(form.getDescriptionOfWork());
    upcomingTender.setEstimatedTenderDate(form.getEstimatedTenderDate().createDateOrNull());
    upcomingTender.setContractBand(form.getContractBand());

    var contactDetailForm = form.getContactDetail();
    upcomingTender.setContactName(contactDetailForm.getName());
    upcomingTender.setPhoneNumber(contactDetailForm.getPhoneNumber());
    upcomingTender.setJobTitle(contactDetailForm.getJobTitle());
    upcomingTender.setEmailAddress(contactDetailForm.getEmailAddress());
  }

  @Transactional
  public void delete(UpcomingTender upcomingTender) {
    upcomingTenderFileLinkService.removeUpcomingTenderFileLinks(upcomingTender);
    upcomingTenderRepository.delete(upcomingTender);
  }

  public UpcomingTender getOrError(Integer upcomingTenderId, ProjectDetail projectDetail) {
    return upcomingTenderRepository.findByIdAndProjectDetail(upcomingTenderId, projectDetail).orElseThrow(() ->
        new PathfinderEntityNotFoundException(
            String.format("Unable to find tender with id: %s for ProjectDetail with id %s",
                upcomingTenderId,
                projectDetail != null ? projectDetail.getId() : null)
        )
    );
  }

  public boolean canCurrentUserAccessTender(UpcomingTender upcomingTender) {
    return projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        upcomingTender.getProjectDetail(),
        new OrganisationGroupIdWrapper(upcomingTender.getAddedByOrganisationGroup())
    );
  }

  public BindingResult validate(UpcomingTenderForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    upcomingTenderFormValidator.validate(form, bindingResult, new UpcomingTenderValidationHint(validationType));
    return validationService.validate(form, bindingResult, validationType);
  }

  public boolean isValid(UpcomingTender upcomingTender, ValidationType validationType) {
    var form = getForm(upcomingTender);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  public UpcomingTenderForm getForm(UpcomingTender upcomingTender) {
    var form = new UpcomingTenderForm();

    if (upcomingTender.getTenderFunction() != null) {
      form.setTenderFunction(upcomingTender.getTenderFunction().name());
    } else if (upcomingTender.getManualTenderFunction() != null) {
      form.setTenderFunction(SearchSelectorService.getValueWithManualEntryPrefix(upcomingTender.getManualTenderFunction()));
    }

    form.setEstimatedTenderDate(new ThreeFieldDateInput(upcomingTender.getEstimatedTenderDate()));
    form.setDescriptionOfWork(upcomingTender.getDescriptionOfWork());
    form.setContractBand(upcomingTender.getContractBand());
    form.setContactDetail(new ContactDetailForm(upcomingTender));
    form.setUploadedFileWithDescriptionForms(getUploadedFilesFormsByUpcomingTender(upcomingTender));

    return form;
  }

  /**
   * Remove an uploaded upcoming tender file.
   * @param fileId The file id to remove
   * @param projectDetail the project detail the file is linked to
   * @param webUserAccount the logged in user
   * @return a FileDeleteResult to indicate a success or failure of the removal
   */
  public FileDeleteResult deleteUpcomingTenderFile(String fileId,
                                                   ProjectDetail projectDetail,
                                                   WebUserAccount webUserAccount) {
    var file = projectDetailFileService.getProjectDetailFileByProjectDetailAndFileId(
        projectDetail,
        fileId
    );

    if (file.getFileLinkStatus().equals(FileLinkStatus.FULL)) {
      // if fully linked we need to remove the upcoming tender file link
      upcomingTenderFileLinkService.removeUpcomingTenderFileLink(file);
    }

    return projectDetailFileService.processFileDeletion(file, webUserAccount);
  }

  private List<UploadFileWithDescriptionForm> getUploadedFilesFormsByUpcomingTender(UpcomingTender upcomingTender) {
    return upcomingTenderFileLinkService.getAllByUpcomingTender(upcomingTender)
        .stream()
        .map(upcomingTenderFileLink -> {
          var uploadedFileView = projectDetailFileService.getUploadedFileView(
              upcomingTender.getProjectDetail(),
              upcomingTenderFileLink.getProjectDetailFile().getFileId(),
              UpcomingTenderFileLinkService.FILE_PURPOSE,
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

  public List<UpcomingTender> getUpcomingTendersForDetail(ProjectDetail detail) {
    return upcomingTenderRepository.findByProjectDetailOrderByIdAsc(detail);
  }

  public List<UpcomingTender> getUpcomingTendersForProjectVersion(Project project, Integer version) {
    return upcomingTenderRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version);
  }

  /**
   * If there's data in the form turn it back into a format the searchSelector can parse.
   * @param form valid or invalid UpcomingTenderForm
   * @return id and display name of the search selector items empty map if there's no form data.
   */
  public Map<String, String> getPreSelectedFunction(UpcomingTenderForm form) {
    return searchSelectorService.getPreSelectedSearchSelectorValue(form.getTenderFunction(), Function.values());
  }


  /**
   * Search the TenderFunction enum displayNames for those that include searchTerm.
   * @param searchTerm Term to match against TenderFunction display names
   * @return return matching results plus manual entry
   */
  public List<RestSearchItem> findTenderFunctionsLikeWithManualEntry(String searchTerm) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.UPCOMING_TENDER);
  }

  public List<UpcomingTender> getPastUpcomingTendersForRemindableProjects(List<RemindableProject> remindableProjects) {
    var projectDetailIds = remindableProjects
        .stream()
        .map(RemindableProject::getProjectDetailId)
        .collect(Collectors.toList());

    var pastUpcomingTenders =  upcomingTenderRepository.findAllByProjectDetail_IdIn(projectDetailIds);
    var currentDate = LocalDate.now();

    return pastUpcomingTenders
        .stream()
        .filter(ut -> ut.getEstimatedTenderDate().isBefore(currentDate))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    var upcomingTenders =  getUpcomingTendersForDetail(detail);
    return !upcomingTenders.isEmpty() && upcomingTenders.stream()
        .allMatch(ut -> isValid(ut, ValidationType.FULL));
  }

  @Override
  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.UPCOMING_TENDERS);
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail, Set<UserToProjectRelationship> userToProjectRelationships) {
    return isTaskValidForProjectDetail(detail)
        && UserToProjectRelationshipUtil.canAccessProjectTask(ProjectTask.UPCOMING_TENDERS, userToProjectRelationships);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    final var upcomingTenders = getUpcomingTendersForDetail(projectDetail);
    upcomingTenderFileLinkService.removeUpcomingTenderFileLinks(upcomingTenders);
    upcomingTenderRepository.deleteAll(upcomingTenders);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

    // duplicate and link the upcoming tender entities to the new ProjectDetail
    final var duplicatedUpcomingTenderEntities = entityDuplicationService.duplicateEntitiesAndSetNewParent(
        getUpcomingTendersForDetail(fromDetail),
        toDetail,
        UpcomingTender.class
    );

    final var duplicatedUpcomingTenderEntityMap = entityDuplicationService.createDuplicatedEntityPairingMap(
        duplicatedUpcomingTenderEntities
    );

    upcomingTenderFileLinkService.copyUpcomingTenderFileLinkData(
        fromDetail,
        toDetail,
        duplicatedUpcomingTenderEntityMap
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }
}
