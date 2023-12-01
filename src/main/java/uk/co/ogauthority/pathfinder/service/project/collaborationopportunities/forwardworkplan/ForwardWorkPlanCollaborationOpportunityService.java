package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityCommon;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityFormCommon;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;

@Service
public class ForwardWorkPlanCollaborationOpportunityService
    extends CollaborationOpportunitiesService implements ProjectFormSectionService {

  private final ForwardWorkPlanCollaborationOpportunityRepository forwardWorkPlanCollaborationOpportunityRepository;
  private final ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService;
  private final ForwardWorkPlanCollaborationOpportunityFormValidator forwardWorkPlanCollaborationOpportunityFormValidator;
  private final ForwardWorkPlanCollaborationSetupService forwardWorkPlanCollaborationSetupService;
  private final ValidationService validationService;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public ForwardWorkPlanCollaborationOpportunityService(
      SearchSelectorService searchSelectorService,
      FunctionService functionService,
      ProjectSetupService projectSetupService,
      ProjectDetailFileService projectDetailFileService,
      ForwardWorkPlanCollaborationOpportunityRepository forwardWorkPlanCollaborationOpportunityRepository,
      ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService,
      ForwardWorkPlanCollaborationOpportunityFormValidator forwardWorkPlanCollaborationOpportunityFormValidator,
      ForwardWorkPlanCollaborationSetupService forwardWorkPlanCollaborationSetupService,
      ValidationService validationService,
      EntityDuplicationService entityDuplicationService,
      TeamService teamService) {
    super(
        searchSelectorService,
        functionService,
        projectSetupService,
        projectDetailFileService,
        teamService);
    this.forwardWorkPlanCollaborationOpportunityRepository = forwardWorkPlanCollaborationOpportunityRepository;
    this.forwardWorkPlanCollaborationOpportunityFileLinkService = forwardWorkPlanCollaborationOpportunityFileLinkService;
    this.forwardWorkPlanCollaborationOpportunityFormValidator = forwardWorkPlanCollaborationOpportunityFormValidator;
    this.forwardWorkPlanCollaborationSetupService = forwardWorkPlanCollaborationSetupService;
    this.validationService = validationService;
    this.entityDuplicationService = entityDuplicationService;
  }

  public List<RestSearchItem> findFunctionsLikeWithManualEntry(String searchTerm) {
    return super.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.WORK_PLAN_COLLABORATION_OPPORTUNITY);
  }

  public Map<String, String> getPreSelectedCollaborationFunction(ForwardWorkPlanCollaborationOpportunityForm form) {
    return super.getPreSelectedCollaborationFunction(form, Function.values());
  }

  public boolean isValid(ForwardWorkPlanCollaborationOpportunity opportunity, ValidationType validationType) {
    return super.isValid(opportunity, validationType);
  }

  public List<ForwardWorkPlanCollaborationOpportunity> getOpportunitiesForProjectVersion(Project project, Integer version) {
    return forwardWorkPlanCollaborationOpportunityRepository.findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        project,
        version
    );
  }

  @Transactional
  public ForwardWorkPlanCollaborationOpportunity createCollaborationOpportunity(
      ProjectDetail detail,
      ForwardWorkPlanCollaborationOpportunityForm form,
      AuthenticatedUserAccount authenticatedUserAccount
  ) {
    var opportunity = new ForwardWorkPlanCollaborationOpportunity(detail);

    super.populateCollaborationOpportunity(
        form,
        opportunity
    );
    setAddedByOrganisationGroup(opportunity, authenticatedUserAccount);

    opportunity = forwardWorkPlanCollaborationOpportunityRepository.save(opportunity);

    forwardWorkPlanCollaborationOpportunityFileLinkService.updateCollaborationOpportunityFileLinks(
        opportunity,
        form,
        authenticatedUserAccount
    );

    return opportunity;
  }

  @Transactional
  public ForwardWorkPlanCollaborationOpportunity updateCollaborationOpportunity(
      ForwardWorkPlanCollaborationOpportunity opportunity,
      ForwardWorkPlanCollaborationOpportunityForm form,
      AuthenticatedUserAccount authenticatedUserAccount
  ) {
    forwardWorkPlanCollaborationOpportunityFileLinkService.updateCollaborationOpportunityFileLinks(
        opportunity,
        form,
        authenticatedUserAccount
    );
    super.populateCollaborationOpportunity(
        form,
        opportunity
    );
    return forwardWorkPlanCollaborationOpportunityRepository.save(opportunity);
  }

  @Transactional
  public void delete(ForwardWorkPlanCollaborationOpportunity opportunity) {
    forwardWorkPlanCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(opportunity);
    forwardWorkPlanCollaborationOpportunityRepository.delete(opportunity);
  }

  public ForwardWorkPlanCollaborationOpportunity getOrError(Integer opportunityId, ProjectDetail projectDetail) {
    return forwardWorkPlanCollaborationOpportunityRepository.findByIdAndProjectDetail(opportunityId, projectDetail)
        .orElseThrow(
            () -> new PathfinderEntityNotFoundException(
                String.format("Unable to find ForwardWorkPlanCollaborationOpportunity with ID %d", opportunityId)
            )
        );
  }

  @Override
  public List<ForwardWorkPlanCollaborationOpportunity> getOpportunitiesForDetail(ProjectDetail projectDetail) {
    return forwardWorkPlanCollaborationOpportunityRepository.findAllByProjectDetailOrderByIdAsc(projectDetail);
  }

  @Override
  public <E extends CollaborationOpportunityCommon> CollaborationOpportunityFormCommon getForm(E entity) {

    final var form = new ForwardWorkPlanCollaborationOpportunityForm();
    final var fileLinks = forwardWorkPlanCollaborationOpportunityFileLinkService.getAllByCollaborationOpportunity(
        (ForwardWorkPlanCollaborationOpportunity) entity
    );

    return super.populateCollaborationOpportunityForm(
        entity,
        form,
        fileLinks,
        ForwardWorkPlanCollaborationOpportunityFileLinkService.FILE_PURPOSE
    );
  }

  @Override
  public <F extends CollaborationOpportunityFormCommon> BindingResult validate(F form, BindingResult bindingResult,
                                                                               ValidationType validationType) {
    var collaborationOpportunityValidationHint = new ForwardWorkPlanCollaborationOpportunityValidationHint();
    forwardWorkPlanCollaborationOpportunityFormValidator.validate(
        form,
        bindingResult,
        collaborationOpportunityValidationHint
    );
    return validationService.validate(form, bindingResult, validationType);
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {

    final var forwardWorkPlanCollaborationSetup = forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(detail)
        .orElse(new ForwardWorkPlanCollaborationSetup());

    if (forwardWorkPlanCollaborationSetup.getHasCollaborationToAdd() == null) {
      return false;
    } else if (BooleanUtils.isTrue(forwardWorkPlanCollaborationSetup.getHasCollaborationToAdd())) {
      return forwardWorkPlanCollaborationSetup.getHasOtherCollaborationToAdd() != null
          && !hasOtherCollaborationsToAdd(forwardWorkPlanCollaborationSetup)
          && areAllAddedCollaborationsValid(detail);
    } else {
      // indicated that no collaboration are to be added
      return true;
    }
  }

  @Override
  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return ProjectService.isForwardWorkPlanProject(detail);
  }

  private boolean hasOtherCollaborationsToAdd(ForwardWorkPlanCollaborationSetup forwardWorkPlanCollaborationSetup) {
    return BooleanUtils.isTrue(forwardWorkPlanCollaborationSetup.getHasOtherCollaborationToAdd());
  }

  private boolean areAllAddedCollaborationsValid(ProjectDetail projectDetail) {
    final var collaborations = getOpportunitiesForDetail(projectDetail);
    return !collaborations.isEmpty() && collaborations
        .stream()
        .allMatch(collaborationOpportunity -> isValid(collaborationOpportunity, ValidationType.FULL));
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail, Set<UserToProjectRelationship> userToProjectRelationships) {
    return isTaskValidForProjectDetail(detail)
        && UserToProjectRelationshipUtil.canAccessProjectTask(ProjectTask.WORK_PLAN_COLLABORATION_OPPORTUNITIES,
        userToProjectRelationships);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

    forwardWorkPlanCollaborationSetupService.copySectionData(fromDetail, toDetail);

    final var duplicatedOpportunityEntities = entityDuplicationService.duplicateEntitiesAndSetNewParent(
        getOpportunitiesForDetail(fromDetail),
        toDetail,
        ForwardWorkPlanCollaborationOpportunity.class
    );

    final var duplicatedOpportunityEntityMap = entityDuplicationService.createDuplicatedEntityPairingMap(
        duplicatedOpportunityEntities
    );

    forwardWorkPlanCollaborationOpportunityFileLinkService.copyCollaborationOpportunityFileLinkData(
        fromDetail,
        toDetail,
        duplicatedOpportunityEntityMap
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.FORWARD_WORK_PLAN);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    forwardWorkPlanCollaborationSetupService.removeSectionData(projectDetail);
    final var collaborationOpportunities = getOpportunitiesForDetail(projectDetail);
    forwardWorkPlanCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(collaborationOpportunities);
    forwardWorkPlanCollaborationOpportunityRepository.deleteAll(collaborationOpportunities);
  }
}
