package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.controller.rest.WorkPlanUpcomingTenderRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.WorkPlanUpcomingTenderContractBand;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderCompletionForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderSetupForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.WorkPlanUpcomingTenderView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationErrorOrderingService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class WorkPlanUpcomingTenderModelService {

  protected static final String SUMMARY_TEMPLATE_PATH = "project/workplanupcomingtender/workPlanUpcomingTenderFormSummary";
  protected static final String FORM_TEMPLATE_PATH = "project/workplanupcomingtender/workPlanUpcomingTender";
  protected static final String REMOVE_TEMPLATE_PATH = "project/workplanupcomingtender/removeWorkPlanUpcomingTender";
  protected static final String SETUP_TEMPLATE_PATH = "project/workplanupcomingtender/setupWorkPlanUpcomingTenders";

  protected static final String ERROR_FIELD_NAME = "upcoming-tender-%d";
  protected static final String ERROR_MESSAGE = "Upcoming tender %d is incomplete";
  protected static final String EMPTY_LIST_ERROR = "You must add at least one upcoming tender";

  private final BreadcrumbService breadcrumbService;
  private final SearchSelectorService searchSelectorService;
  private final ValidationErrorOrderingService validationErrorOrderingService;

  public WorkPlanUpcomingTenderModelService(BreadcrumbService breadcrumbService,
                                            SearchSelectorService searchSelectorService,
                                            ValidationErrorOrderingService validationErrorOrderingService) {
    this.breadcrumbService = breadcrumbService;
    this.searchSelectorService = searchSelectorService;
    this.validationErrorOrderingService = validationErrorOrderingService;
  }

  public ModelAndView getUpcomingTenderFormModelAndView(ProjectDetail projectDetail,
                                                        WorkPlanUpcomingTenderForm form) {
    var modelAndView = new ModelAndView(FORM_TEMPLATE_PATH)
        .addObject("pageNameSingular", WorkPlanUpcomingTenderController.PAGE_NAME_SINGULAR)
        .addObject("form", form)
        .addObject("preSelectedFunction", getPreSelectedFunction(form))
        .addObject("contractBands", WorkPlanUpcomingTenderContractBand.getAllAsMap())
        .addObject("departmentTenderRestUrl", SearchSelectorService.route(
            on(WorkPlanUpcomingTenderRestController.class).searchTenderDepartments(null)
        ))
        .addObject("contractTermPeriodDays", DurationPeriod.getEntryAsMap(DurationPeriod.DAYS))
        .addObject("contractTermPeriodWeeks", DurationPeriod.getEntryAsMap(DurationPeriod.WEEKS))
        .addObject("contractTermPeriodMonths", DurationPeriod.getEntryAsMap(DurationPeriod.MONTHS))
        .addObject("contractTermPeriodYears", DurationPeriod.getEntryAsMap(DurationPeriod.YEARS))
        .addObject("quarters", Quarter.getAllAsMap());

    breadcrumbService.fromWorkPlanUpcomingTenders(
        projectDetail.getProject().getId(),
        modelAndView,
        WorkPlanUpcomingTenderController.PAGE_NAME_SINGULAR
    );

    return modelAndView;
  }

  public ModelAndView getViewUpcomingTendersModelAndView(ProjectDetail projectDetail,
                                                         List<WorkPlanUpcomingTenderView> tenderViews,
                                                         ValidationResult validationResult,
                                                         ForwardWorkPlanTenderCompletionForm form,
                                                         BindingResult finaliseFormBindingResult) {

    final var projectId = projectDetail.getProject().getId();

    final var modelAndView = new ModelAndView(SUMMARY_TEMPLATE_PATH)
        .addObject("pageName", WorkPlanUpcomingTenderController.PAGE_NAME)
        .addObject("tenderViews", tenderViews)
        .addObject("isValid", validationResult.equals(ValidationResult.VALID))
        .addObject(
            "errorSummary",
            getSummaryViewErrors(tenderViews, validationResult, form, finaliseFormBindingResult)
        )
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId))
        .addObject("form", form);

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, projectDetail);

    breadcrumbService.fromTaskList(projectId, modelAndView, WorkPlanUpcomingTenderController.PAGE_NAME);

    return modelAndView;
  }

  public ModelAndView getRemoveUpcomingTenderConfirmModelAndView(
      Integer projectId,
      WorkPlanUpcomingTenderView workPlanUpcomingTenderView
  ) {
    var modelAndView = new ModelAndView(REMOVE_TEMPLATE_PATH)
        .addObject("view", workPlanUpcomingTenderView)
        .addObject("cancelUrl",
            ReverseRouter.route(on(WorkPlanUpcomingTenderController.class).viewUpcomingTenders(projectId, null))
        );

    breadcrumbService.fromWorkPlanUpcomingTenders(
        projectId,
        modelAndView,
        WorkPlanUpcomingTenderController.REMOVE_PAGE_NAME
    );

    return modelAndView;
  }

  public ModelAndView getUpcomingTenderSetupModelAndView(ProjectDetail projectDetail,
                                                         ForwardWorkPlanTenderSetupForm form) {

    final var projectId = projectDetail.getProject().getId();

    final var modelAndView = new ModelAndView(SETUP_TEMPLATE_PATH)
        .addObject("pageName", WorkPlanUpcomingTenderController.PAGE_NAME)
        .addObject("form", form)
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId));

    breadcrumbService.fromTaskList(
        projectId,
        modelAndView,
        WorkPlanUpcomingTenderController.PAGE_NAME
    );

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, projectDetail);

    return modelAndView;
  }

  protected Map<String, String> getPreSelectedFunction(WorkPlanUpcomingTenderForm form) {
    return searchSelectorService.getPreSelectedSearchSelectorValue(form.getDepartmentType(), Function.values());
  }

  protected List<ErrorItem> getErrors(List<WorkPlanUpcomingTenderView> views) {
    return SummaryUtil.getErrors(new ArrayList<>(views), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  private List<ErrorItem> getSummaryViewErrors(List<WorkPlanUpcomingTenderView> tenderViews,
                                               ValidationResult validationResult,
                                               ForwardWorkPlanTenderCompletionForm form,
                                               BindingResult bindingResult) {

    final var errorList = validationResult.equals(ValidationResult.INVALID)
        ? getErrors(tenderViews)
        : new ArrayList<ErrorItem>();

    final var formErrors = validationErrorOrderingService.getErrorItemsFromBindingResult(
        form,
        bindingResult,
        errorList.size() + 1 // offset form errors from view errors so error summary ordering is correct
    );

    errorList.addAll(formErrors);

    return errorList;
  }
}
