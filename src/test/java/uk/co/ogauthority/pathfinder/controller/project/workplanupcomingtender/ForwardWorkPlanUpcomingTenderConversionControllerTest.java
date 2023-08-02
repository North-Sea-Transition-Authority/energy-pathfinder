package uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.mvc.ReverseRouter.route;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderConversionForm;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.ForwardWorkPlanUpcomingTenderView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderConversionService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderSummaryService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ForwardWorkPlanUpcomingTenderConversionController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class ForwardWorkPlanUpcomingTenderConversionControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer UPCOMING_TENDER_ID = 1;
  private static final Integer DISPLAY_ORDER = 1;
  private static final Class<ForwardWorkPlanUpcomingTenderConversionController> CONTROLLER = ForwardWorkPlanUpcomingTenderConversionController.class;
  private static final Class<ForwardWorkPlanUpcomingTenderController> SUMMARY_CONTROLLER = ForwardWorkPlanUpcomingTenderController.class;
  private static final String VIEW_NAME = "project/workplanupcomingtender/convertForwardWorkPlanUpcomingTender";

  @MockBean
  private ForwardWorkPlanUpcomingTenderService upcomingTenderService;

  @MockBean
  private ForwardWorkPlanUpcomingTenderSummaryService upcomingTenderSummaryService;

  @MockBean
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @MockBean
  private ForwardWorkPlanUpcomingTenderConversionService conversionService;

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);
  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
  private final ForwardWorkPlanUpcomingTender upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
  private final ForwardWorkPlanUpcomingTenderView upcomingTenderView = ForwardWorkPlanUpcomingTenderUtil.getView(DISPLAY_ORDER, true);

  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);
  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.FORWARD_WORK_PLAN);
  private final Set<ProjectPermission> requiredPermissions = ProjectControllerTesterService.PROJECT_CREATE_PERMISSION_SET;
  private ProjectControllerTesterService projectControllerTesterService;

  @Before
  public void setUp() {
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(any(), any())).thenReturn(true);
    when(upcomingTenderService.getOrError(UPCOMING_TENDER_ID)).thenReturn(upcomingTender);

    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUser)).thenReturn(true);
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(eq(projectDetail), any())).thenReturn(true);
    when(upcomingTenderSummaryService.getUpcomingTenderView(upcomingTender, DISPLAY_ORDER)).thenReturn(upcomingTenderView);

    projectControllerTesterService = new ProjectControllerTesterService(
        mockMvc,
        projectOperatorService,
        projectContributorsCommonService,
        teamService
    );
  }

  @Test
  public void convertUpcomingTenderConfirm() throws Exception {
    var modelAndView = mockMvc.perform(
            get(route(on(CONTROLLER).convertUpcomingTenderConfirm(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null)))
                .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertThat(model)
        .containsEntry("view", upcomingTenderView)
        .containsEntry("cancelUrl", ReverseRouter.route(on(SUMMARY_CONTROLLER).viewUpcomingTenders(PROJECT_ID, null)))
        .containsKeys("form");
  }

  @Test
  public void convertUpcomingTenderConfirm_projectContextSmokeTest() {
    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).convertUpcomingTenderConfirm(
            PROJECT_ID,
            UPCOMING_TENDER_ID,
            DISPLAY_ORDER,
            null
        ),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void convertUpcomingTender_validForm() throws Exception {
    var form = new UpcomingTenderConversionForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(conversionService.validate(
        any(UpcomingTenderConversionForm.class),
        any(BindingResult.class))
    ).thenReturn(bindingResult);

    mockMvc.perform(
        post(route(on(CONTROLLER)
            .convertUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null, null, null)))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl(route(on(SUMMARY_CONTROLLER).viewUpcomingTenders(PROJECT_ID, null))));

    verify(conversionService).validate(any(), any());
    verify(conversionService).convertUpcomingTenderToAwardedContract(
        any(ForwardWorkPlanUpcomingTender.class),
        any(UpcomingTenderConversionForm.class)
    );
  }

  @Test
  public void convertUpcomingTender_invalidForm() throws Exception {
    var form = new UpcomingTenderConversionForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default Message"));
    when(conversionService.validate(
        any(UpcomingTenderConversionForm.class),
        any(BindingResult.class))
    ).thenReturn(bindingResult);

    mockMvc.perform(
            post(route(on(CONTROLLER)
                .convertUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null, null, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf()))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name(VIEW_NAME));

    verify(conversionService).validate(any(), any());
    verify(conversionService, never()).convertUpcomingTenderToAwardedContract(any(), any());
  }

  @Test
  public void convertUpcomingTender_projectContextSmokeTest() {
    var form = new UpcomingTenderConversionForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(conversionService.validate(
        any(UpcomingTenderConversionForm.class),
        any(BindingResult.class))
    ).thenReturn(bindingResult);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).convertUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null, null, null),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }
}
