package uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ForwardWorkPlanAwardedContractRemovalController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class ForwardWorkPlanAwardedContractRemovalControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer AWARDED_CONTRACT_ID = 10;
  private static final Integer DISPLAY_ORDER = 1;
  private static final Class<ForwardWorkPlanAwardedContractRemovalController> CONTROLLER = ForwardWorkPlanAwardedContractRemovalController.class;
  private static final Class<ForwardWorkPlanAwardedContractSummaryController> SUMMARY_CONTROLLER = ForwardWorkPlanAwardedContractSummaryController.class;
  private static final Class<ForwardWorkPlanAwardedContractSetupController> SETUP_CONTROLLER = ForwardWorkPlanAwardedContractSetupController.class;


  @MockBean
  private ForwardWorkPlanAwardedContractService awardedContractService;

  @MockBean
  private ForwardWorkPlanAwardedContractSummaryService summaryService;

  @MockBean
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

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
  public void removeAwardedContractConfirmation() throws Exception {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);
    when(awardedContractService.getAwardedContract(AWARDED_CONTRACT_ID, projectDetail)).thenReturn(awardedContract);

    var awardedContractView = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(DISPLAY_ORDER);
    when(summaryService.getAwardedContractView(AWARDED_CONTRACT_ID, projectDetail, DISPLAY_ORDER)).thenReturn(awardedContractView);

    var modelAndView = mockMvc.perform(
            get(route(on(CONTROLLER).removeAwardedContractConfirmation(PROJECT_ID, AWARDED_CONTRACT_ID, DISPLAY_ORDER, null)))
                .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk())
        .andExpect(view().name("project/awardedcontract/removeAwardedContract"))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertThat(model).contains(
        entry("awardedContractView", awardedContractView),
        entry("cancelUrl", ReverseRouter.route(on(SUMMARY_CONTROLLER).viewAwardedContracts(PROJECT_ID, null)))
    );
  }

  @Test
  public void removeAwardedContractConfirmation_projectContextSmokeTest() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);
    when(awardedContractService.getAwardedContract(AWARDED_CONTRACT_ID, projectDetail)).thenReturn(awardedContract);

    var awardedContractView = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(DISPLAY_ORDER);
    when(summaryService.getAwardedContractView(AWARDED_CONTRACT_ID, projectDetail, DISPLAY_ORDER)).thenReturn(awardedContractView);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).removeAwardedContractConfirmation(
            PROJECT_ID,
            AWARDED_CONTRACT_ID,
            DISPLAY_ORDER,
            null
        ),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void removeAwardedContract_hasOtherAwardedContracts() throws Exception {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);
    when(awardedContractService.getAwardedContract(AWARDED_CONTRACT_ID, projectDetail)).thenReturn(awardedContract);

    when(awardedContractService.hasAwardedContracts(projectDetail)).thenReturn(true);

    mockMvc.perform(
            post(route(on(CONTROLLER).removeAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, DISPLAY_ORDER, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(route(on(SUMMARY_CONTROLLER).viewAwardedContracts(PROJECT_ID, null))));

    verify(awardedContractService).deleteAwardedContract(awardedContract);
  }

  @Test
  public void removeAwardedContract_hasNoOtherAwardedContracts() throws Exception {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);
    when(awardedContractService.getAwardedContract(AWARDED_CONTRACT_ID, projectDetail)).thenReturn(awardedContract);

    when(awardedContractService.hasAwardedContracts(projectDetail)).thenReturn(false);

    mockMvc.perform(
            post(route(on(CONTROLLER).removeAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, DISPLAY_ORDER, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(route(on(SETUP_CONTROLLER).getAwardedContractSetup(PROJECT_ID, null, null))));

    verify(awardedContractService).deleteAwardedContract(awardedContract);
  }

  @Test
  public void removeAwardedContract_projectContextSmokeTest() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);
    when(awardedContractService.getAwardedContract(AWARDED_CONTRACT_ID, projectDetail)).thenReturn(awardedContract);

    when(awardedContractService.hasAwardedContracts(projectDetail)).thenReturn(false);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).removeAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, DISPLAY_ORDER, null),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

}
