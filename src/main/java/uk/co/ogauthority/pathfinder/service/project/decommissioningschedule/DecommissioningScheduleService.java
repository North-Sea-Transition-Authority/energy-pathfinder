package uk.co.ogauthority.pathfinder.service.project.decommissioningschedule;

import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.decommissioningschedule.DecommissioningScheduleController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissioningschedule.DecommissioningSchedule;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.CessationOfProductionDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.DecommissioningStartDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.decommissioningschedule.DecommissioningScheduleForm;
import uk.co.ogauthority.pathfinder.model.form.project.decommissioningschedule.DecommissioningScheduleFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.decommissioningschedule.DecommissioningScheduleValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.decommissioningschedule.DecommissioningScheduleRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;

@Service
public class DecommissioningScheduleService implements ProjectFormSectionService {

  public static final String TEMPLATE_PATH = "project/decommissioningschedule/decommissioningSchedule";

  private final DecommissioningScheduleRepository decommissioningScheduleRepository;
  private final ProjectInformationService projectInformationService;
  private final ValidationService validationService;
  private final DecommissioningScheduleFormValidator decommissioningScheduleFormValidator;
  private final EntityDuplicationService entityDuplicationService;
  private final BreadcrumbService breadcrumbService;

  @Autowired
  public DecommissioningScheduleService(
      DecommissioningScheduleRepository decommissioningScheduleRepository,
      ProjectInformationService projectInformationService,
      ValidationService validationService,
      DecommissioningScheduleFormValidator decommissioningScheduleFormValidator,
      EntityDuplicationService entityDuplicationService,
      BreadcrumbService breadcrumbService) {
    this.decommissioningScheduleRepository = decommissioningScheduleRepository;
    this.projectInformationService = projectInformationService;
    this.validationService = validationService;
    this.decommissioningScheduleFormValidator = decommissioningScheduleFormValidator;
    this.entityDuplicationService = entityDuplicationService;
    this.breadcrumbService = breadcrumbService;
  }

