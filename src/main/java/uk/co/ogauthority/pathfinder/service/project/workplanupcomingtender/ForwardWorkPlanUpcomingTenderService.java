package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanTenderSetup;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;

@Service
public class ForwardWorkPlanUpcomingTenderService implements ProjectFormSectionService {

  private final FunctionService functionService;
  private final ValidationService validationService;
  private final ForwardWorkPlanUpcomingTenderFormValidator workPlanUpcomingTenderFormValidator;
  private final ForwardWorkPlanUpcomingTenderRepository workPlanUpcomingTenderRepository;
  private final SearchSelectorService searchSelectorService;
  private final EntityDuplicationService entityDuplicationService;
  private final ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;

  @Autowired
  public ForwardWorkPlanUpcomingTenderService(FunctionService functionService,
                                              ValidationService validationService,
                                              ForwardWorkPlanUpcomingTenderFormValidator workPlanUpcomingTenderFormValidator,
                                              ForwardWorkPlanUpcomingTenderRepository workPlanUpcomingTenderRepository,
                                              SearchSelectorService searchSelectorService,
                                              EntityDuplicationService entityDuplicationService,
                                              ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService) {
    this.functionService = functionService;
    this.validationService = validationService;
    this.workPlanUpcomingTenderFormValidator = workPlanUpcomingTenderFormValidator;
    this.workPlanUpcomingTenderRepository = workPlanUpcomingTenderRepository;
    this.searchSelectorService = searchSelectorService;
    this.entityDuplicationService = entityDuplicationService;
    this.forwardWorkPlanTenderSetupService = forwardWorkPlanTenderSetupService;
  }

