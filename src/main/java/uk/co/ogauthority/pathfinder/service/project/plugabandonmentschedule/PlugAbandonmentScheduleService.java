package uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule.PlugAbandonmentScheduleController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleForm;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleValidationHint;
import uk.co.ogauthority.pathfinder.model.view.plugabandonmentschedule.PlugAbandonmentScheduleView;
import uk.co.ogauthority.pathfinder.model.view.wellbore.WellboreView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.plugabandonmentschedule.PlugAbandonmentScheduleRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.service.wellbore.WellboreService;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@Service
public class PlugAbandonmentScheduleService implements ProjectFormSectionService {

  public static final String TEMPLATE_PATH = "project/plugabandonmentschedule/plugAbandonmentScheduleForm";
  public static final String REMOVE_TEMPLATE_PATH = "project/plugabandonmentschedule/removePlugAbandonmentSchedule";

  private final WellboreService wellboreService;
  private final ValidationService validationService;
  private final PlugAbandonmentScheduleFormValidator plugAbandonmentScheduleFormValidator;
  private final PlugAbandonmentScheduleRepository plugAbandonmentScheduleRepository;
  private final PlugAbandonmentWellService plugAbandonmentWellService;
  private final ProjectSetupService projectSetupService;
  private final EntityDuplicationService entityDuplicationService;
  private final BreadcrumbService breadcrumbService;

  public PlugAbandonmentScheduleService(WellboreService wellboreService,
                                        ValidationService validationService,
                                        PlugAbandonmentScheduleFormValidator plugAbandonmentScheduleFormValidator,
                                        PlugAbandonmentScheduleRepository plugAbandonmentScheduleRepository,
                                        PlugAbandonmentWellService plugAbandonmentWellService,
                                        ProjectSetupService projectSetupService,
                                        EntityDuplicationService entityDuplicationService,
                                        BreadcrumbService breadcrumbService) {
    this.wellboreService = wellboreService;
    this.validationService = validationService;
    this.plugAbandonmentScheduleFormValidator = plugAbandonmentScheduleFormValidator;
    this.plugAbandonmentScheduleRepository = plugAbandonmentScheduleRepository;
    this.plugAbandonmentWellService = plugAbandonmentWellService;
    this.projectSetupService = projectSetupService;
    this.entityDuplicationService = entityDuplicationService;
    this.breadcrumbService = breadcrumbService;
  }

  public String getWellboreRestUrl() {
    return wellboreService.getWellboreRestUrl();
  }

  public BindingResult validate(PlugAbandonmentScheduleForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    var plugAbandonmentScheduleValidationHint = new PlugAbandonmentScheduleValidationHint(validationType);
    plugAbandonmentScheduleFormValidator.validate(form, bindingResult, validationType, plugAbandonmentScheduleValidationHint);
    validationService.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  @Transactional
  public PlugAbandonmentSchedule createPlugAbandonmentSchedule(PlugAbandonmentScheduleForm form, ProjectDetail projectDetail) {
    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();
    plugAbandonmentSchedule.setProjectDetail(projectDetail);
    return updatePlugAbandonmentSchedule(form, plugAbandonmentSchedule);
  }

  @Transactional
  public PlugAbandonmentSchedule updatePlugAbandonmentSchedule(Integer plugAbandonmentScheduleId,
                                                               ProjectDetail projectDetail,
                                                               PlugAbandonmentScheduleForm plugAbandonmentScheduleForm) {
    var plugAbandonmentSchedule = getPlugAbandonmentScheduleOrError(plugAbandonmentScheduleId, projectDetail);
    return updatePlugAbandonmentSchedule(plugAbandonmentScheduleForm, plugAbandonmentSchedule);
  }

  protected PlugAbandonmentSchedule updatePlugAbandonmentSchedule(PlugAbandonmentScheduleForm form,
                                                                  PlugAbandonmentSchedule plugAbandonmentSchedule) {

    var plugAbandonmentDate = form.getPlugAbandonmentDate();
    plugAbandonmentSchedule.setEarliestStartYear(
        plugAbandonmentDate.getMinYear() != null ? Integer.parseInt(plugAbandonmentDate.getMinYear()) : null
    );
    plugAbandonmentSchedule.setLatestCompletionYear(
        plugAbandonmentDate.getMaxYear() != null ? Integer.parseInt(plugAbandonmentDate.getMaxYear()) : null
    );

    plugAbandonmentSchedule = plugAbandonmentScheduleRepository.save(plugAbandonmentSchedule);
    plugAbandonmentWellService.setPlugAbandonmentScheduleWells(plugAbandonmentSchedule, form.getWells());
    return plugAbandonmentSchedule;
  }

  @Transactional
  public void deletePlugAbandonmentSchedule(PlugAbandonmentSchedule plugAbandonmentSchedule) {
    plugAbandonmentWellService.deletePlugAbandonmentScheduleWells(plugAbandonmentSchedule);
    plugAbandonmentScheduleRepository.delete(plugAbandonmentSchedule);
  }

  public PlugAbandonmentScheduleForm getForm(PlugAbandonmentSchedule plugAbandonmentSchedule) {
    var form = new PlugAbandonmentScheduleForm();

    form.setPlugAbandonmentDate(new MinMaxDateInput(
        StringDisplayUtil.getValueAsStringOrNull(plugAbandonmentSchedule.getEarliestStartYear()),
        StringDisplayUtil.getValueAsStringOrNull(plugAbandonmentSchedule.getLatestCompletionYear())
    ));

    var plugAbandonmentWells = plugAbandonmentWellService.getPlugAbandonmentWells(plugAbandonmentSchedule);
    form.setWells(plugAbandonmentWells.stream()
        .map(plugAbandonmentWell -> plugAbandonmentWell.getWellbore().getId())
        .collect(Collectors.toList()));

    return form;
  }

  public boolean isValid(PlugAbandonmentSchedule plugAbandonmentSchedule, ValidationType validationType) {
    var form = getForm(plugAbandonmentSchedule);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  public PlugAbandonmentSchedule getPlugAbandonmentScheduleOrError(Integer plugAbandonmentScheduleId, ProjectDetail projectDetail) {
    return plugAbandonmentScheduleRepository.findByIdAndProjectDetail(plugAbandonmentScheduleId, projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format(
                "No PlugAbandonmentSchedule found with id %s for ProjectDetail with id %s",
                plugAbandonmentScheduleId,
                projectDetail != null ? projectDetail.getId() : "null"
            )
        ));
  }

  public List<PlugAbandonmentSchedule> getPlugAbandonmentSchedulesForProjectDetail(ProjectDetail projectDetail) {
    return plugAbandonmentScheduleRepository.findByProjectDetailOrderByIdAsc(projectDetail);
  }

  public List<PlugAbandonmentSchedule> getPlugAbandonmentSchedulesByProjectAndVersion(Project project, Integer version) {
    return plugAbandonmentScheduleRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version);
  }

