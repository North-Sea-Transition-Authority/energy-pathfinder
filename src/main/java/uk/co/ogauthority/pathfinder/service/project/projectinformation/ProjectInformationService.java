package uk.co.ogauthority.pathfinder.service.project.projectinformation;

import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;

@Service
public class ProjectInformationService implements ProjectFormSectionService {

  private final ProjectInformationRepository projectInformationRepository;
  private final ValidationService validationService;
  private final ProjectInformationFormValidator projectInformationFormValidator;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public ProjectInformationService(ProjectInformationRepository projectInformationRepository,
                                   ValidationService validationService,
                                   ProjectInformationFormValidator projectInformationFormValidator,
                                   EntityDuplicationService entityDuplicationService) {
    this.projectInformationRepository = projectInformationRepository;
    this.validationService = validationService;
    this.projectInformationFormValidator = projectInformationFormValidator;
    this.entityDuplicationService = entityDuplicationService;
  }

  @Transactional
  public ProjectInformation createOrUpdate(ProjectDetail projectDetail, ProjectInformationForm form) {
    var projectInformation = projectInformationRepository.findByProjectDetail(projectDetail)
        .orElse(new ProjectInformation());

    projectInformation.setProjectDetail(projectDetail);
    projectInformation.setFieldStage(form.getFieldStage());
    projectInformation.setProjectTitle(form.getProjectTitle());
    projectInformation.setProjectSummary(form.getProjectSummary());

    var contactDetailForm = form.getContactDetail();
    projectInformation.setContactName(contactDetailForm.getName());
    projectInformation.setPhoneNumber(contactDetailForm.getPhoneNumber());
    projectInformation.setJobTitle(contactDetailForm.getJobTitle());
    projectInformation.setEmailAddress(contactDetailForm.getEmailAddress());

    setEntityHiddenFieldStageData(form, projectInformation);

    return projectInformationRepository.save(projectInformation);
  }

  private void clearFirstProductionDate(ProjectInformation projectInformation) {
    projectInformation.setFirstProductionDateYear(null);
    projectInformation.setFirstProductionDateQuarter(null);
  }

  private void clearFirstProductionDate(ProjectInformationForm form) {
    form.setDevelopmentFirstProductionDate(null);
  }

  private void clearFieldStageCategory(ProjectInformation projectInformation) {
    projectInformation.setFieldStageSubCategory(null);
  }

  private void clearFieldStageCategory(ProjectInformationForm form) {
    form.setFieldStageSubCategory(null);
  }

