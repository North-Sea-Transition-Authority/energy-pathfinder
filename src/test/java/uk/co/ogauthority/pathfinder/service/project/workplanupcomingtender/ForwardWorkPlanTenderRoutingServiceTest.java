package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanTenderSetup;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderCompletionForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderSetupForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanTenderRoutingServiceTest {

  @Mock
  private ForwardWorkPlanUpcomingTenderModelService workPlanUpcomingTenderModelService;

  @Mock
  private ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;

  @Mock
  private ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  @Mock
  private ForwardWorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService;

  @Mock
  private ForwardWorkPlanTenderCompletionService forwardWorkPlanTenderCompletionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private ForwardWorkPlanTenderRoutingService forwardWorkPlanTenderRoutingService;

  @Before
  public void setup() {
    forwardWorkPlanTenderRoutingService = new ForwardWorkPlanTenderRoutingService(
        workPlanUpcomingTenderModelService,
        forwardWorkPlanTenderSetupService,
        workPlanUpcomingTenderService,
        workPlanUpcomingTenderSummaryService,
        forwardWorkPlanTenderCompletionService
    );
  }

  @Test
  public void getUpcomingTenderSetupRoute_whenSetupNotAnswered_thenSetupEndpoint() {

    final var emptySetupForm = new ForwardWorkPlanTenderSetupForm();

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupFormFromDetail(projectDetail))
        .thenReturn(emptySetupForm);

    final var expectedModelAndView = getDummyModelAndView("whenSetupNotAnswered");

    when(workPlanUpcomingTenderModelService.getUpcomingTenderSetupModelAndView(projectDetail, emptySetupForm))
        .thenReturn(expectedModelAndView);

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getUpcomingTenderSetupRoute(projectDetail);

    assertThat(resultingRoute).isEqualTo(expectedModelAndView);
  }

  @Test
  public void getUpcomingTenderSetupRoute_whenSetupIsNo_thenSetupEndpoint() {

    final var setupFormWithNoTendersToAdd = new ForwardWorkPlanTenderSetupForm();
    setupFormWithNoTendersToAdd.setHasTendersToAdd(false);

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupFormFromDetail(projectDetail))
        .thenReturn(setupFormWithNoTendersToAdd);

    final var expectedModelAndView = getDummyModelAndView("whenSetupIsNo");

    when(workPlanUpcomingTenderModelService.getUpcomingTenderSetupModelAndView(projectDetail, setupFormWithNoTendersToAdd))
        .thenReturn(expectedModelAndView);

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getUpcomingTenderSetupRoute(projectDetail);

    assertThat(resultingRoute).isEqualTo(expectedModelAndView);
  }

  @Test
  public void getUpcomingTenderSetupRoute_whenSetupIsYesAndNoTendersAdded_thenSetupEndpoint() {

    final var setupFormWithTendersToAdd = new ForwardWorkPlanTenderSetupForm();
    setupFormWithTendersToAdd.setHasTendersToAdd(true);

    when(workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail)).thenReturn(List.of());

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupFormFromDetail(projectDetail))
        .thenReturn(setupFormWithTendersToAdd);

    final var expectedModelAndView = getDummyModelAndView("whenSetupIsYesAndNoTendersAdded");

    when(workPlanUpcomingTenderModelService.getUpcomingTenderSetupModelAndView(projectDetail, setupFormWithTendersToAdd))
        .thenReturn(expectedModelAndView);

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getUpcomingTenderSetupRoute(projectDetail);

    assertThat(resultingRoute).isEqualTo(expectedModelAndView);

  }

  @Test
  public void getUpcomingTenderSetupRoute_whenSetupIsYesAndTendersAdded_thenViewTendersEndpoint() {

    final var setupFormWithTendersToAdd = new ForwardWorkPlanTenderSetupForm();
    setupFormWithTendersToAdd.setHasTendersToAdd(true);

    when(workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail)).thenReturn(
        List.of(ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail))
    );

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupFormFromDetail(projectDetail))
        .thenReturn(setupFormWithTendersToAdd);

    final var expectedRoute = ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).viewUpcomingTenders(
        projectDetail.getProject().getId(),
        null
    )).getViewName();

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getUpcomingTenderSetupRoute(projectDetail);

    assertThat(resultingRoute.getViewName()).isEqualTo(expectedRoute);
  }

  @Test
  public void getPostSaveUpcomingTenderSetupRoute_whenTendersToAdd_thenAddTenderRedirect() {

    final var forwardWorkPlanTenderSetup = new ForwardWorkPlanTenderSetup();
    forwardWorkPlanTenderSetup.setHasTendersToAdd(true);

    final var expectedRoute = ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).addUpcomingTender(
        projectDetail.getProject().getId(),
        null
    )).getViewName();

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getPostSaveUpcomingTenderSetupRoute(
        forwardWorkPlanTenderSetup,
        projectDetail
    ).getViewName();

    assertThat(resultingRoute).isEqualTo(expectedRoute);
  }

  @Test
  public void getPostSaveUpcomingTenderSetupRoute_whenNoTendersToAdd_thenTaskListRedirect() {

    final var forwardWorkPlanTenderSetup = new ForwardWorkPlanTenderSetup();
    forwardWorkPlanTenderSetup.setHasTendersToAdd(false);

    final var expectedRoute = ReverseRouter.redirect(on(TaskListController.class).viewTaskList(
        projectDetail.getProject().getId(),
        null
    )).getViewName();

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getPostSaveUpcomingTenderSetupRoute(
        forwardWorkPlanTenderSetup,
        projectDetail
    ).getViewName();

    assertThat(resultingRoute).isEqualTo(expectedRoute);
  }

  @Test
  public void getViewUpcomingTendersRoute_whenNoTendersAdded_thenTenderSetupRedirect() {

    when(workPlanUpcomingTenderSummaryService.getSummaryViews(projectDetail)).thenReturn(List.of());

    final var expectedRoute = ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).getUpcomingTenderSetup(
        projectDetail.getProject().getId(),
        null,
        null
    )).getViewName();

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getViewUpcomingTendersRoute(
        projectDetail
    ).getViewName();

    assertThat(resultingRoute).isEqualTo(expectedRoute);
  }

  @Test
  public void getViewUpcomingTendersRoute_whenTendersAdded_thenTenderViewEndpoint() {

    final var tenderViews = List.of(
        ForwardWorkPlanUpcomingTenderUtil.getView(1, true)
    );

    when(workPlanUpcomingTenderSummaryService.getSummaryViews(projectDetail)).thenReturn(tenderViews);

    final var expectedRoute = getDummyModelAndView("whenTendersAdded");

    final var form = new ForwardWorkPlanTenderCompletionForm();

    when(forwardWorkPlanTenderCompletionService.getForwardWorkPlanTenderCompletionFormFromDetail(projectDetail)).thenReturn(form);

    when(workPlanUpcomingTenderModelService.getViewUpcomingTendersModelAndView(
        projectDetail,
        tenderViews,
        ValidationResult.NOT_VALIDATED,
        form,
        ReverseRouter.emptyBindingResult()
    )).thenReturn(expectedRoute);

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getViewUpcomingTendersRoute(projectDetail);

    assertThat(resultingRoute).isEqualTo(expectedRoute);
  }

  @Test
  public void getAddUpcomingTenderRoute_whenTendersToAdd_thenAddTenderEndpoint() {

    final var setupFormWithTendersToAdd = new ForwardWorkPlanTenderSetupForm();
    setupFormWithTendersToAdd.setHasTendersToAdd(true);

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupFormFromDetail(projectDetail))
        .thenReturn(setupFormWithTendersToAdd);

    final var expectedRoute = getDummyModelAndView("whenTendersToAdd");

    when(workPlanUpcomingTenderModelService.getUpcomingTenderFormModelAndView(
        eq(projectDetail),
        any()
    )).thenReturn(expectedRoute);

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getAddUpcomingTenderRoute(projectDetail);

    assertThat(resultingRoute).isEqualTo(expectedRoute);
  }

  @Test
  public void getAddUpcomingTenderRoute_whenNoTendersToAdd_thenSetupRedirect() {

    final var setupFormWithNoTendersToAdd = new ForwardWorkPlanTenderSetupForm();
    setupFormWithNoTendersToAdd.setHasTendersToAdd(false);

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupFormFromDetail(projectDetail))
        .thenReturn(setupFormWithNoTendersToAdd);

    final var expectedRoute = ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).getUpcomingTenderSetup(
        projectDetail.getProject().getId(),
        null,
        null
    )).getViewName();

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getAddUpcomingTenderRoute(projectDetail);

    assertThat(resultingRoute.getViewName()).isEqualTo(expectedRoute);
  }

  @Test
  public void getAddUpcomingTenderRoute_whenSetupNotAnswered_thenSetupRedirect() {

    final var emptySetupForm = new ForwardWorkPlanTenderSetupForm();
    emptySetupForm.setHasTendersToAdd(null);

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupFormFromDetail(projectDetail))
        .thenReturn(emptySetupForm);

    final var expectedRoute = ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).getUpcomingTenderSetup(
        projectDetail.getProject().getId(),
        null,
        null
    )).getViewName();

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getAddUpcomingTenderRoute(projectDetail);

    assertThat(resultingRoute.getViewName()).isEqualTo(expectedRoute);

  }


  @Test
  public void getPostSaveUpcomingTendersRoute_whenHasOtherTendersToAdd_assertAddTenderRedirect() {

    final var forwardWorkPlanTenderSetup = new ForwardWorkPlanTenderSetup();
    forwardWorkPlanTenderSetup.setHasOtherTendersToAdd(true);

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getPostSaveUpcomingTendersRoute(
        forwardWorkPlanTenderSetup,
        projectDetail
    ).getViewName();

    final var expectedRoute = ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).addUpcomingTender(
        projectDetail.getProject().getId(),
        null
    )).getViewName();

    assertThat(resultingRoute).isEqualTo(expectedRoute);
  }

  @Test
  public void getPostSaveUpcomingTendersRoute_whenNoOtherTendersToAdd_assertTaskListRedirect() {

    final var forwardWorkPlanTenderSetup = new ForwardWorkPlanTenderSetup();
    forwardWorkPlanTenderSetup.setHasOtherTendersToAdd(false);

    final var resultingRoute = forwardWorkPlanTenderRoutingService.getPostSaveUpcomingTendersRoute(
        forwardWorkPlanTenderSetup,
        projectDetail
    ).getViewName();

    final var expectedRoute = ReverseRouter.redirect(on(TaskListController.class).viewTaskList(
        projectDetail.getProject().getId(),
        null
    )).getViewName();

    assertThat(resultingRoute).isEqualTo(expectedRoute);
  }

  private ModelAndView getDummyModelAndView(String viewName) {
    return new ModelAndView(viewName);
  }
}