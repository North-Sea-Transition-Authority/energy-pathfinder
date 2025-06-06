package uk.co.ogauthority.pathfinder.controller.project.upcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.infrastructure.InfrastructureAwardedContractController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.notificationbanner.NotificationBannerType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderConversionForm;
import uk.co.ogauthority.pathfinder.model.notificationbanner.NotificationBannerView;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderConversionService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderSummaryService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.notificationbanner.NotificationBannerUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = UpcomingTenderConversionController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class UpcomingTenderConversionControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer UPCOMING_TENDER_ID = 1;
  private static final Integer DISPLAY_ORDER = 1;
  private static final Class<UpcomingTenderConversionController> CONTROLLER = UpcomingTenderConversionController.class;
  private static final Class<UpcomingTendersController> SUMMARY_CONTROLLER = UpcomingTendersController.class;
  private static final String VIEW_NAME = "project/upcomingtender/convertUpcomingTender";

  @MockitoBean
  private UpcomingTenderService upcomingTenderService;

  @MockitoBean
  private UpcomingTenderSummaryService upcomingTenderSummaryService;

  @MockitoBean
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @MockitoBean
  private UpcomingTenderConversionService conversionService;

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);
  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
  private final UpcomingTender upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);
  private final UpcomingTenderView upcomingTenderView = UpcomingTenderUtil.getView(DISPLAY_ORDER, true);

  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);
  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.INFRASTRUCTURE);
  private final Set<ProjectPermission> requiredPermissions = ProjectControllerTesterService.PROJECT_CREATE_PERMISSION_SET;
  private ProjectControllerTesterService projectControllerTesterService;

  @Before
  public void setUp() {
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(any(), any())).thenReturn(true);
    when(upcomingTenderService.getOrError(UPCOMING_TENDER_ID, projectDetail)).thenReturn(upcomingTender);

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
    when(upcomingTenderService.isValid(upcomingTender, ValidationType.FULL)).thenReturn(true);

    var flashMap = mockMvc.perform(
            post(route(on(CONTROLLER)
                .convertUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null, null, null, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(route(on(SUMMARY_CONTROLLER).viewUpcomingTenders(PROJECT_ID, null))))
        .andReturn()
        .getFlashMap();

    verify(conversionService).validate(any(), any());
    verify(conversionService).convertUpcomingTenderToAwardedContract(
        any(UpcomingTender.class),
        any(UpcomingTenderConversionForm.class)
    );

    assertThat(flashMap.get(NotificationBannerUtils.NOTIFICATION_BANNER_OBJECT_NAME)).isNotNull();

    var bannerView = (NotificationBannerView) flashMap.get(NotificationBannerUtils.NOTIFICATION_BANNER_OBJECT_NAME);
    assertThat(bannerView.getBannerLink().getLinkText()).isEqualTo(UpcomingTenderConversionController.BANNER_LINK_TEXT);
    assertThat(bannerView.getBannerLink().getLinkUrl()).isEqualTo(
        ReverseRouter.route(on(InfrastructureAwardedContractController.class).viewAwardedContracts(PROJECT_ID, null)));
    assertThat(bannerView.getBannerType()).isEqualTo(NotificationBannerType.SUCCESS);
    assertThat(bannerView.getTitle().getTitle()).isEqualTo(UpcomingTenderConversionController.BANNER_TITLE);
    assertThat(bannerView.getHeading().getHeading()).isEqualTo(UpcomingTenderConversionController.BANNER_HEADING);
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
    when(upcomingTenderService.isValid(upcomingTender, ValidationType.FULL)).thenReturn(true);

    mockMvc.perform(
            post(route(on(CONTROLLER)
                .convertUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null, null, null, null)))
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
    when(upcomingTenderService.isValid(upcomingTender, ValidationType.FULL)).thenReturn(true);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).convertUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null, null, null, null),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void convertUpcomingTender_withDifferentProjectDetail_assertNotFound() throws Exception {
    var otherAllowedProjectId = PROJECT_ID+1;
    var otherAllowedProject = new Project();
    otherAllowedProject.setId(otherAllowedProjectId);

    var otherAllowedProjectDetail = mock(ProjectDetail.class);
    when(otherAllowedProjectDetail.getProject())
        .thenReturn(otherAllowedProject);
    when(otherAllowedProjectDetail.getStatus())
        .thenReturn(ProjectStatus.DRAFT);
    when(otherAllowedProjectDetail.getProjectType())
        .thenReturn(ProjectType.INFRASTRUCTURE);

    when(projectService.getLatestDetailOrError(otherAllowedProjectId))
        .thenReturn(otherAllowedProjectDetail);

    when(upcomingTenderService.getOrError(UPCOMING_TENDER_ID, otherAllowedProjectDetail))
        .thenThrow(PathfinderEntityNotFoundException.class);

    when(projectOperatorService.isUserInProjectTeam(otherAllowedProjectDetail, authenticatedUser))
        .thenReturn(true);

    mockMvc.perform(
            get(route(on(CONTROLLER).convertUpcomingTenderConfirm(otherAllowedProjectId, UPCOMING_TENDER_ID, DISPLAY_ORDER, null)))
                .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isNotFound());
  }
}
