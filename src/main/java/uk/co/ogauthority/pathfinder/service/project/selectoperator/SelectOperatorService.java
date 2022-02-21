package uk.co.ogauthority.pathfinder.service.project.selectoperator;

import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorFormValidator;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class SelectOperatorService implements ProjectFormSectionService {

  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final ValidationService validationService;
  private final ProjectOperatorService projectOperatorService;
  private final EntityDuplicationService entityDuplicationService;
  private final ProjectOperatorFormValidator projectOperatorFormValidator;

  @Autowired
  public SelectOperatorService(
      PortalOrganisationAccessor portalOrganisationAccessor,
      ValidationService validationService,
      ProjectOperatorService projectOperatorService,
      EntityDuplicationService entityDuplicationService,
      ProjectOperatorFormValidator projectOperatorFormValidator
  ) {
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.validationService = validationService;
    this.projectOperatorService = projectOperatorService;
    this.entityDuplicationService = entityDuplicationService;
    this.projectOperatorFormValidator = projectOperatorFormValidator;
  }

  /**
   * Get the PortalOrganisationGroup for the given id.
   * If the user cannot access this group then error.
   * @param user user trying to access the group
   * @param orgGrpId portalOrganisationUnit orgGrpId
   * @return the PortalOrganisationGroup with the given id
   */
  public PortalOrganisationGroup getOrganisationGroupOrError(AuthenticatedUserAccount user, Integer orgGrpId) {
    var orgGroup =  getOrganisationGroupOrError(orgGrpId);

    if (!projectOperatorService.canUserAccessOrgGroup(user, orgGroup)) {
      throw new AccessDeniedException(
          String.format(
              "User with wua: %d does not have access to organisation group with id: %d",
              user.getWuaId(),
              orgGroup.getOrgGrpId())
      );
    }
    return orgGroup;
  }

  /**
   * Get the PortalOrganisationGroup for the specified id, error if it does not exist.
   * @param orgGrpId id of the PortalOrganisationGroup
   * @return the PortalOrganisationGroup with the specified id
   */
  public PortalOrganisationGroup getOrganisationGroupOrError(Integer orgGrpId) {
    return portalOrganisationAccessor.getOrganisationGroupOrError(orgGrpId);
  }

  public ProjectOperator updateProjectOperator(ProjectDetail detail, ProjectOperatorForm projectOperatorForm) {
    return projectOperatorService.createOrUpdateProjectOperator(detail, projectOperatorForm);
  }

  public BindingResult validate(ProjectOperatorForm form,
                                BindingResult bindingResult) {
    projectOperatorFormValidator.validate(form, bindingResult);
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  /**
   * Get the projectOperator and build the form. Error if projectOperator not found.
   * @param detail detail to build the form for.
   * @return form with detail's associated organisationGroup.
   */
  public ProjectOperatorForm getForm(ProjectDetail detail) {

    final var projectOperator = getProjectOperatorOrError(detail);

    final var form = new ProjectOperatorForm();
    form.setOperator(projectOperator.getOrganisationGroup().getOrgGrpId().toString());
    form.setIsPublishedAsOperator(projectOperator.isPublishedAsOperator());

    if (
        BooleanUtils.isFalse(projectOperator.isPublishedAsOperator())
        &&
        projectOperator.getPublishableOrganisationUnit() != null
    ) {
      form.setPublishableOrganisation(String.valueOf(projectOperator.getPublishableOrganisationUnit().getOuId()));
    }

    return form;
  }

  private ProjectOperator getProjectOperatorOrError(ProjectDetail detail) {
    return projectOperatorService.getProjectOperatorByProjectDetail(detail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format(
                "No ProjectOperator found for detail id: %d", detail.getId()
            )
        ));
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    var form = getForm(detail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult);
    return !bindingResult.hasErrors();
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntityAndSetNewParent(
        getProjectOperatorOrError(fromDetail),
        toDetail,
        ProjectOperator.class
    );
  }

  @Override
  public boolean alwaysCopySectionData(ProjectDetail projectDetail) {
    return ProjectType.FORWARD_WORK_PLAN.equals(projectDetail.getProjectType());
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return ProjectService.isInfrastructureProject(detail);
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    projectOperatorService.deleteProjectOperatorByProjectDetail(projectDetail);
  }

  @Override
  public boolean allowSectionDataCleanUp(ProjectDetail projectDetail) {
    return false;
  }
}