  public BindingResult validate(DecommissioningScheduleForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(validationType);
    decommissioningScheduleFormValidator.validate(form, bindingResult, decommissioningScheduleValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  @Transactional
  public DecommissioningSchedule createOrUpdate(ProjectDetail projectDetail, DecommissioningScheduleForm form) {
    var decommissioningSchedule = decommissioningScheduleRepository.findByProjectDetail(projectDetail)
        .orElse(new DecommissioningSchedule());

    decommissioningSchedule.setProjectDetail(projectDetail);

    setDecommissioningScheduleDecomData(decommissioningSchedule, form);
    setDecommissioningScheduleCopData(decommissioningSchedule, form);

    return decommissioningScheduleRepository.save(decommissioningSchedule);
  }

  private void setDecommissioningScheduleDecomData(DecommissioningSchedule decommissioningSchedule,
                                                   DecommissioningScheduleForm form) {
    var decommissioningStartDateType = form.getDecommissioningStartDateType();
    decommissioningSchedule.setDecommissioningStartDateType(decommissioningStartDateType);

    if (DecommissioningStartDateType.EXACT.equals(decommissioningStartDateType)) {
      decommissioningSchedule.setExactDecommissioningStartDate(
          form.getExactDecommissioningStartDate() != null
              ? form.getExactDecommissioningStartDate().createDateOrNull()
              : null
      );
    } else {
      decommissioningSchedule.setExactDecommissioningStartDate(null);
    }

    if (DecommissioningStartDateType.ESTIMATED.equals(decommissioningStartDateType)) {
      var estimatedDecommissioningStartDate = form.getEstimatedDecommissioningStartDate();
      decommissioningSchedule.setEstimatedDecommissioningStartDateQuarter(
          estimatedDecommissioningStartDate != null
              ? estimatedDecommissioningStartDate.getQuarter()
              : null
      );
      decommissioningSchedule.setEstimatedDecommissioningStartDateYear(
          estimatedDecommissioningStartDate != null && estimatedDecommissioningStartDate.getYear() != null
              ? Integer.parseInt(estimatedDecommissioningStartDate.getYear())
              : null
      );
    } else {
      decommissioningSchedule.setEstimatedDecommissioningStartDateQuarter(null);
      decommissioningSchedule.setEstimatedDecommissioningStartDateYear(null);
    }

    if (DecommissioningStartDateType.UNKNOWN.equals(decommissioningStartDateType)) {
      decommissioningSchedule.setDecommissioningStartDateNotProvidedReason(
          form.getDecommissioningStartDateNotProvidedReason()
      );
    } else {
      decommissioningSchedule.setDecommissioningStartDateNotProvidedReason(null);
    }
  }

  private void setDecommissioningScheduleCopData(DecommissioningSchedule decommissioningSchedule,
                                                 DecommissioningScheduleForm form) {
    var cessationOfProductionDateType = form.getCessationOfProductionDateType();
    decommissioningSchedule.setCessationOfProductionDateType(cessationOfProductionDateType);

    if (CessationOfProductionDateType.EXACT.equals(cessationOfProductionDateType)) {
      decommissioningSchedule.setExactCessationOfProductionDate(
          form.getExactCessationOfProductionDate() != null
              ? form.getExactCessationOfProductionDate().createDateOrNull()
              : null
      );
    } else {
      decommissioningSchedule.setExactCessationOfProductionDate(null);
    }

    if (CessationOfProductionDateType.ESTIMATED.equals(cessationOfProductionDateType)) {
      var estimatedCessationOfProductionDate = form.getEstimatedCessationOfProductionDate();
      decommissioningSchedule.setEstimatedCessationOfProductionDateQuarter(
          estimatedCessationOfProductionDate != null
              ? estimatedCessationOfProductionDate.getQuarter()
              : null
      );
      decommissioningSchedule.setEstimatedCessationOfProductionDateYear(
          estimatedCessationOfProductionDate != null && estimatedCessationOfProductionDate.getYear() != null
              ? Integer.parseInt(estimatedCessationOfProductionDate.getYear())
              : null
      );
    } else {
      decommissioningSchedule.setEstimatedCessationOfProductionDateQuarter(null);
      decommissioningSchedule.setEstimatedCessationOfProductionDateYear(null);
    }

    if (CessationOfProductionDateType.UNKNOWN.equals(cessationOfProductionDateType)) {
      decommissioningSchedule.setCessationOfProductionDateNotProvidedReason(
          form.getCessationOfProductionDateNotProvidedReason()
      );
    } else {
      decommissioningSchedule.setCessationOfProductionDateNotProvidedReason(null);
    }
  }

  public Optional<DecommissioningSchedule> getDecommissioningSchedule(ProjectDetail projectDetail) {
    return decommissioningScheduleRepository.findByProjectDetail(projectDetail);
  }

  public DecommissioningSchedule getDecommissioningScheduleOrError(ProjectDetail projectDetail) {
    return getDecommissioningSchedule(projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Unable to find DecommissioningSchedule for projectDetail with ID %s", projectDetail.getId())));
  }

  public Optional<DecommissioningSchedule> getDecommissioningScheduleByProjectAndVersion(Project project, Integer version) {
    return decommissioningScheduleRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version);
  }

  public DecommissioningScheduleForm getForm(ProjectDetail projectDetail) {
    return decommissioningScheduleRepository.findByProjectDetail(projectDetail)
        .map(this::getForm)
        .orElse(new DecommissioningScheduleForm());
  }

  private DecommissioningScheduleForm getForm(DecommissioningSchedule decommissioningSchedule) {
    var form = new DecommissioningScheduleForm();
    setFormDecomData(form, decommissioningSchedule);
    setFormCopData(form, decommissioningSchedule);
    return form;
  }

  private void setFormDecomData(DecommissioningScheduleForm form, DecommissioningSchedule decommissioningSchedule) {
    var decommissioningStartDateType = decommissioningSchedule.getDecommissioningStartDateType();
    form.setDecommissioningStartDateType(decommissioningStartDateType);

    if (DecommissioningStartDateType.EXACT.equals(decommissioningStartDateType)) {
      form.setExactDecommissioningStartDate(
          new ThreeFieldDateInput(decommissioningSchedule.getExactDecommissioningStartDate())
      );
    } else {
      form.setExactDecommissioningStartDate(null);
    }

    if (DecommissioningStartDateType.ESTIMATED.equals(decommissioningStartDateType)) {
      form.setEstimatedDecommissioningStartDate(
          new QuarterYearInput(
              decommissioningSchedule.getEstimatedDecommissioningStartDateQuarter(),
              decommissioningSchedule.getEstimatedDecommissioningStartDateYear() != null
                  ? Integer.toString(decommissioningSchedule.getEstimatedDecommissioningStartDateYear())
                  : null
          )
      );
    } else {
      form.setEstimatedDecommissioningStartDate(null);
    }

    if (DecommissioningStartDateType.UNKNOWN.equals(decommissioningStartDateType)) {
      form.setDecommissioningStartDateNotProvidedReason(
          decommissioningSchedule.getDecommissioningStartDateNotProvidedReason()
      );
    } else {
      form.setDecommissioningStartDateNotProvidedReason(null);
    }
  }

  private void setFormCopData(DecommissioningScheduleForm form, DecommissioningSchedule decommissioningSchedule) {
    var cessationOfProductionDateType = decommissioningSchedule.getCessationOfProductionDateType();
    form.setCessationOfProductionDateType(cessationOfProductionDateType);

    if (CessationOfProductionDateType.EXACT.equals(cessationOfProductionDateType)) {
      form.setExactCessationOfProductionDate(
          new ThreeFieldDateInput(decommissioningSchedule.getExactCessationOfProductionDate())
      );
    } else {
      form.setExactCessationOfProductionDate(null);
    }

    if (CessationOfProductionDateType.ESTIMATED.equals(cessationOfProductionDateType)) {
      form.setEstimatedCessationOfProductionDate(
          new QuarterYearInput(
              decommissioningSchedule.getEstimatedCessationOfProductionDateQuarter(),
              decommissioningSchedule.getEstimatedCessationOfProductionDateYear() != null
                  ? Integer.toString(decommissioningSchedule.getEstimatedCessationOfProductionDateYear())
                  : null
          )
      );
    } else {
      form.setEstimatedCessationOfProductionDate(null);
    }

    if (CessationOfProductionDateType.UNKNOWN.equals(cessationOfProductionDateType)) {
      form.setCessationOfProductionDateNotProvidedReason(
          decommissioningSchedule.getCessationOfProductionDateNotProvidedReason()
      );
    } else {
      form.setCessationOfProductionDateNotProvidedReason(null);
    }
  }

  public ModelAndView getDecommissioningScheduleModelAndView(Integer projectId, DecommissioningScheduleForm form) {
    var modelAndView = new ModelAndView(TEMPLATE_PATH)
        .addObject("form", form)
        .addObject("pageName", DecommissioningScheduleController.PAGE_NAME)
        .addObject("exactDecommissioningStartDateType",
            DecommissioningStartDateType.getEntryAsMap(DecommissioningStartDateType.EXACT))
        .addObject("estimatedDecommissioningStartDateType",
            DecommissioningStartDateType.getEntryAsMap(DecommissioningStartDateType.ESTIMATED))
        .addObject("unknownDecommissioningStartDateType",
            DecommissioningStartDateType.getEntryAsMap(DecommissioningStartDateType.UNKNOWN))
        .addObject("exactCessationOfProductionDateType",
            CessationOfProductionDateType.getEntryAsMap(CessationOfProductionDateType.EXACT))
        .addObject("estimatedCessationOfProductionDateType",
            CessationOfProductionDateType.getEntryAsMap(CessationOfProductionDateType.ESTIMATED))
        .addObject("unknownCessationOfProductionDateType",
            CessationOfProductionDateType.getEntryAsMap(CessationOfProductionDateType.UNKNOWN))
        .addObject("quarters", Quarter.getAllAsMap());

    breadcrumbService.fromTaskList(projectId, modelAndView, DecommissioningScheduleController.PAGE_NAME);
    return modelAndView;
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    var form = getForm(detail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }

  @Override
  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return ProjectService.isInfrastructureProject(detail) && projectInformationService.isDecomRelated(detail);
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail, Set<UserToProjectRelationship> userToProjectRelationships) {
    return isTaskValidForProjectDetail(detail)
        && UserToProjectRelationshipUtil.canAccessProjectTask(ProjectTask.DECOMMISSIONING_SCHEDULE,
        userToProjectRelationships);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    decommissioningScheduleRepository.deleteByProjectDetail(projectDetail);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntityAndSetNewParent(
        getDecommissioningScheduleOrError(fromDetail),
        toDetail,
        DecommissioningSchedule.class
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }
}
