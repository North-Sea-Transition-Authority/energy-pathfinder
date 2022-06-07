package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityCommon;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityFileLinkCommon;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadFileWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityFormCommon;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Service
public abstract class CollaborationOpportunitiesService {

  private final SearchSelectorService searchSelectorService;
  private final FunctionService functionService;
  private final ProjectSetupService projectSetupService;
  private final ProjectDetailFileService projectDetailFileService;
  private final TeamService teamService;

  @Autowired
  public CollaborationOpportunitiesService(SearchSelectorService searchSelectorService,
                                           FunctionService functionService,
                                           ProjectSetupService projectSetupService,
                                           ProjectDetailFileService projectDetailFileService,
                                           TeamService teamService) {
    this.searchSelectorService = searchSelectorService;
    this.functionService = functionService;
    this.projectSetupService = projectSetupService;
    this.projectDetailFileService = projectDetailFileService;
    this.teamService = teamService;
  }

  /**
   * Method to return the collaboration opportunity entities associated with the provided project detail.
   * @param projectDetail The project detail to retrieve the collaboration opportunities for
   * @return the collaboration opportunity entities associated with the provided project detail
   */
  public abstract List<? extends CollaborationOpportunityCommon> getOpportunitiesForDetail(ProjectDetail projectDetail);

  /**
   * Get a populated form based on the provided collaboration opportunity entity.
   * @param entity The entity to populate the form from
   * @param <E> The specific implementation of CollaborationOpportunityCommon
   * @return a populated form based on the provided collaboration opportunity entity
   */
  public abstract <E extends CollaborationOpportunityCommon> CollaborationOpportunityFormCommon getForm(E entity);

  public abstract <F extends CollaborationOpportunityFormCommon> BindingResult validate(F form,
                                                                                        BindingResult bindingResult,
                                                                                        ValidationType validationType);