  public Optional<ProjectInformation> getProjectInformationByProjectAndVersion(Project project, Integer version) {
    return projectInformationRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version);
  }

  public Optional<ProjectInformation> getProjectInformation(ProjectDetail projectDetail) {
    return projectInformationRepository.findByProjectDetail(projectDetail);
  }

  public ProjectInformation getProjectInformationOrError(ProjectDetail projectDetail) {
    return getProjectInformation(projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Unable to find ProjectInformation for projectDetail with ID %s", projectDetail.getId())));
  }

  public String getProjectTitle(ProjectDetail detail) {
    var title = projectInformationRepository.findTitleByProjectDetail(detail);
    return title != null ? title : "";
  }

  public ProjectInformationForm getForm(ProjectDetail projectDetail) {
    return projectInformationRepository.findByProjectDetail(projectDetail)
        .map(this::getForm).orElse(new ProjectInformationForm());
  }

  private ProjectInformationForm getForm(ProjectInformation projectInformation) {

    var form  = new ProjectInformationForm();
    form.setFieldStage(projectInformation.getFieldStage());
    form.setProjectTitle(projectInformation.getProjectTitle());
    form.setProjectSummary(projectInformation.getProjectSummary());
    form.setContactDetail(new ContactDetailForm(projectInformation));

    setFormHiddenFieldStageData(projectInformation, form);

    return form;
  }

  private QuarterYearInput getFirstProductionDate(ProjectInformation projectInformation) {
    var firstProductionDate = new QuarterYearInput();
    firstProductionDate.setQuarter(projectInformation.getFirstProductionDateQuarter());
    firstProductionDate.setYear(projectInformation.getFirstProductionDateYear());
    return firstProductionDate;
  }

  /**
   * Validate the ProjectInformationForm.
   */
  public BindingResult validate(ProjectInformationForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    var projectInformationValidationHint = new ProjectInformationValidationHint(validationType);
    projectInformationFormValidator.validate(form, bindingResult, projectInformationValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  private void setEntityHiddenFieldStageData(ProjectInformationForm form, ProjectInformation projectInformation) {

    var fieldStage = form.getFieldStage();
    var fieldStagesWithHiddenInputs = FieldStage.getAllWithHiddenInputs();
    var fieldStagesWithSubCategories = FieldStageSubCategory.getAllFieldStagesWithSubCategories();

    if (fieldStage == null || !fieldStagesWithHiddenInputs.contains(fieldStage)) {
      clearFirstProductionDate(projectInformation);
      clearFieldStageCategory(projectInformation);
    } else if (fieldStage.equals(FieldStage.DEVELOPMENT)) {
      form.getDevelopmentFirstProductionDate()
          .create()
          .ifPresent(quarterYearInput -> {
            projectInformation.setFirstProductionDateQuarter(quarterYearInput.getQuarter());
            projectInformation.setFirstProductionDateYear(Integer.parseInt(quarterYearInput.getYear()));
          });

      // These inputs are hidden if development field stage
      clearFieldStageCategory(projectInformation);

    } else if (fieldStagesWithSubCategories.contains(fieldStage)) {
      projectInformation.setFieldStageSubCategory(form.getFieldStageSubCategory());

      // These inputs are hidden if energy transition field stage
      clearFirstProductionDate(projectInformation);
    }
  }

  private void setFormHiddenFieldStageData(ProjectInformation projectInformation, ProjectInformationForm form) {

    var fieldStage = projectInformation.getFieldStage();
    var fieldStagesWithHiddenInputs = FieldStage.getAllWithHiddenInputs();
    var fieldStagesWithSubCategories = FieldStageSubCategory.getAllFieldStagesWithSubCategories();

    if (fieldStage == null || !fieldStagesWithHiddenInputs.contains(fieldStage)) {
      clearFirstProductionDate(form);
      clearFieldStageCategory(form);
    } else if (fieldStage.equals(FieldStage.DEVELOPMENT)) {
      form.setDevelopmentFirstProductionDate(getFirstProductionDate(projectInformation));
      clearFieldStageCategory(form);
    } else if (fieldStagesWithSubCategories.contains(fieldStage)) {
      form.setFieldStageSubCategory(projectInformation.getFieldStageSubCategory());
      clearFirstProductionDate(form);
    }
  }

  public boolean isDecomRelated(ProjectDetail detail) {
    return getProjectInformation(detail).map(p -> FieldStage.DECOMMISSIONING.equals(p.getFieldStage()))
        .orElse(false);
  }

  boolean isEnergyTransitionProject(ProjectDetail projectDetail) {
    return getProjectInformation(projectDetail)
        .map(this::isEnergyTransitionProject)
        .orElse(false);
  }

  public boolean isEnergyTransitionProject(ProjectInformation projectInformation) {
    return FieldStage.getEnergyTransitionProjectFieldStages().contains(projectInformation.getFieldStage());
  }

  public boolean isOilAndGasProject(ProjectDetail projectDetail) {
    return getProjectInformation(projectDetail)
        .map(projectInformation -> !isEnergyTransitionProject(projectInformation))
        .orElse(false);
  }

  public Optional<FieldStage> getFieldStage(ProjectDetail projectDetail) {
    return getProjectInformation(projectDetail)
        .map(ProjectInformation::getFieldStage);
  }

  @Override
  public boolean isComplete(ProjectDetail details) {
    var form = getForm(details);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }

  @Override
  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return ProjectService.isInfrastructureProject(detail);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    projectInformationRepository.deleteByProjectDetail(projectDetail);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntityAndSetNewParent(
        getProjectInformationOrError(fromDetail),
        toDetail,
        ProjectInformation.class
    );
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail, Set<UserToProjectRelationship> userToProjectRelationships) {
    return isTaskValidForProjectDetail(detail)
        && UserToProjectRelationshipUtil.canAccessProjectTask(ProjectTask.PROJECT_INFORMATION,
        userToProjectRelationships);
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }
}
