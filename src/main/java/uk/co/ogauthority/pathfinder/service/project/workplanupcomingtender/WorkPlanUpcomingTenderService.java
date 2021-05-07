package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.WorkPlanUpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class WorkPlanUpcomingTenderService implements ProjectFormSectionService {

  private final FunctionService functionService;
  private final ValidationService validationService;
  private final WorkPlanUpcomingTenderFormValidator workPlanUpcomingTenderFormValidator;
  private final WorkPlanUpcomingTenderRepository workPlanUpcomingTenderRepository;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public WorkPlanUpcomingTenderService(FunctionService functionService,
                                       ValidationService validationService,
                                       WorkPlanUpcomingTenderFormValidator workPlanUpcomingTenderFormValidator,
                                       WorkPlanUpcomingTenderRepository workPlanUpcomingTenderRepository,
                                       SearchSelectorService searchSelectorService) {
    this.functionService = functionService;
    this.validationService = validationService;
    this.workPlanUpcomingTenderFormValidator = workPlanUpcomingTenderFormValidator;
    this.workPlanUpcomingTenderRepository = workPlanUpcomingTenderRepository;
    this.searchSelectorService = searchSelectorService;
  }

  public List<RestSearchItem> findDepartmentTenderLikeWithManualEntry(String searchTerm) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.WORK_PLAN_UPCOMING_TENDER);
  }

  public WorkPlanUpcomingTender getOrError(Integer upcomingTenderId) {
    return workPlanUpcomingTenderRepository.findById(upcomingTenderId).orElseThrow(
        () -> new PathfinderEntityNotFoundException(
            String.format("Unable to find tender with id: %s", upcomingTenderId)
        )
    );
  }

  public BindingResult validate(WorkPlanUpcomingTenderForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    workPlanUpcomingTenderFormValidator.validate(form, bindingResult, new WorkPlanUpcomingTenderValidationHint(validationType));
    return validationService.validate(form, bindingResult, validationType);
  }

  public boolean isValid(WorkPlanUpcomingTender workPlanUpcomingTender, ValidationType validationType) {
    var form = getForm(workPlanUpcomingTender);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  public WorkPlanUpcomingTenderForm getForm(WorkPlanUpcomingTender workPlanUpcomingTender) {
    var form = new WorkPlanUpcomingTenderForm();

    if (workPlanUpcomingTender.getDepartmentType() != null) {
      form.setDepartmentType(workPlanUpcomingTender.getDepartmentType().name());
    } else if (workPlanUpcomingTender.getManualDepartmentType() != null) {
      form.setDepartmentType(SearchSelectorService.getValueWithManualEntryPrefix(workPlanUpcomingTender.getManualDepartmentType()));
    }

    form.setEstimatedTenderDate(new ThreeFieldDateInput(workPlanUpcomingTender.getEstimatedTenderDate()));
    form.setDescriptionOfWork(workPlanUpcomingTender.getDescriptionOfWork());
    form.setContractBand(workPlanUpcomingTender.getContractBand());
    form.setContactDetail(new ContactDetailForm(workPlanUpcomingTender));

    setContractTermDurationFromEntity(workPlanUpcomingTender, form);

    return form;
  }

  @Transactional
  public WorkPlanUpcomingTender createUpcomingTender(ProjectDetail detail,
                                                     WorkPlanUpcomingTenderForm form) {
    var upcomingTender = new WorkPlanUpcomingTender(detail);
    return updateUpcomingTender(upcomingTender, form);
  }

  @Transactional
  public WorkPlanUpcomingTender updateUpcomingTender(WorkPlanUpcomingTender workPlanUpcomingTender,
                                                     WorkPlanUpcomingTenderForm form) {
    setCommonFields(workPlanUpcomingTender, form);
    return workPlanUpcomingTenderRepository.save(workPlanUpcomingTender);
  }

  public List<WorkPlanUpcomingTender> getUpcomingTendersForDetail(ProjectDetail projectDetail) {
    return workPlanUpcomingTenderRepository.findByProjectDetailOrderByIdAsc(projectDetail);
  }

  public List<WorkPlanUpcomingTender> getUpcomingTendersForProjectAndVersion(Project project, Integer version) {
    return workPlanUpcomingTenderRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        project,
        version
    );
  }

  private void setCommonFields(WorkPlanUpcomingTender upcomingTender, WorkPlanUpcomingTenderForm form) {

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        form.getDepartmentType(),
        Function.values(),
        upcomingTender::setManualDepartmentType,
        upcomingTender::setDepartmentType
    );

    upcomingTender.setDescriptionOfWork(form.getDescriptionOfWork());
    upcomingTender.setEstimatedTenderDate(form.getEstimatedTenderDate().createDateOrNull());
    upcomingTender.setContractBand(form.getContractBand());

    var contactDetailsForm = form.getContactDetail();
    upcomingTender.setContactName(contactDetailsForm.getName());
    upcomingTender.setPhoneNumber(contactDetailsForm.getPhoneNumber());
    upcomingTender.setJobTitle(contactDetailsForm.getJobTitle());
    upcomingTender.setEmailAddress(contactDetailsForm.getEmailAddress());

    upcomingTender.setContractTermDurationPeriod(form.getContractTermDurationPeriod());
    upcomingTender.setContractTermDuration(getContractTermDurationFromForm(form));
  }

  private Integer getContractTermDurationFromForm(WorkPlanUpcomingTenderForm form) {

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

  private void setContractTermDurationFromEntity(WorkPlanUpcomingTender workPlanUpcomingTender,
                                                 WorkPlanUpcomingTenderForm form) {

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
  public void delete(WorkPlanUpcomingTender workPlanUpcomingTender) {
    workPlanUpcomingTenderRepository.delete(workPlanUpcomingTender);
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    var upcomingTenders = getUpcomingTendersForDetail(detail);
    return !upcomingTenders.isEmpty() && upcomingTenders.stream()
        .allMatch(ut -> isValid(ut, ValidationType.FULL));
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return ProjectService.isForwardWorkPlanProject(detail);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    //TODO method will be implemented with PAT-470
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    //TODO method will be implemented with PAT-535
  }
}
