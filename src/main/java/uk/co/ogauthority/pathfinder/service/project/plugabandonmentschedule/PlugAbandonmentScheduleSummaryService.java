package uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule.PlugAbandonmentScheduleController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.plugabandonmentschedule.PlugAbandonmentScheduleView;
import uk.co.ogauthority.pathfinder.model.view.plugabandonmentschedule.PlugAbandonmentScheduleViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class PlugAbandonmentScheduleSummaryService {

  public static final String SUMMARY_TEMPLATE_PATH = "project/plugabandonmentschedule/plugAbandonmentScheduleFormSummary";

  public static final String ERROR_FIELD_NAME = "plug-abandonment-schedule-%d";
  public static final String ERROR_MESSAGE = "Plug and abandonment schedule %d is incomplete";
  public static final String EMPTY_LIST_ERROR = "You must add at least one plug and abandonment schedule";
  
  private final PlugAbandonmentScheduleService plugAbandonmentScheduleService;
  private final PlugAbandonmentWellService plugAbandonmentWellService;
  private final BreadcrumbService breadcrumbService;
  
  @Autowired
  public PlugAbandonmentScheduleSummaryService(PlugAbandonmentScheduleService plugAbandonmentScheduleService,
                                               PlugAbandonmentWellService plugAbandonmentWellService,
                                               BreadcrumbService breadcrumbService) {
    this.plugAbandonmentScheduleService = plugAbandonmentScheduleService;
    this.plugAbandonmentWellService = plugAbandonmentWellService;
    this.breadcrumbService = breadcrumbService;
  }
  
  public List<PlugAbandonmentScheduleView> getPlugAbandonmentScheduleSummaryViews(ProjectDetail projectDetail) {
    return constructPlugAbandonmentScheduleViews(projectDetail, ValidationType.NO_VALIDATION);
  }

  public List<PlugAbandonmentScheduleView> getPlugAbandonmentScheduleSummaryViewsByProjectAndVersion(Project project,
                                                                                                     Integer version) {
    return constructPlugAbandonmentScheduleViews(
        plugAbandonmentScheduleService.getPlugAbandonmentSchedulesByProjectAndVersion(project, version),
        ValidationType.NO_VALIDATION
    );
  }

  public PlugAbandonmentScheduleView getPlugAbandonmentScheduleSummaryView(Integer plugAbandonmentScheduleId,
                                                                           ProjectDetail projectDetail,
                                                                           Integer displayOrder) {
    var plugAbandonmentSchedule = plugAbandonmentScheduleService.getPlugAbandonmentScheduleOrError(
        plugAbandonmentScheduleId,
        projectDetail
    );
    var plugAbandonmentWells = plugAbandonmentWellService.getPlugAbandonmentWells(plugAbandonmentSchedule);
    return PlugAbandonmentScheduleViewUtil.from(plugAbandonmentSchedule, plugAbandonmentWells, displayOrder);
  }

  public List<PlugAbandonmentScheduleView> getValidatedPlugAbandonmentScheduleSummaryViews(ProjectDetail projectDetail) {
    return constructPlugAbandonmentScheduleViews(projectDetail, ValidationType.FULL);
  }

  public List<ErrorItem> getPlugAbandonmentScheduleViewErrors(List<PlugAbandonmentScheduleView> plugAbandonmentScheduleViews) {
    return SummaryUtil.getErrors(new ArrayList<>(plugAbandonmentScheduleViews), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public ValidationResult validateViews(List<PlugAbandonmentScheduleView> plugAbandonmentScheduleViews) {
    return SummaryUtil.validateViews(new ArrayList<>(plugAbandonmentScheduleViews));
  }

  private List<PlugAbandonmentScheduleView> constructPlugAbandonmentScheduleViews(ProjectDetail projectDetail,
                                                                                  ValidationType validationType) {
    return constructPlugAbandonmentScheduleViews(
        plugAbandonmentScheduleService.getPlugAbandonmentSchedulesForProjectDetail(projectDetail),
        validationType
    );
  }

  private List<PlugAbandonmentScheduleView> constructPlugAbandonmentScheduleViews(List<PlugAbandonmentSchedule> plugAbandonmentSchedules,
                                                                                  ValidationType validationType) {
    return IntStream.range(0, plugAbandonmentSchedules.size())
        .mapToObj(index -> {

          PlugAbandonmentScheduleView plugAbandonmentScheduleView;
          var plugAbandonmentSchedule = plugAbandonmentSchedules.get(index);
          var plugAbandonmentWells = plugAbandonmentWellService.getPlugAbandonmentWells(plugAbandonmentSchedule);
          var displayIndex = index + 1;

          if (validationType.equals(ValidationType.NO_VALIDATION)) {
            plugAbandonmentScheduleView = PlugAbandonmentScheduleViewUtil.from(
                plugAbandonmentSchedule,
                plugAbandonmentWells,
                displayIndex
            );
          } else {
            var isValid = plugAbandonmentScheduleService.isValid(plugAbandonmentSchedule, validationType);
            plugAbandonmentScheduleView = PlugAbandonmentScheduleViewUtil.from(
                plugAbandonmentSchedule,
                plugAbandonmentWells,
                displayIndex,
                isValid
            );
          }

          return plugAbandonmentScheduleView;

        })
        .collect(Collectors.toList());
  }

  public boolean canShowInTaskList(ProjectDetail detail) {
    return plugAbandonmentScheduleService.canShowInTaskList(detail);
  }

  public ModelAndView getPlugAbandonmentScheduleSummaryModelAndView(Integer projectId,
                                                                    List<PlugAbandonmentScheduleView> plugAbandonmentScheduleViews,
                                                                    ValidationResult validationResult) {
    var modelAndView = new ModelAndView(SUMMARY_TEMPLATE_PATH)
        .addObject("pageName", PlugAbandonmentScheduleController.SUMMARY_PAGE_NAME)
        .addObject("addPlugAbandonmentScheduleUrl",
            ReverseRouter.route(on(PlugAbandonmentScheduleController.class).addPlugAbandonmentSchedule(projectId, null))
        )
        .addObject("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectId)
        )
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId))
        .addObject("plugAbandonmentScheduleViews", plugAbandonmentScheduleViews)
        .addObject("errorList",
            validationResult.equals(ValidationResult.INVALID)
                ? getPlugAbandonmentScheduleViewErrors(plugAbandonmentScheduleViews)
                : null
        );

    breadcrumbService.fromTaskList(projectId, modelAndView, PlugAbandonmentScheduleController.TASK_LIST_NAME);

    return modelAndView;
  }
}
