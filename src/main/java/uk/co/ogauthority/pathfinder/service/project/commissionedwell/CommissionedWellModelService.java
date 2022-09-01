package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.commissionedwell.CommissionedWellController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellForm;
import uk.co.ogauthority.pathfinder.model.view.commissionedwell.CommissionedWellScheduleView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class CommissionedWellModelService {

  static final String SUMMARY_TEMPLATE_PATH = "project/commissionedwell/commissionedWellFormSummary";
  static final String FORM_TEMPLATE_PATH = "project/commissionedwell/commissionedWellForm";
  static final String REMOVE_TEMPLATE_PATH = "project/commissionedwell/removeCommissionedWellSchedule";

  static final String ERROR_FIELD_NAME = "commissioned-well-schedule-%d";
  static final String ERROR_MESSAGE = "Well commissioning schedule %d is incomplete";
  static final String EMPTY_LIST_ERROR = "You must add at least one well commissioning schedule";

  private final BreadcrumbService breadcrumbService;
  private final CommissionedWellService commissionedWellService;

  @Autowired
  public CommissionedWellModelService(BreadcrumbService breadcrumbService,
                                      CommissionedWellService commissionedWellService) {
    this.breadcrumbService = breadcrumbService;
    this.commissionedWellService = commissionedWellService;
  }

  public ModelAndView getViewCommissionedWellsModelAndView(ProjectDetail projectDetail,
                                                           List<CommissionedWellScheduleView> commissionedWellScheduleViews,
                                                           ValidationResult validationResult) {

    var projectId = projectDetail.getProject().getId();

    var modelAndView = new ModelAndView(SUMMARY_TEMPLATE_PATH)
        .addObject("pageName", CommissionedWellController.SUMMARY_PAGE_NAME)
        .addObject(
            "addCommissionedWellUrl",
            ReverseRouter.route(on(CommissionedWellController.class).addCommissioningSchedule(projectId, null))
        )
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId))
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId))
        .addObject("commissionedWellScheduleViews", commissionedWellScheduleViews)
        .addObject("errorList",
            validationResult.equals(ValidationResult.INVALID)
                ? getCommissionedWellScheduleViewErrors(commissionedWellScheduleViews)
                : Collections.emptyList()
        );

    breadcrumbService.fromTaskList(projectId, modelAndView, CommissionedWellController.TASK_LIST_NAME);

    return modelAndView;
  }

  public ModelAndView getCommissionedWellModelAndView(ProjectDetail projectDetail,
                                                      CommissionedWellForm commissionedWellForm) {

    var projectId = projectDetail.getProject().getId();

    var modelAndView = new ModelAndView(FORM_TEMPLATE_PATH)
        .addObject("pageName", CommissionedWellController.FORM_PAGE_NAME)
        .addObject("wellsRestUrl", commissionedWellService.getWellboreRestUrl())
        .addObject("alreadyAddedWells", commissionedWellService.getWellboreViewsFromForm(commissionedWellForm))
        .addObject("form", commissionedWellForm);

    breadcrumbService.fromCommissionedWells(
        projectId,
        modelAndView,
        CommissionedWellController.FORM_PAGE_NAME
    );

    return modelAndView;
  }

  public ModelAndView getRemoveCommissionedWellScheduleModelAndView(Integer projectId,
                                                                    CommissionedWellScheduleView commissionedWellScheduleView) {
    var modelAndView = new ModelAndView(REMOVE_TEMPLATE_PATH)
        .addObject("commissionedWellScheduleView", commissionedWellScheduleView)
        .addObject("cancelUrl",
            ReverseRouter.route(on(CommissionedWellController.class).viewWellsToCommission(projectId, null)))
        .addObject("pageName", CommissionedWellController.REMOVE_PAGE_NAME);

    breadcrumbService.fromCommissionedWells(projectId, modelAndView, CommissionedWellController.REMOVE_PAGE_NAME);

    return modelAndView;
  }

  private List<ErrorItem> getCommissionedWellScheduleViewErrors(List<CommissionedWellScheduleView> commissionedWellScheduleViews) {
    return SummaryUtil.getErrors(new ArrayList<>(commissionedWellScheduleViews), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }
}