  /**
   * Determines if a collaboration opportunity entity is valid.
   * @param entity The entity to check if valid
   * @param validationType The validation type to use in the valid check
   * @return true is the provided entity is valid, false otherwise
   */
  protected boolean isValid(CollaborationOpportunityCommon entity, ValidationType validationType) {
    final var form = getForm(entity);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  /**
   * Method to populate a collaboration opportunity entity based on the provided form.
   * @param sourceForm the form to populate the entity from
   * @param entityToPopulate the entity to populate
   * @return a populated entity based on the provided form
   */
  protected CollaborationOpportunityCommon populateCollaborationOpportunity(
      CollaborationOpportunityFormCommon sourceForm,
      CollaborationOpportunityCommon entityToPopulate
  ) {
    setCommonEntityFields(sourceForm, entityToPopulate);
    return entityToPopulate;
  }

  /**
   * Finds a list of functions with a type of functionType which match the provided searchTerm.
   * @param searchTerm the term to search for
   * @param functionType the function type to restrict by
   * @return a list of matching functions based on the searchTerm and functionType
   */
  protected List<RestSearchItem> findFunctionsLikeWithManualEntry(String searchTerm, FunctionType functionType) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, functionType);
  }

  /**
   * Method to return the function selected in the form for pre-population of this value by the front end.
   * @param form the form being viewed
   * @param functionValues the list of possible functions to select from
   * @return a map containing the selected function for display on the front end
   */
  protected Map<String, String> getPreSelectedCollaborationFunction(CollaborationOpportunityFormCommon form,
                                                                    Function[] functionValues) {
    return searchSelectorService.getPreSelectedSearchSelectorValue(form.getFunction(), functionValues);
  }

  /**
   * Method to populate a form from the given entity.
   * @param sourceEntity The entity to populate the form data from
   * @param formToPopulate The form to populate
   * @param collaborationOpportunityFileLinks The files associated with the entity
   * @param projectDetailFilePurpose The file upload purpose for the attached files
   * @return a populated form based on the entity and files provided
   */
  protected CollaborationOpportunityFormCommon populateCollaborationOpportunityForm(
      CollaborationOpportunityCommon sourceEntity,
      CollaborationOpportunityFormCommon formToPopulate,
      List<? extends CollaborationOpportunityFileLinkCommon> collaborationOpportunityFileLinks,
      ProjectDetailFilePurpose projectDetailFilePurpose
  ) {
    if (sourceEntity.getFunction() != null) {
      formToPopulate.setFunction(sourceEntity.getFunction().name());
    } else if (sourceEntity.getManualFunction() != null) {
      formToPopulate.setFunction(SearchSelectorService.getValueWithManualEntryPrefix(sourceEntity.getManualFunction()));
    }

    formToPopulate.setUrgentResponseNeeded(sourceEntity.getUrgentResponseNeeded());
    formToPopulate.setDescriptionOfWork(sourceEntity.getDescriptionOfWork());
    formToPopulate.setContactDetail(new ContactDetailForm(sourceEntity));
    formToPopulate.setUploadedFileWithDescriptionForms(getUploadedFilesFormsByCollaborationOpportunity(
        collaborationOpportunityFileLinks,
        projectDetailFilePurpose
    ));

    return formToPopulate;
  }

  /**
   * Determines if the section is complete (e.g. doesn't contain any validation errors).
   * @param projectDetail the project detail we are processing
   * @return true if the section is complete, false otherwise
   */
  protected boolean isComplete(ProjectDetail projectDetail) {

    final var collaborationOpportunities = getOpportunitiesForDetail(projectDetail);

    return !collaborationOpportunities.isEmpty() && collaborationOpportunities
        .stream()
        .allMatch(opportunity -> isValid(opportunity, ValidationType.FULL));
  }

  /**
   * Determines if the task should be shown in the task list.
   * @param projectDetail the project detail we are processing
   * @param projectTask the project task to check
   * @return true if task can be shown in task list, false otherwise
   */
  protected boolean canShowInTaskList(ProjectDetail projectDetail, ProjectTask projectTask) {
    return projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, projectTask);
  }

  private void setCommonEntityFields(CollaborationOpportunityFormCommon sourceForm,
                                     CollaborationOpportunityCommon destinationEntity) {
    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        sourceForm.getFunction(),
        Function.values(),
        destinationEntity::setManualFunction,
        destinationEntity::setFunction
    );

    destinationEntity.setDescriptionOfWork(sourceForm.getDescriptionOfWork());
    destinationEntity.setUrgentResponseNeeded(sourceForm.getUrgentResponseNeeded());

    final var contactDetailForm = sourceForm.getContactDetail();
    destinationEntity.setContactName(contactDetailForm.getName());
    destinationEntity.setPhoneNumber(contactDetailForm.getPhoneNumber());
    destinationEntity.setJobTitle(contactDetailForm.getJobTitle());
    destinationEntity.setEmailAddress(contactDetailForm.getEmailAddress());
  }

  private List<UploadFileWithDescriptionForm> getUploadedFilesFormsByCollaborationOpportunity(
      List<? extends CollaborationOpportunityFileLinkCommon> collaborationOpportunityFileLinks,
      ProjectDetailFilePurpose projectDetailFilePurpose
  ) {
    return collaborationOpportunityFileLinks
        .stream()
        .map(collaborationOpportunityFileLink -> {

          final var projectDetailFile = collaborationOpportunityFileLink.getProjectDetailFile();

          final var uploadedFileView = projectDetailFileService.getUploadedFileView(
              projectDetailFile.getProjectDetail(),
              projectDetailFile.getFileId(),
              projectDetailFilePurpose,
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

  protected void setAddedByOrganisationGroup(CollaborationOpportunityCommon opportunity,
                                             AuthenticatedUserAccount userAccount) {
    PortalOrganisationGroup portalOrganisationGroup = teamService.getContributorPortalOrganisationGroup(userAccount);
    opportunity.setAddedByOrganisationGroup(portalOrganisationGroup.getOrgGrpId());
  }
}
