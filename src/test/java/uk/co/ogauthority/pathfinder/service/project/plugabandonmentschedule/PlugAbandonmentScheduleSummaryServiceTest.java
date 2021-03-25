package uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.AbstractMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule.PlugAbandonmentScheduleController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.testutil.PlugAbandonmentScheduleTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class PlugAbandonmentScheduleSummaryServiceTest {

  @Mock
  private PlugAbandonmentScheduleService plugAbandonmentScheduleService;

  @Mock
  private PlugAbandonmentWellService plugAbandonmentWellService;

  @Mock
  private BreadcrumbService breadcrumbService;

  private PlugAbandonmentScheduleSummaryService plugAbandonmentScheduleSummaryService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  
  @Before
  public void setup() {
    plugAbandonmentScheduleSummaryService = new PlugAbandonmentScheduleSummaryService(
        plugAbandonmentScheduleService,
        plugAbandonmentWellService,
        breadcrumbService
    );
  }

  @Test
  public void getPlugAbandonmentScheduleSummaryViews_whenViews_thenReturnPopulatedList() {

    var plugAbandonmentSchedule = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();

    when(plugAbandonmentScheduleService.getPlugAbandonmentSchedulesForProjectDetail(projectDetail)).thenReturn(
        List.of(plugAbandonmentSchedule)
    );

    var results = plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    var plugAbandonmentScheduleView = results.get(0);
    assertThat(plugAbandonmentScheduleView.getId()).isEqualTo(plugAbandonmentSchedule.getId());
    assertThat(plugAbandonmentScheduleView.getProjectId()).isEqualTo(plugAbandonmentSchedule.getProjectDetail().getProject().getId());
    assertThat(plugAbandonmentScheduleView.isValid()).isTrue();
  }

  @Test
  public void getPlugAbandonmentScheduleSummaryViews_whenNoViews_thenReturnEmptyList() {

    when(plugAbandonmentScheduleService.getPlugAbandonmentSchedulesForProjectDetail(projectDetail)).thenReturn(
        List.of()
    );

    var results = plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryViews(projectDetail);
    assertThat(results).isEmpty();
  }

  @Test
  public void getPlugAbandonmentScheduleSummaryViewsByProjectAndVersion_whenViews_thenReturnPopulatedList() {
    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();
    var plugAbandonmentSchedule = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();

    when(plugAbandonmentScheduleService.getPlugAbandonmentSchedulesByProjectAndVersion(project, version)).thenReturn(
        List.of(plugAbandonmentSchedule)
    );

    var results = plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryViewsByProjectAndVersion(project, version);

    assertThat(results).hasSize(1);
    var plugAbandonmentScheduleView = results.get(0);
    assertThat(plugAbandonmentScheduleView.getId()).isEqualTo(plugAbandonmentSchedule.getId());
    assertThat(plugAbandonmentScheduleView.getProjectId()).isEqualTo(plugAbandonmentSchedule.getProjectDetail().getProject().getId());
    assertThat(plugAbandonmentScheduleView.isValid()).isTrue();
  }

  @Test
  public void getPlugAbandonmentScheduleSummaryViewsByProjectAndVersion_whenNoViews_thenReturnEmptyList() {
    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();

    when(plugAbandonmentScheduleService.getPlugAbandonmentSchedulesByProjectAndVersion(project, version)).thenReturn(
        List.of()
    );

    var results = plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryViewsByProjectAndVersion(project, version);
    assertThat(results).isEmpty();
  }

  @Test
  public void getPlugAbandonmentScheduleSummaryView_whenFound_thenViewReturned() {

    final var plugAbandonmentScheduleId = 1;
    var plugAbandonmentSchedule = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();

    when(plugAbandonmentScheduleService.getPlugAbandonmentScheduleOrError(plugAbandonmentScheduleId, projectDetail)).thenReturn(plugAbandonmentSchedule);

    var view = plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryView(
        plugAbandonmentScheduleId,
        projectDetail,
        1
    );

    assertThat(view.getProjectId()).isEqualTo(projectDetail.getProject().getId());
    assertThat(view.getValid()).isTrue();
  }

  @Test
  public void getValidatedPlugAbandonmentScheduleSummaryViews_whenValid_thenIsValidTrue() {

    var plugAbandonmentSchedule = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();

    when(plugAbandonmentScheduleService.isValid(plugAbandonmentSchedule, ValidationType.FULL)).thenReturn(true);

    when(plugAbandonmentScheduleService.getPlugAbandonmentSchedulesForProjectDetail(projectDetail)).thenReturn(
        List.of(plugAbandonmentSchedule)
    );

    var results = plugAbandonmentScheduleSummaryService.getValidatedPlugAbandonmentScheduleSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).isValid()).isTrue();
  }

  @Test
  public void getValidatedPlugAbandonmentScheduleSummaryViews_whenInvalid_thenIsValidFalse() {

    var plugAbandonmentSchedule = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();

    when(plugAbandonmentScheduleService.isValid(plugAbandonmentSchedule, ValidationType.FULL)).thenReturn(false);

    when(plugAbandonmentScheduleService.getPlugAbandonmentSchedulesForProjectDetail(projectDetail)).thenReturn(
        List.of(plugAbandonmentSchedule)
    );

    var results = plugAbandonmentScheduleSummaryService.getValidatedPlugAbandonmentScheduleSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).isValid()).isFalse();
  }

  @Test
  public void getValidatedPlugAbandonmentScheduleSummaryViews_whenNoViews_thenEmptyList() {

    when(plugAbandonmentScheduleService.getPlugAbandonmentSchedulesForProjectDetail(projectDetail)).thenReturn(
        List.of()
    );

    var results = plugAbandonmentScheduleSummaryService.getValidatedPlugAbandonmentScheduleSummaryViews(projectDetail);

    assertThat(results).isEmpty();
  }

  @Test
  public void getPlugAbandonmentScheduleViewErrors_whenErrors_thenErrorsReturned() {
    var plugAbandonmentScheduleViews = List.of(
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(1, true),
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(2, false),
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(3, false)
    );
    var errors = plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleViewErrors(plugAbandonmentScheduleViews);

    assertThat(errors.size()).isEqualTo(2);

    var plugAbandonmentScheduleView1 = errors.get(0);

    assertThat(plugAbandonmentScheduleView1.getDisplayOrder()).isEqualTo(2);
    assertThat(plugAbandonmentScheduleView1.getFieldName()).isEqualTo(String.format(PlugAbandonmentScheduleSummaryService.ERROR_FIELD_NAME, 2));
    assertThat(plugAbandonmentScheduleView1.getErrorMessage()).isEqualTo(String.format(PlugAbandonmentScheduleSummaryService.ERROR_MESSAGE, 2));

    var plugAbandonmentScheduleView2 = errors.get(1);
    assertThat(plugAbandonmentScheduleView2.getDisplayOrder()).isEqualTo(3);
    assertThat(plugAbandonmentScheduleView2.getFieldName()).isEqualTo(String.format(PlugAbandonmentScheduleSummaryService.ERROR_FIELD_NAME, 3));
    assertThat(plugAbandonmentScheduleView2.getErrorMessage()).isEqualTo(String.format(PlugAbandonmentScheduleSummaryService.ERROR_MESSAGE, 3));
  }

  @Test
  public void getPlugAbandonmentScheduleViewErrors_whenNoErrors_thenEmptyListReturned() {
    var plugAbandonmentScheduleViews = List.of(
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(1, true),
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(2, true)
    );
    var errors = plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleViewErrors(plugAbandonmentScheduleViews);

    assertThat(errors).isEmpty();
  }

  @Test
  public void validateViews_whenEmpty_thenInvalid() {
    assertThat(plugAbandonmentScheduleSummaryService.validateViews(List.of())).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void validateViews_whenInvalidView_thenInvalid() {
    var plugAbandonmentScheduleView = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(1, false);
    var validationResult = plugAbandonmentScheduleSummaryService.validateViews(List.of(plugAbandonmentScheduleView));
    assertThat(validationResult).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void validateViews_whenValidView_thenValid() {
    var plugAbandonmentScheduleView = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(1, true);
    var validationResult = plugAbandonmentScheduleSummaryService.validateViews(List.of(plugAbandonmentScheduleView));
    assertThat(validationResult).isEqualTo(ValidationResult.VALID);
  }

  @Test
  public void canShowInTaskList_whenCanShowInTaskList_thenTrue() {
    when(plugAbandonmentScheduleService.canShowInTaskList(projectDetail)).thenReturn(true);

    assertThat(plugAbandonmentScheduleSummaryService.canShowInTaskList(projectDetail)).isTrue();
  }

  @Test
  public void canShowInTaskList_whenCannotShowInTaskList_thenFalse() {
    when(plugAbandonmentScheduleService.canShowInTaskList(projectDetail)).thenReturn(false);

    assertThat(plugAbandonmentScheduleSummaryService.canShowInTaskList(projectDetail)).isFalse();
  }

  @Test
  public void getPlugAbandonmentScheduleSummaryModelAndView_whenInvalidValidationResult() {
    var projectId = projectDetail.getProject().getId();

    var plugAbandonmentScheduleViews = List.of(
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(),
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView()
    );

    var modelAndView = plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryModelAndView(
        projectId,
        plugAbandonmentScheduleViews,
        ValidationResult.INVALID
    );

    assertThat(modelAndView.getViewName()).isEqualTo(PlugAbandonmentScheduleSummaryService.SUMMARY_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("pageName", PlugAbandonmentScheduleController.SUMMARY_PAGE_NAME),
        entry("addPlugAbandonmentScheduleUrl",
            ReverseRouter.route(on(PlugAbandonmentScheduleController.class).addPlugAbandonmentSchedule(projectId, null))
        ),
        entry("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectId)
        ),
        entry("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId)),
        entry("plugAbandonmentScheduleViews", plugAbandonmentScheduleViews),
        entry("errorList", plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleViewErrors(plugAbandonmentScheduleViews))
    );

    verify(breadcrumbService, times(1)).fromTaskList(projectId, modelAndView, PlugAbandonmentScheduleController.TASK_LIST_NAME);
  }

  @Test
  public void getPlugAbandonmentScheduleSummaryModelAndView_whenValidValidationResult() {
    var projectId = projectDetail.getProject().getId();

    var plugAbandonmentScheduleViews = List.of(
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(),
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView()
    );

    var modelAndView = plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryModelAndView(
        projectId,
        plugAbandonmentScheduleViews,
        ValidationResult.VALID
    );

    assertThat(modelAndView.getViewName()).isEqualTo(PlugAbandonmentScheduleSummaryService.SUMMARY_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("pageName", PlugAbandonmentScheduleController.SUMMARY_PAGE_NAME),
        entry("addPlugAbandonmentScheduleUrl",
            ReverseRouter.route(on(PlugAbandonmentScheduleController.class).addPlugAbandonmentSchedule(projectId, null))
        ),
        entry("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectId)
        ),
        entry("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId)),
        entry("plugAbandonmentScheduleViews", plugAbandonmentScheduleViews),
        new AbstractMap.SimpleEntry<>("errorList", null)
    );

    verify(breadcrumbService, times(1)).fromTaskList(projectId, modelAndView, PlugAbandonmentScheduleController.TASK_LIST_NAME);
  }
}
