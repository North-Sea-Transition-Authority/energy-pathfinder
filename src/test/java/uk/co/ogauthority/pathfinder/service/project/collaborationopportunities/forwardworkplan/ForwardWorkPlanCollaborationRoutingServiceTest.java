package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetup;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationCompletionForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetupForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationRoutingServiceTest {

  @Mock
  private ForwardWorkPlanCollaborationOpportunityService collaborationOpportunityService;

  @Mock
  private ForwardWorkPlanCollaborationOpportunityModelService collaborationOpportunityModelService;

  @Mock
  private ForwardWorkPlanCollaborationSetupService collaborationSetupService;

  @Mock
  private ForwardWorkPlanCollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService;

  @Mock
  private ForwardWorkPlanCollaborationCompletionService collaborationCompletionService;

  private ProjectDetail projectDetail;

  private ForwardWorkPlanCollaborationRoutingService forwardWorkPlanCollaborationRoutingService;

  @Before
  public void setup() {

    projectDetail = ProjectUtil.getProjectDetails();

    forwardWorkPlanCollaborationRoutingService = new ForwardWorkPlanCollaborationRoutingService(
        collaborationOpportunityService,
        collaborationOpportunityModelService,
        collaborationSetupService,
        collaborationOpportunitiesSummaryService,
        collaborationCompletionService
    );
  }

  @Test
  public void getCollaborationOpportunitySetupRoute_whenCollaborationsToAddNotAnswered_thenSetupRoute() {

    final var setupForm = new ForwardWorkPlanCollaborationSetupForm();
    setupForm.setHasCollaborationsToAdd(null);

    when(collaborationSetupService.getCollaborationSetupFormFromDetail(projectDetail)).thenReturn(setupForm);

    final var expectedModelAndView = getDummyModelAndView("whenCollaborationsToAddNotAnswered");

    when(collaborationOpportunityModelService.getCollaborationSetupModelAndView(
        projectDetail,
        setupForm
    )).thenReturn(expectedModelAndView);

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getCollaborationOpportunitySetupRoute(
        projectDetail
    );

    assertThat(resultingRoute).isEqualTo(expectedModelAndView);
  }

  @Test
  public void getCollaborationOpportunitySetupRoute_whenNoCollaborationsToAdd_thenSetupRoute() {

    final var setupForm = new ForwardWorkPlanCollaborationSetupForm();
    setupForm.setHasCollaborationsToAdd(false);

    when(collaborationSetupService.getCollaborationSetupFormFromDetail(projectDetail)).thenReturn(setupForm);

    final var expectedModelAndView = getDummyModelAndView("whenNoCollaborationsToAdd");

    when(collaborationOpportunityModelService.getCollaborationSetupModelAndView(
        projectDetail,
        setupForm
    )).thenReturn(expectedModelAndView);

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getCollaborationOpportunitySetupRoute(
        projectDetail
    );

    assertThat(resultingRoute).isEqualTo(expectedModelAndView);
  }

  @Test
  public void getCollaborationOpportunitySetupRoute_whenCollaborationsToAddAndNoCollaborationsAdded_thenSetupRoute() {

    final var setupForm = new ForwardWorkPlanCollaborationSetupForm();
    setupForm.setHasCollaborationsToAdd(true);

    when(collaborationSetupService.getCollaborationSetupFormFromDetail(projectDetail)).thenReturn(setupForm);

    final var expectedModelAndView = getDummyModelAndView("whenCollaborationsToAddAndNoCollaborationsAdded");

    when(collaborationOpportunityModelService.getCollaborationSetupModelAndView(
        projectDetail,
        setupForm
    )).thenReturn(expectedModelAndView);

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getCollaborationOpportunitySetupRoute(
        projectDetail
    );

    assertThat(resultingRoute).isEqualTo(expectedModelAndView);
  }

  @Test
  public void getCollaborationOpportunitySetupRoute_whenCollaborationsToAddAndCollaborationsAdded_thenSummaryRoute() {

    final var setupForm = new ForwardWorkPlanCollaborationSetupForm();
    setupForm.setHasCollaborationsToAdd(true);

    when(collaborationSetupService.getCollaborationSetupFormFromDetail(projectDetail)).thenReturn(setupForm);

    when(collaborationOpportunityService.getOpportunitiesForDetail(projectDetail)).thenReturn(
        List.of(new ForwardWorkPlanCollaborationOpportunity())
    );

    final var expectedModelAndView = ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class).viewCollaborationOpportunities(
        projectDetail.getProject().getId(),
        null
    ));

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getCollaborationOpportunitySetupRoute(
        projectDetail
    ).getViewName();

    assertThat(resultingRoute).isEqualTo(expectedModelAndView.getViewName());
  }

  @Test
  public void getPostSaveUpcomingCollaborationsSetupRoute_whenCollaborationsToAdd_thenAddRoute() {

    final var collaborationSetup = new ForwardWorkPlanCollaborationSetup();
    collaborationSetup.setHasCollaborationToAdd(true);

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getPostSaveUpcomingCollaborationsSetupRoute(
        collaborationSetup,
        projectDetail
    );

    final var expectedRoute = ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class)
        .addCollaborationOpportunity(projectDetail.getProject().getId(), null));

    assertThat(resultingRoute.getViewName()).isEqualTo(expectedRoute.getViewName());
  }

  @Test
  public void getPostSaveUpcomingCollaborationsSetupRoute_whenNoCollaborationsToAdd_thenTaskList() {

    final var collaborationSetup = new ForwardWorkPlanCollaborationSetup();
    collaborationSetup.setHasCollaborationToAdd(false);

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getPostSaveUpcomingCollaborationsSetupRoute(
        collaborationSetup,
        projectDetail
    );

    final var expectedRoute =  ReverseRouter.redirect(on(TaskListController.class)
        .viewTaskList(projectDetail.getProject().getId(), null));

    assertThat(resultingRoute.getViewName()).isEqualTo(expectedRoute.getViewName());
  }

  @Test
  public void getPostSaveUpcomingCollaborationsSetupRoute_whenCollaborationsToAddNotAnswered_thenTaskList() {

    final var collaborationSetup = new ForwardWorkPlanCollaborationSetup();
    collaborationSetup.setHasCollaborationToAdd(null);

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getPostSaveUpcomingCollaborationsSetupRoute(
        collaborationSetup,
        projectDetail
    );

    final var expectedRoute =  ReverseRouter.redirect(on(TaskListController.class)
        .viewTaskList(projectDetail.getProject().getId(), null));

    assertThat(resultingRoute.getViewName()).isEqualTo(expectedRoute.getViewName());
  }

  @Test
  public void getViewCollaborationsRoute_whenNoCollaborations_thenSetupRoute() {

    when(collaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(Collections.emptyList());

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getViewCollaborationsRoute(projectDetail);

    final var expectedRoute = ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class).getCollaborationOpportunitySetup(
        projectDetail.getProject().getId(),
        null,
        null
    ));

    assertThat(resultingRoute.getViewName()).isEqualTo(expectedRoute.getViewName());

    verify(collaborationCompletionService, times(1)).resetHasOtherCollaborationsToAdd(any(), any());
  }

  @Test
  public void getViewCollaborationsRoute_whenCollaborations_thenViewCollaborationRoute() {

    final var collaborationViews = List.of(new ForwardWorkPlanCollaborationOpportunityView());

    when(collaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(
        collaborationViews
    );

    when(collaborationCompletionService.getForwardWorkPlanCollaborationCompletionFormFromDetail(projectDetail))
        .thenReturn(new ForwardWorkPlanCollaborationCompletionForm());

    final var expectedRoute = getDummyModelAndView("whenCollaborations");

    when(collaborationOpportunityModelService.getViewCollaborationOpportunitiesModelAndView(
        eq(projectDetail),
        eq(ValidationResult.NOT_VALIDATED),
        eq(collaborationViews),
        any(),
        any()
    )).thenReturn(expectedRoute);

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getViewCollaborationsRoute(projectDetail);

    assertThat(resultingRoute).isEqualTo(expectedRoute);

    verify(collaborationCompletionService, never()).resetHasOtherCollaborationsToAdd(any(), any());

  }

  @Test
  public void getAddCollaborationOpportunityRoute_whenHasCollaborationsToAdd_thenAddRoute() {

    final var setupForm = new ForwardWorkPlanCollaborationSetupForm();
    setupForm.setHasCollaborationsToAdd(true);

    when(collaborationSetupService.getCollaborationSetupFormFromDetail(projectDetail)).thenReturn(setupForm);

    final var expectedRoute = getDummyModelAndView("whenHasCollaborationsToAdd");

    when(collaborationOpportunityModelService.getCollaborationOpportunityModelAndView(
        any(),
        any(),
        anyInt()
    )).thenReturn(expectedRoute);

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getAddCollaborationOpportunityRoute(
        expectedRoute,
        new ForwardWorkPlanCollaborationOpportunityForm(),
        projectDetail
    );

    assertThat(resultingRoute).isEqualTo(expectedRoute);
  }

  @Test
  public void getAddCollaborationOpportunityRoute_whenNoCollaborationsToAdd_thenSetupRoute() {

    final var setupForm = new ForwardWorkPlanCollaborationSetupForm();
    setupForm.setHasCollaborationsToAdd(false);

    when(collaborationSetupService.getCollaborationSetupFormFromDetail(projectDetail)).thenReturn(setupForm);

    final var expectedRoute = ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class).getCollaborationOpportunitySetup(
        projectDetail.getProject().getId(),
        null,
        null
    ));

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getAddCollaborationOpportunityRoute(
        expectedRoute,
        new ForwardWorkPlanCollaborationOpportunityForm(),
        projectDetail
    );

    assertThat(resultingRoute.getViewName()).isEqualTo(expectedRoute.getViewName());
  }

  @Test
  public void getAddCollaborationOpportunityRoute_whenCollaborationsToAddNotAnswered_thenSetupRoute() {

    final var setupForm = new ForwardWorkPlanCollaborationSetupForm();
    setupForm.setHasCollaborationsToAdd(null);

    when(collaborationSetupService.getCollaborationSetupFormFromDetail(projectDetail)).thenReturn(setupForm);

    final var expectedRoute = ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class).getCollaborationOpportunitySetup(
        projectDetail.getProject().getId(),
        null,
        null
    ));

    final var resultingRoute = forwardWorkPlanCollaborationRoutingService.getAddCollaborationOpportunityRoute(
        expectedRoute,
        new ForwardWorkPlanCollaborationOpportunityForm(),
        projectDetail
    );

    assertThat(resultingRoute.getViewName()).isEqualTo(expectedRoute.getViewName());

  }

  private ModelAndView getDummyModelAndView(String viewName) {
    return new ModelAndView(viewName);
  }
}