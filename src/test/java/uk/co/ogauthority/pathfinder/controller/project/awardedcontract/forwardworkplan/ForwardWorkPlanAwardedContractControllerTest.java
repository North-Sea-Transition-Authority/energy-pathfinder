package uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan;

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

import java.util.Collections;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractForm;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ForwardWorkPlanAwardedContractController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class ForwardWorkPlanAwardedContractControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer AWARDED_CONTRACT_ID = 10;
  private static final Class<ForwardWorkPlanAwardedContractController> CONTROLLER = ForwardWorkPlanAwardedContractController.class;
  private static final Class<ForwardWorkPlanAwardedContractSummaryController> SUMMARY_CONTROLLER = ForwardWorkPlanAwardedContractSummaryController.class;

  @MockitoBean
  private ForwardWorkPlanAwardedContractService awardedContractService;

  @MockitoBean
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
  public void addAwardedContract() throws Exception {
    when(awardedContractService.getPreSelectedContractFunction(any(ForwardWorkPlanAwardedContractForm.class)))
        .thenReturn(Collections.emptyMap());

    var modelAndView = mockMvc.perform(
        get(route(on(CONTROLLER).addAwardedContract(PROJECT_ID, null)))
            .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk())
        .andExpect(view().name("project/awardedcontract/awardedContract"))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertThat(model)
        .containsEntry("contractBands", ContractBand.getAllAsMap(ProjectType.FORWARD_WORK_PLAN))
        .containsKeys("form", "contractFunctionRestUrl", "preSelectedContractFunctionMap");
  }

  @Test
  public void addAwardedContract_projectContextSmokeTest() {
    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).addAwardedContract(
            PROJECT_ID,
            null
        ),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void getAwardedContract_projectContextSmokeTest() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);

    when(awardedContractService.getAwardedContract(AWARDED_CONTRACT_ID, projectDetail)).thenReturn(awardedContract);
    when(awardedContractService.getForm(awardedContract))
        .thenReturn(AwardedContractTestUtil.createForwardWorkPlanAwardedContractForm());

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).getAwardedContract(
            projectDetail.getProject().getId(),
            AWARDED_CONTRACT_ID,
            null
        ),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void saveAwardedContract_newAwardedContract_validForm() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new ForwardWorkPlanAwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    when(awardedContractService.createAwardedContract(any(), any(), any()))
        .thenReturn(awardedContract);

    mockMvc.perform(
        post(route(on(CONTROLLER).saveAwardedContract(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(route(on(SUMMARY_CONTROLLER).viewAwardedContracts(PROJECT_ID, null))));

    verify(awardedContractService).validate(any(), any(), eq(ValidationType.FULL));
    verify(awardedContractService).createAwardedContract(any(), any(), any());
  }

  @Test
  public void saveAwardedContract_newAwardedContract_invalidForm() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new ForwardWorkPlanAwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default Message"));
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    when(awardedContractService.createAwardedContract(any(), any(), any()))
        .thenReturn(awardedContract);

    mockMvc.perform(
            post(route(on(CONTROLLER).saveAwardedContract(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("project/awardedcontract/awardedContract"));

    verify(awardedContractService).validate(any(), any(), eq(ValidationType.FULL));
    verify(awardedContractService, never()).createAwardedContract(any(), any(), any());
  }

  @Test
  public void saveAwardedContract_newAwardedContract_projectContextSmokeTest() {
    var form = new ForwardWorkPlanAwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    when(awardedContractService.createAwardedContract(any(), any(), any()))
        .thenReturn(awardedContract);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).saveAwardedContract(PROJECT_ID, form, bindingResult, ValidationType.FULL, null),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void saveAwardedContract_existingAwardedContract_validForm() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new ForwardWorkPlanAwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);
    when(awardedContractService.getAwardedContract(AWARDED_CONTRACT_ID, projectDetail)).thenReturn(awardedContract);
    when(awardedContractService.updateAwardedContract(eq(AWARDED_CONTRACT_ID), any(ProjectDetail.class), any(ForwardWorkPlanAwardedContractForm.class)))
        .thenReturn(awardedContract);

    mockMvc.perform(
            post(route(on(CONTROLLER).saveAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, form, bindingResult, ValidationType.FULL, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(route(on(SUMMARY_CONTROLLER).viewAwardedContracts(PROJECT_ID, null))));

    verify(awardedContractService).validate(any(), any(), eq(ValidationType.FULL));
    verify(awardedContractService).updateAwardedContract(
        eq(AWARDED_CONTRACT_ID),
        any(ProjectDetail.class),
        any(ForwardWorkPlanAwardedContractForm.class)
    );
  }

  @Test
  public void saveAwardedContract_existingAwardedContract_invalidForm() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new ForwardWorkPlanAwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default Message"));
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);
    when(awardedContractService.getAwardedContract(AWARDED_CONTRACT_ID, projectDetail)).thenReturn(awardedContract);

    mockMvc.perform(
            post(route(on(CONTROLLER).saveAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, form, bindingResult, ValidationType.FULL, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("project/awardedcontract/awardedContract"));

    verify(awardedContractService).validate(any(), any(), eq(ValidationType.FULL));
    verify(awardedContractService, never()).updateAwardedContract(any(), any(), any());
  }

  @Test
  public void saveAwardedContract_existingAwardedContract_projectContextSmokeTest() {
    var form = new ForwardWorkPlanAwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);
    when(awardedContractService.getAwardedContract(AWARDED_CONTRACT_ID, projectDetail)).thenReturn(awardedContract);
    when(awardedContractService.updateAwardedContract(eq(AWARDED_CONTRACT_ID), any(ProjectDetail.class), any(ForwardWorkPlanAwardedContractForm.class)))
        .thenReturn(awardedContract);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CONTROLLER).saveAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, form, bindingResult, ValidationType.FULL, null),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

}