  public List<RestSearchItem> findDepartmentTenderLikeWithManualEntry(String searchTerm) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.WORK_PLAN_UPCOMING_TENDER);
  }

  public ForwardWorkPlanUpcomingTender getOrError(Integer upcomingTenderId) {
    return workPlanUpcomingTenderRepository.findById(upcomingTenderId).orElseThrow(
        () -> new PathfinderEntityNotFoundException(
            String.format("Unable to find tender with id: %s", upcomingTenderId)
        )
    );
  }

  public BindingResult validate(ForwardWorkPlanUpcomingTenderForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    workPlanUpcomingTenderFormValidator.validate(form, bindingResult, new ForwardWorkPlanUpcomingTenderValidationHint(validationType));
    return validationService.validate(form, bindingResult, validationType);
  }

  public boolean isValid(ForwardWorkPlanUpcomingTender workPlanUpcomingTender, ValidationType validationType) {
    var form = getForm(workPlanUpcomingTender);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  public ForwardWorkPlanUpcomingTenderForm getForm(ForwardWorkPlanUpcomingTender workPlanUpcomingTender) {
    var form = new ForwardWorkPlanUpcomingTenderForm();

    if (workPlanUpcomingTender.getDepartmentType() != null) {
      form.setDepartmentType(workPlanUpcomingTender.getDepartmentType().name());
    } else if (workPlanUpcomingTender.getManualDepartmentType() != null) {
      form.setDepartmentType(SearchSelectorService.getValueWithManualEntryPrefix(workPlanUpcomingTender.getManualDepartmentType()));
    }

    form.setEstimatedTenderStartDate(new QuarterYearInput(
        workPlanUpcomingTender.getEstimatedTenderDateQuarter(),
        workPlanUpcomingTender.getEstimatedTenderDateYear() != null
            ? Integer.toString(workPlanUpcomingTender.getEstimatedTenderDateYear())
            : null
        )
    );
    form.setDescriptionOfWork(workPlanUpcomingTender.getDescriptionOfWork());
    form.setContractBand(workPlanUpcomingTender.getContractBand());
    form.setContactDetail(new ContactDetailForm(workPlanUpcomingTender));

    setContractTermDurationFromEntity(workPlanUpcomingTender, form);

    return form;
  }

  @Transactional
  public ForwardWorkPlanUpcomingTender createUpcomingTender(ProjectDetail detail,
                                                            ForwardWorkPlanUpcomingTenderForm form) {
    var upcomingTender = new ForwardWorkPlanUpcomingTender(detail);
    return updateUpcomingTender(upcomingTender, form);
  }

  @Transactional
  public ForwardWorkPlanUpcomingTender updateUpcomingTender(ForwardWorkPlanUpcomingTender workPlanUpcomingTender,
                                                            ForwardWorkPlanUpcomingTenderForm form) {
    setCommonFields(workPlanUpcomingTender, form);
    return workPlanUpcomingTenderRepository.save(workPlanUpcomingTender);
  }

  public List<ForwardWorkPlanUpcomingTender> getUpcomingTendersForDetail(ProjectDetail projectDetail) {
    return workPlanUpcomingTenderRepository.findByProjectDetailOrderByIdAsc(projectDetail);
  }

  public List<ForwardWorkPlanUpcomingTender> getUpcomingTendersForProjectAndVersion(Project project, Integer version) {
    return workPlanUpcomingTenderRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        project,
        version
    );
  }

  private void setCommonFields(ForwardWorkPlanUpcomingTender upcomingTender, ForwardWorkPlanUpcomingTenderForm form) {

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        form.getDepartmentType(),
        Function.values(),
        upcomingTender::setManualDepartmentType,
        upcomingTender::setDepartmentType
    );

    upcomingTender.setDescriptionOfWork(form.getDescriptionOfWork());
    upcomingTender.setEstimatedTenderDateQuarter(
        form.getEstimatedTenderStartDate() != null && form.getEstimatedTenderStartDate().getQuarter() != null
            ? form.getEstimatedTenderStartDate().getQuarter()
            : null
    );
    upcomingTender.setEstimatedTenderDateYear(
        form.getEstimatedTenderStartDate() != null && form.getEstimatedTenderStartDate().getYear() != null
            ? Integer.parseInt(form.getEstimatedTenderStartDate().getYear())
            : null
    );
    upcomingTender.setContractBand(form.getContractBand());

    var contactDetailsForm = form.getContactDetail();
    upcomingTender.setContactName(contactDetailsForm.getName());
    upcomingTender.setPhoneNumber(contactDetailsForm.getPhoneNumber());
    upcomingTender.setJobTitle(contactDetailsForm.getJobTitle());
    upcomingTender.setEmailAddress(contactDetailsForm.getEmailAddress());

    upcomingTender.setContractTermDurationPeriod(form.getContractTermDurationPeriod());
    upcomingTender.setContractTermDuration(getContractTermDurationFromForm(form));
  }

  private Integer getContractTermDurationFromForm(ForwardWorkPlanUpcomingTenderForm form) {

    final var durationPeriod = form.getContractTermDurationPeriod();

    if (DurationPeriod.DAYS.equals(durationPeriod)) {
      return form.getContractTermDayDuration();
    } else if (DurationPeriod.WEEKS.equals(durationPeriod)) {
      return form.getContractTermWeekDuration();
    } else if (DurationPeriod.MONTHS.equals(durationPeriod)) {
      return form.getContractTermMonthDuration();
    } else if (DurationPeriod.YEARS.equals(durationPeriod)) {
      return form.getContractTermYearDuration();
    } else {
      return null;
    }
  }

  private void setContractTermDurationFromEntity(ForwardWorkPlanUpcomingTender workPlanUpcomingTender,
                                                 ForwardWorkPlanUpcomingTenderForm form) {

    final var durationPeriod = workPlanUpcomingTender.getContractTermDurationPeriod();
    form.setContractTermDurationPeriod(durationPeriod);

    final var duration = workPlanUpcomingTender.getContractTermDuration();

    if (DurationPeriod.DAYS.equals(durationPeriod)) {
      form.setContractTermDayDuration(duration);
    } else if (DurationPeriod.WEEKS.equals(durationPeriod)) {
      form.setContractTermWeekDuration(duration);
    } else if (DurationPeriod.MONTHS.equals(durationPeriod)) {
      form.setContractTermMonthDuration(duration);
    } else if (DurationPeriod.YEARS.equals(durationPeriod)) {
      form.setContractTermYearDuration(duration);
    } else {
      form.setContractTermDayDuration(null);
      form.setContractTermWeekDuration(null);
      form.setContractTermMonthDuration(null);
      form.setContractTermYearDuration(null);
    }
  }

  @Transactional
  public void delete(ForwardWorkPlanUpcomingTender workPlanUpcomingTender) {
    workPlanUpcomingTenderRepository.delete(workPlanUpcomingTender);
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {

    final var forwardWorkPlanTenderSetup = forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(detail)
        .orElse(new ForwardWorkPlanTenderSetup());

    if (forwardWorkPlanTenderSetup.getHasTendersToAdd() == null) {
      return false;
    } else if (BooleanUtils.isTrue(forwardWorkPlanTenderSetup.getHasTendersToAdd())) {
      return forwardWorkPlanTenderSetup.getHasOtherTendersToAdd() != null
          && !hasOtherTendersToAdd(forwardWorkPlanTenderSetup)
          && areAllAddedTendersValid(detail);
    } else {
      // indicated that no tenders need to be added
      return true;
    }
  }

  @Override
  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return ProjectService.isForwardWorkPlanProject(detail);
  }

  private boolean hasOtherTendersToAdd(ForwardWorkPlanTenderSetup forwardWorkPlanTenderSetup) {
    return BooleanUtils.isTrue(forwardWorkPlanTenderSetup.getHasOtherTendersToAdd());
  }

  private boolean areAllAddedTendersValid(ProjectDetail projectDetail) {
    final var upcomingTenders = getUpcomingTendersForDetail(projectDetail);
    return !upcomingTenders.isEmpty() && upcomingTenders
        .stream()
        .allMatch(ut -> isValid(ut, ValidationType.FULL));
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail, Set<UserToProjectRelationship> userToProjectRelationships) {
    return isTaskValidForProjectDetail(detail)
        && UserToProjectRelationshipUtil.canAccessProjectTask(ProjectTask.WORK_PLAN_UPCOMING_TENDERS,
        userToProjectRelationships);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {

    forwardWorkPlanTenderSetupService.removeSectionData(projectDetail);

    final var upcomingTenders = getUpcomingTendersForDetail(projectDetail);
    workPlanUpcomingTenderRepository.deleteAll(upcomingTenders);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

    forwardWorkPlanTenderSetupService.copySectionData(fromDetail, toDetail);

    entityDuplicationService.duplicateEntitiesAndSetNewParent(
        getUpcomingTendersForDetail(fromDetail),
        toDetail,
        ForwardWorkPlanUpcomingTender.class
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.FORWARD_WORK_PLAN);
  }
}
