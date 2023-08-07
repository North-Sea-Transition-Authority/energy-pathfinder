package uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan;

import static java.util.Map.entry;
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

import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardContractController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ForwardWorkPlanAwardedContractSummaryController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class ForwardWorkPlanAwardedContractSummaryControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Class<ForwardWorkPlanAwardedContractSummaryController> CONTROLLER = ForwardWorkPlanAwardedContractSummaryController.class;
  private static final Class<ForwardWorkPlanAwardedContractController> AWARDED_CONTRACT_CONTROLLER = ForwardWorkPlanAwardedContractController.class;
  private static final Class<TaskListController> TASK_LIST_CONTROLLER = TaskListController.class;
  private static final Class<ForwardWorkPlanAwardedContractSummaryForm>  SUMMARY_FORM = ForwardWorkPlanAwardedContractSummaryForm.class;

  @MockBean
  private ForwardWorkPlanAwardedContractSummaryService summaryService;

  @MockBean
  private ValidationService validationService;

  @MockBean
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Captor
  private ArgumentCaptor<ForwardWorkPlanAwardedContractSummaryForm> formCaptor;

  private ProjectDetail projectDetail;
  private AuthenticatedUserAccount authenticatedUser;
  private ProjectControllerTesterService projectControllerTesterService;

  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);
  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.FORWARD_WORK_PLAN);
  private final Set<ProjectPermission> requiredPermissions = ProjectControllerTesterService.PROJECT_CREATE_PERMISSION_SET;

  @Before
  public void setUp() {
    projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.CREATE_PROJECT_PRIVILEGES);

    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUser)).thenReturn(true);
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(eq(projectDetail), any())).thenReturn(true);

    projectControllerTesterService = new ProjectControllerTesterService(
        mockMvc,
        projectOperatorService,
        projectContributorsCommonService,
        teamService
    );
  }

  @Test
  public void viewAwardedContracts() throws Exception {
    var awardedContractView = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(1);
    var awardedContractViewList = List.of(awardedContractView);
    when(summaryService.getAwardedContractViews(projectDetail)).thenReturn(awardedContractViewList);

    var form = mock(ForwardWorkPlanAwardedContractSummaryForm.class);
    when(summaryService.getForm(projectDetail)).thenReturn(form);

    var modelAndView = mockMvc.perform(
            get(route(on(CONTROLLER).viewAwardedContracts(PROJECT_ID, null)))
                .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk())
        .andExpect(view().name("project/awardedcontract/forwardworkplan/forwardWorkPlanAwardedContractFormSummary"))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertThat(model).contains(
        entry("pageTitle", AwardContractController.PAGE_NAME),
        entry("awardedContractViews", awardedContractViewList),
        entry("addAwardedContractUrl", ReverseRouter.route(on(AWARDED_CONTRACT_CONTROLLER).addAwardedContract(PROJECT_ID, null))),
        entry("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(PROJECT_ID)),
        entry("projectTypeDisplayNameLowercase", projectDetail.getProjectType().getLowercaseDisplayName())
    );
  }

  @Test
  public void viewAwardedContracts_projectContextSmokeTest() {
    var awardedContractView = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(1);
    var awardedContractViewList = List.of(awardedContractView);
    when(summaryService.getAwardedContractViews(projectDetail)).thenReturn(awardedContractViewList);

    var form = mock(ForwardWorkPlanAwardedContractSummaryForm.class);
    when(summaryService.getForm(projectDetail)).thenReturn(form);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).viewAwardedContracts(PROJECT_ID, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void saveAwardedContractSummary_validForm() throws Exception {
    var awardedContractView = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(1);
    var awardedContractViewList = List.of(awardedContractView);
    when(summaryService.getAwardedContractViews(projectDetail)).thenReturn(awardedContractViewList);

    var bindingResult = new BeanPropertyBindingResult(SUMMARY_FORM, "form");
    when(validationService.validate(any(), any(), eq(ValidationType.FULL))).thenReturn(bindingResult);

    mockMvc.perform(
            post(route(on(CONTROLLER).saveAwardedContractSummary(PROJECT_ID, null, bindingResult, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .param("hasOtherContractsToAdd", "true")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(route(on(TASK_LIST_CONTROLLER).viewTaskList(PROJECT_ID, null))));

    verify(validationService).validate(formCaptor.capture(), any(), eq(ValidationType.FULL));
    var form = formCaptor.getValue();
    assertThat(form.getHasOtherContractsToAdd()).isTrue();

    verify(summaryService).saveAwardedContractSummary(form, projectDetail);
  }

  @Test
  public void saveAwardedContractSummary_invalidForm() throws Exception {
    var awardedContractView = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(1);
    var awardedContractViewList = List.of(awardedContractView);
    when(summaryService.getAwardedContractViews(projectDetail)).thenReturn(awardedContractViewList);

    var bindingResult = new BeanPropertyBindingResult(SUMMARY_FORM, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default Message"));
    when(validationService.validate(any(), any(), eq(ValidationType.FULL))).thenReturn(bindingResult);

    mockMvc.perform(
            post(route(on(CONTROLLER).saveAwardedContractSummary(PROJECT_ID, null, bindingResult, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
        )
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("project/awardedcontract/forwardworkplan/forwardWorkPlanAwardedContractFormSummary"));

    verify(validationService).validate(formCaptor.capture(), any(), eq(ValidationType.FULL));
    var form = formCaptor.getValue();
    assertThat(form.getHasOtherContractsToAdd()).isNull();

    verify(summaryService, never()).saveAwardedContractSummary(form, projectDetail);
  }

  @Test
  public void saveAwardedContractSummary_projectContextSmokeTest() {
    var awardedContractView = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(1);
    var awardedContractViewList = List.of(awardedContractView);
    when(summaryService.getAwardedContractViews(projectDetail)).thenReturn(awardedContractViewList);

    var bindingResult = new BeanPropertyBindingResult(SUMMARY_FORM, "form");
    when(validationService.validate(any(), any(), eq(ValidationType.FULL))).thenReturn(bindingResult);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam("hasOtherContractsToAdd", "true")
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).saveAwardedContractSummary(PROJECT_ID, null, bindingResult, null),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

}