  @Override
  public boolean isComplete(ProjectDetail projectDetail) {
    var plugAbandonmentSchedules = getPlugAbandonmentSchedulesForProjectDetail(projectDetail);
    return !plugAbandonmentSchedules.isEmpty() && plugAbandonmentSchedules.stream()
        .allMatch(plugAbandonmentSchedule -> isValid(plugAbandonmentSchedule, ValidationType.FULL));
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.WELLS);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    var plugAbandonmentSchedules = getPlugAbandonmentSchedulesForProjectDetail(projectDetail);
    plugAbandonmentWellService.deletePlugAbandonmentScheduleWells(plugAbandonmentSchedules);
    plugAbandonmentScheduleRepository.deleteAll(plugAbandonmentSchedules);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntitiesAndSetNewParent(
        getPlugAbandonmentSchedulesForProjectDetail(fromDetail),
        toDetail,
        PlugAbandonmentSchedule.class
    );

    // TODO: PAT-451
  }

  public ModelAndView getPlugAbandonmentScheduleModelAndView(Integer projectId,
                                                             PlugAbandonmentScheduleForm form) {
    return getPlugAbandonmentScheduleModelAndView(
        projectId,
        form,
        plugAbandonmentWellService.getWellboreViewsFromFormSorted(form)
    );
  }

  public ModelAndView getPlugAbandonmentScheduleModelAndView(Integer projectId,
                                                             PlugAbandonmentScheduleForm form,
                                                             PlugAbandonmentSchedule plugAbandonmentSchedule) {
    return getPlugAbandonmentScheduleModelAndView(
        projectId,
        form,
        plugAbandonmentWellService.getWellboreViewsFromScheduleSorted(plugAbandonmentSchedule)
    );
  }

  public ModelAndView getPlugAbandonmentScheduleModelAndView(Integer projectId,
                                                             PlugAbandonmentScheduleForm form,
                                                             List<WellboreView> wellboreViews) {
    var modelAndView = new ModelAndView(TEMPLATE_PATH)
        .addObject("form", form)
        .addObject("pageName", PlugAbandonmentScheduleController.FORM_PAGE_NAME)
        .addObject("alreadyAddedWells", wellboreViews)
        .addObject("wellsRestUrl", getWellboreRestUrl());

    breadcrumbService.fromPlugAbandonmentSchedule(projectId, modelAndView, PlugAbandonmentScheduleController.FORM_PAGE_NAME);

    return modelAndView;
  }

  public ModelAndView removePlugAbandonmentScheduleModelAndView(Integer projectId,
                                                                PlugAbandonmentScheduleView plugAbandonmentScheduleView) {
    var modelAndView = new ModelAndView(REMOVE_TEMPLATE_PATH)
        .addObject("plugAbandonmentScheduleView", plugAbandonmentScheduleView)
        .addObject("cancelUrl",
            ReverseRouter.route(on(PlugAbandonmentScheduleController.class).viewPlugAbandonmentSchedules(projectId, null)))
        .addObject("pageName", PlugAbandonmentScheduleController.REMOVE_PAGE_NAME);

    breadcrumbService.fromPlugAbandonmentSchedule(projectId, modelAndView, PlugAbandonmentScheduleController.REMOVE_PAGE_NAME);

    return modelAndView;
  }
}
