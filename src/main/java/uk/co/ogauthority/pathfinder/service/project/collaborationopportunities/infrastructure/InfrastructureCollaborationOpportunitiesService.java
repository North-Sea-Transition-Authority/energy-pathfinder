package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.config.file.FileDeleteResult;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityCommon;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityFormCommon;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class InfrastructureCollaborationOpportunitiesService
    extends CollaborationOpportunitiesService implements ProjectFormSectionService {

  private final ValidationService validationService;
  private final InfrastructureCollaborationOpportunityFormValidator infrastructureCollaborationOpportunityFormValidator;
  private final InfrastructureCollaborationOpportunitiesRepository infrastructureCollaborationOpportunitiesRepository;
  private final InfrastructureCollaborationOpportunityFileLinkService infrastructureCollaborationOpportunityFileLinkService;
  private final ProjectDetailFileService projectDetailFileService;
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
    super(
        searchSelectorService,
        functionService,
        projectSetupService,
        projectDetailFileService
    );
    this.validationService = validationService;
    this.infrastructureCollaborationOpportunityFormValidator = infrastructureCollaborationOpportunityFormValidator;
    this.infrastructureCollaborationOpportunitiesRepository = infrastructureCollaborationOpportunitiesRepository;
    this.infrastructureCollaborationOpportunityFileLinkService = infrastructureCollaborationOpportunityFileLinkService;
    this.projectDetailFileService = projectDetailFileService;
    this.entityDuplicationService = entityDuplicationService;
  }

  @Override
  public <F extends CollaborationOpportunityFormCommon> BindingResult validate(F form,
                                                                               BindingResult bindingResult,
                                                                               ValidationType validationType) {
    var collaborationOpportunityValidationHint = new InfrastructureCollaborationOpportunityValidationHint();
    infrastructureCollaborationOpportunityFormValidator.validate(
        form,
        bindingResult,
        collaborationOpportunityValidationHint
    );
    return validationService.validate(form, bindingResult, validationType);
  }

  public boolean isValid(InfrastructureCollaborationOpportunity opportunity, ValidationType validationType) {
    return super.isValid(opportunity, validationType);
  }

  @Transactional
  public InfrastructureCollaborationOpportunity createCollaborationOpportunity(
      ProjectDetail detail,
      InfrastructureCollaborationOpportunityForm form,
      AuthenticatedUserAccount authenticatedUserAccount
  ) {
    var opportunity = new InfrastructureCollaborationOpportunity(detail);

    super.populateCollaborationOpportunity(
        form,
        opportunity
    );

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
    super.populateCollaborationOpportunity(
        form,
        opportunity
    );
    return infrastructureCollaborationOpportunitiesRepository.save(opportunity);
  }

  @Transactional
  public void delete(InfrastructureCollaborationOpportunity opportunity) {
    infrastructureCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(opportunity);
    infrastructureCollaborationOpportunitiesRepository.delete(opportunity);
  }

  public List<RestSearchItem> findFunctionsLikeWithManualEntry(String searchTerm) {
    return super.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.COLLABORATION_OPPORTUNITY);
  }

  public List<InfrastructureCollaborationOpportunity> getOpportunitiesForProjectVersion(Project project, Integer version) {
    return infrastructureCollaborationOpportunitiesRepository.findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        project,
        version
    );
  }

  public Map<String, String> getPreSelectedCollaborationFunction(InfrastructureCollaborationOpportunityForm form) {
    return super.getPreSelectedCollaborationFunction(form, Function.values());
  }

  public InfrastructureCollaborationOpportunity getOrError(Integer opportunityId) {
    return infrastructureCollaborationOpportunitiesRepository.findById(opportunityId)
        .orElseThrow(
            () -> new PathfinderEntityNotFoundException(
                String.format("Unable to find InfrastructureCollaborationOpportunity with ID %d", opportunityId)
          )
        );
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

  @Override
  public List<InfrastructureCollaborationOpportunity> getOpportunitiesForDetail(ProjectDetail detail) {
    return infrastructureCollaborationOpportunitiesRepository.findAllByProjectDetailOrderByIdAsc(detail);
  }

  @Override
  public <E extends CollaborationOpportunityCommon> InfrastructureCollaborationOpportunityForm getForm(E opportunity) {

    final var form = new InfrastructureCollaborationOpportunityForm();
    final var fileLinks = infrastructureCollaborationOpportunityFileLinkService.getAllByCollaborationOpportunity(
        (InfrastructureCollaborationOpportunity) opportunity
    );

    return (InfrastructureCollaborationOpportunityForm) super.populateCollaborationOpportunityForm(
        opportunity,
        form,
        fileLinks,
        InfrastructureCollaborationOpportunityFileLinkService.FILE_PURPOSE
    );
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    return super.isComplete(detail);
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return super.canShowInTaskList(detail, ProjectTask.COLLABORATION_OPPORTUNITIES);
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
