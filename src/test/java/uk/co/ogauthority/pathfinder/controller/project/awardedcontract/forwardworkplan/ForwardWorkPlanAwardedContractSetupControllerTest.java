package uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupForm;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ForwardWorkPlanAwardedContractSetupController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class ForwardWorkPlanAwardedContractSetupControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Class<ForwardWorkPlanAwardedContractSetupController> CONTROLLER = ForwardWorkPlanAwardedContractSetupController.class;
  private static final Class<ForwardWorkPlanAwardedContractSummaryController> SUMMARY_CONTROLLER = ForwardWorkPlanAwardedContractSummaryController.class;

  @MockBean
  private ForwardWorkPlanAwardedContractSetupService setupService;

  @MockBean
  private ForwardWorkPlanAwardedContractService awardedContractService;

  private ProjectDetail projectDetail;
  private AuthenticatedUserAccount authenticatedUser;
  private ArgumentCaptor<ForwardWorkPlanAwardedContractSetupForm> formCaptor;
  private ArgumentCaptor<BindingResult> bindingResultCaptor;
  private ProjectControllerTesterService projectControllerTesterService;

  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);
  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.FORWARD_WORK_PLAN);
  private final Set<ProjectPermission> requiredPermissions = ProjectControllerTesterService.PROJECT_CREATE_PERMISSION_SET;

  @Before
  public void setUp() {
    projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.CREATE_PROJECT_PRIVILEGES);

    formCaptor = ArgumentCaptor.forClass(ForwardWorkPlanAwardedContractSetupForm.class);
    bindingResultCaptor = ArgumentCaptor.forClass(BindingResult.class);

    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUser)).thenReturn(true);

    projectControllerTesterService = new ProjectControllerTesterService(
        mockMvc,
        projectOperatorService,
        projectContributorsCommonService,
        teamService
    );
  }

  @Test
  public void getAwardedContractSetup_hasNoAwardedContracts_thenReturnEmptyForm() throws Exception {
    var form = new ForwardWorkPlanAwardedContractSetupForm();
    when(setupService.getAwardedContractSetupFormFromDetail(projectDetail)).thenReturn(form);
    when(awardedContractService.hasAwardedContracts(projectDetail)).thenReturn(false);

    var modelAndView = mockMvc.perform(
        get(route(on(CONTROLLER).getAwardedContractSetup(PROJECT_ID, null, null)))
            .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk())
        .andExpect(view().name(ForwardWorkPlanAwardedContractSetupController.SETUP_TEMPLATE_PATH))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertThat(model).contains(
        entry("pageName", ForwardWorkPlanAwardedContractSetupController.PAGE_NAME),
        entry("form", form),
        entry("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(PROJECT_ID))
    );

    verify(awardedContractService).hasAwardedContracts(projectDetail);
  }

  @Test
  public void getAwardedContractSetup_hasAwardedContracts_thenReturnFilledForm() throws Exception {
    var form = new ForwardWorkPlanAwardedContractSetupForm();
    form.setHasContractToAdd(true);
    when(setupService.getAwardedContractSetupFormFromDetail(projectDetail)).thenReturn(form);
    when(awardedContractService.hasAwardedContracts(projectDetail)).thenReturn(true);

    mockMvc.perform(
        get(route(on(CONTROLLER).getAwardedContractSetup(PROJECT_ID, null, null)))
            .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(route(on(SUMMARY_CONTROLLER).viewAwardedContracts(PROJECT_ID, null))));

    verify(awardedContractService).hasAwardedContracts(projectDetail);
  }

  @Test
  public void getAwardedContractSetup_willAddContract_hasNoContracts_thenRedirectToAwardedContractSummary() throws Exception {
    var form = new ForwardWorkPlanAwardedContractSetupForm();
    form.setHasContractToAdd(true);
    when(setupService.getAwardedContractSetupFormFromDetail(projectDetail)).thenReturn(form);
    when(awardedContractService.hasAwardedContracts(projectDetail)).thenReturn(false);

    var modelAndView = mockMvc.perform(
            get(route(on(CONTROLLER).getAwardedContractSetup(PROJECT_ID, null, null)))
                .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk())
        .andExpect(view().name(ForwardWorkPlanAwardedContractSetupController.SETUP_TEMPLATE_PATH))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertThat(model).contains(
        entry("pageName", ForwardWorkPlanAwardedContractSetupController.PAGE_NAME),
        entry("form", form),
        entry("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(PROJECT_ID))
    );

    verify(awardedContractService).hasAwardedContracts(projectDetail);
  }

  @Test
  public void saveAwardedContractSetup_whenValidForm_thenRedirect() throws Exception {
    var expectedBindingResult = new BeanPropertyBindingResult(ForwardWorkPlanAwardedContractSetupForm.class, "form");
    when(setupService.validate(any(), any()))
        .thenReturn(expectedBindingResult);

    mockMvc.perform(post(route(on(CONTROLLER).saveAwardedContractSetup(PROJECT_ID, null, null, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf())
        .param("hasContractToAdd", "true")
    )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(route(on(SUMMARY_CONTROLLER).viewAwardedContracts(PROJECT_ID, null))));

    verify(setupService).validate(formCaptor.capture(), bindingResultCaptor.capture());

    var form = formCaptor.getValue();
    assertThat(form.getHasContractToAdd()).isTrue();

    var bindingResult = bindingResultCaptor.getValue();
    assertThat(bindingResult.hasErrors()).isFalse();

    verify(setupService).saveAwardedContractSetup(form, projectDetail);
  }

  @Test
  public void saveAwardedContractSetup_whenInvalidForm_thenStayOnPage() throws Exception {
    var validationMessage = "validationMessage";
    doAnswer(invocation -> {
      var bindingResult = invocation.getArgument(1, BindingResult.class);
      bindingResult.rejectValue("hasContractToAdd", "hasContractToAdd.required", validationMessage);
      return null;
    }).when(setupService).validate(
        any(ForwardWorkPlanAwardedContractSetupForm.class),
        any(BindingResult.class)
    );

    mockMvc.perform(post(route(on(CONTROLLER).saveAwardedContractSetup(PROJECT_ID, null, null, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf())
        )
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name(ForwardWorkPlanAwardedContractSetupController.SETUP_TEMPLATE_PATH));

    verify(setupService).validate(formCaptor.capture(), bindingResultCaptor.capture());

    var form = formCaptor.getValue();
    assertThat(form.getHasContractToAdd()).isNull();

    var bindingResult = bindingResultCaptor.getValue();
    assertThat(bindingResult.getAllErrors())
        .hasSize(1)
        .first().matches(error -> validationMessage.equals(error.getDefaultMessage()));

    verify(setupService, never()).saveAwardedContractSetup(form, projectDetail);
  }

  @Test
  public void getAwardedContractSetup_projectContextSmokeTest() {
    when(setupService.getAwardedContractSetupFormFromDetail(projectDetail)).thenReturn(new ForwardWorkPlanAwardedContractSetupForm());
    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).getAwardedContractSetup(PROJECT_ID, null, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void saveAndContinueContractJourney_projectContextSmokeTest() {
    var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanAwardedContractSetupForm.class, "form");
    when(setupService.validate(any(ForwardWorkPlanAwardedContractSetupForm.class), any(BindingResult.class)))
        .thenReturn(bindingResult);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).saveAwardedContractSetup(
            PROJECT_ID,
            null,
            null,
            null,
            null
        ),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

}
