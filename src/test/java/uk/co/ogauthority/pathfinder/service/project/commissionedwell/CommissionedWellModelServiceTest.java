package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.commissionedwell.CommissionedWellController;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellForm;
import uk.co.ogauthority.pathfinder.model.view.commissionedwell.CommissionedWellScheduleView;
import uk.co.ogauthority.pathfinder.model.view.wellbore.WellboreView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.WellboreTestUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@ExtendWith(MockitoExtension.class)
class CommissionedWellModelServiceTest {

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private CommissionedWellService commissionedWellService;

  private CommissionedWellModelService commissionedWellModelService;

  @BeforeEach
  void setup() {
    commissionedWellModelService = new CommissionedWellModelService(
        breadcrumbService,
        commissionedWellService
    );
  }

  @Test
  void getViewCommissionedWellsModelAndView_whenViewsValid_verifyModelProperties() {

    var projectDetail = ProjectUtil.getProjectDetails();
    var projectId = projectDetail.getProject().getId();

    var commissionedWellScheduleViews = List.of(
        new CommissionedWellScheduleView()
    );

    var resultingModelProperties = commissionedWellModelService.getViewCommissionedWellsModelAndView(
        projectDetail,
        commissionedWellScheduleViews,
        ValidationResult.VALID
    );

    assertCommonGetViewCommissionedWellsModelAndViewProperties(
        resultingModelProperties,
        projectId,
        commissionedWellScheduleViews
    );

    assertThat(resultingModelProperties.getModel()).contains(
        entry("errorList", Collections.emptyList())
    );
  }

  @Test
  void getViewCommissionedWellsModelAndView_whenViewsInvalidModelProperties() {

    var projectDetail = ProjectUtil.getProjectDetails();
    var projectId = projectDetail.getProject().getId();

    var invalidCommissionedWellScheduleView = new CommissionedWellScheduleView();
    invalidCommissionedWellScheduleView.setDisplayOrder(1);
    invalidCommissionedWellScheduleView.setIsValid(false);

    var commissionedWellScheduleViews = List.of(
        invalidCommissionedWellScheduleView
    );

    var resultingModelProperties = commissionedWellModelService.getViewCommissionedWellsModelAndView(
        projectDetail,
        commissionedWellScheduleViews,
        ValidationResult.INVALID
    );

    assertCommonGetViewCommissionedWellsModelAndViewProperties(
        resultingModelProperties,
        projectId,
        commissionedWellScheduleViews
    );

    assertThat(resultingModelProperties.getModel()).contains(
        entry("errorList", List.of(
            new ErrorItem(
                invalidCommissionedWellScheduleView.getDisplayOrder(),
                String.format(CommissionedWellModelService.ERROR_FIELD_NAME, invalidCommissionedWellScheduleView.getDisplayOrder()),
                String.format(CommissionedWellModelService.ERROR_MESSAGE, invalidCommissionedWellScheduleView.getDisplayOrder())
            )
        ))
    );
  }

  @Test
  void getCommissionedWellModelAndView_verifyModelProperties() {

    var projectDetail = ProjectUtil.getProjectDetails();
    var projectId = projectDetail.getProject().getId();

    var expectedWellboreUrl = "wellbore-url";
    var expectedCommissionedWellForm = new CommissionedWellForm();
    
    when(commissionedWellService.getWellboreRestUrl()).thenReturn(expectedWellboreUrl);

    var wellboreViewList = List.of(new WellboreView(WellboreTestUtil.createWellbore(), true));

    when(commissionedWellService.getWellboreViewsFromForm(expectedCommissionedWellForm))
        .thenReturn(wellboreViewList);

    var resultingModelProperties = commissionedWellModelService.getCommissionedWellModelAndView(
        projectDetail,
        expectedCommissionedWellForm
    );

    assertThat(resultingModelProperties.getViewName()).isEqualTo(CommissionedWellModelService.FORM_TEMPLATE_PATH);
    assertThat(resultingModelProperties.getModel()).containsExactly(
        entry("pageName", CommissionedWellController.FORM_PAGE_NAME),
        entry("wellsRestUrl", expectedWellboreUrl),
        entry("alreadyAddedWells", wellboreViewList),
        entry("form", expectedCommissionedWellForm)
    );

    verify(breadcrumbService, times(1)).fromCommissionedWells(
        projectId,
        resultingModelProperties,
        CommissionedWellController.FORM_PAGE_NAME
    );
  }

  @Test
  void getRemoveCommissionedWellScheduleModelAndView_verifyExpectedModelProperties() {

    var projectId = 10;
    var commissionedWellScheduleView = new CommissionedWellScheduleView();

    var resultingModelProperties = commissionedWellModelService.getRemoveCommissionedWellScheduleModelAndView(
        projectId,
        commissionedWellScheduleView
    );

    assertThat(resultingModelProperties.getViewName()).isEqualTo(CommissionedWellModelService.REMOVE_TEMPLATE_PATH);
    assertThat(resultingModelProperties.getModel()).containsExactly(
        entry("commissionedWellScheduleView", commissionedWellScheduleView),
        entry("cancelUrl", ReverseRouter.route(on(CommissionedWellController.class).viewWellsToCommission(projectId, null))),
        entry("pageName", CommissionedWellController.REMOVE_PAGE_NAME)
    );

    verify(breadcrumbService, times(1)).fromCommissionedWells(
        projectId,
        resultingModelProperties,
        CommissionedWellController.REMOVE_PAGE_NAME
    );
  }

  private void assertCommonGetViewCommissionedWellsModelAndViewProperties(ModelAndView modelAndView,
                                                                          int projectId,
                                                                          List<CommissionedWellScheduleView> commissionedWellScheduleViews) {

    assertThat(modelAndView.getViewName()).isEqualTo(CommissionedWellModelService.SUMMARY_TEMPLATE_PATH);

    var model = modelAndView.getModel();

    assertThat(model).containsOnlyKeys(
        "pageName",
        "addCommissionedWellUrl",
        "projectSetupUrl",
        "backToTaskListUrl",
        "commissionedWellScheduleViews",
        "errorList"
    );

    assertThat(model).contains(
        entry("pageName", CommissionedWellController.SUMMARY_PAGE_NAME),
        entry(
            "addCommissionedWellUrl",
            ReverseRouter.route(on(CommissionedWellController.class).addCommissioningSchedule(projectId, null))
        ),
        entry("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId)),
        entry("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId)),
        entry("commissionedWellScheduleViews", commissionedWellScheduleViews)
    );

    verify(breadcrumbService, times(1)).fromTaskList(
        projectId,
        modelAndView,
        CommissionedWellController.TASK_LIST_NAME
    );
  }
}