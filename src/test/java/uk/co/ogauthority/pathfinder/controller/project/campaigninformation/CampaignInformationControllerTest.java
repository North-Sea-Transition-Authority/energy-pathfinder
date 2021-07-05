package uk.co.ogauthority.pathfinder.controller.project.campaigninformation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.campaigninformation.CampaignInformationModelService;
import uk.co.ogauthority.pathfinder.service.project.campaigninformation.CampaignInformationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.CampaignInformationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = CampaignInformationController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class CampaignInformationControllerTest extends ProjectContextAbstractControllerTest {

  @MockBean
  private CampaignInformationService campaignInformationService;

  @MockBean
  private CampaignInformationModelService campaignInformationModelService;

  private ProjectControllerTesterService projectControllerTesterService;

  private final int projectId = 1;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES
  );

  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);

  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.INFRASTRUCTURE);

  private final Set<ProjectPermission> requiredPermissions = ProjectControllerTesterService.PROJECT_CREATE_PERMISSION_SET;

  @Before
  public void setup() {
    projectControllerTesterService = new ProjectControllerTesterService(mockMvc, projectOperatorService);
    when(projectService.getLatestDetailOrError(projectId)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);
    when(campaignInformationService.createOrUpdateCampaignInformation(any(), any())).thenReturn(CampaignInformationTestUtil.createCampaignInformation());
  }

  @Test
  public void getCampaignInformation_projectContextSmokeTest() {

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CampaignInformationController.class).getCampaignInformation(projectId, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void saveCampaignInformation_projectContextSmokeTest() {
    var bindingResult = new BeanPropertyBindingResult(CampaignInformationForm.class, "form");
    when(campaignInformationService.validate(any(), any(), any(), any())).thenReturn(bindingResult);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CampaignInformationController.class).saveCampaignInformation(projectId, null, null, null, null),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void saveCampaignInformation_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = CampaignInformationTestUtil.createCampaignInformationForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(campaignInformationService.validate(any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(CampaignInformationController.class)
            .saveCampaignInformation(projectId, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().is3xxRedirection());
    verify(campaignInformationService, times(1)).validate(any(), any(), eq(ValidationType.FULL), any());
    verify(campaignInformationService, times(1)).createOrUpdateCampaignInformation(any(), any());
  }

  @Test
  public void saveCampaignInformation_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = CampaignInformationTestUtil.createCampaignInformationForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(campaignInformationService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(campaignInformationModelService.getCampaignInformationModelAndView(any(), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
        post(ReverseRouter.route(on(CampaignInformationController.class)
            .saveCampaignInformation(projectId, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().isOk());
    verify(campaignInformationService, times(1)).validate(any(), any(), eq(ValidationType.FULL), any());
    verify(campaignInformationService, times(0)).createOrUpdateCampaignInformation(any(), any());

  }

  @Test
  public void saveCampaignInformation_partialValidation_valid() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = CampaignInformationTestUtil.createCampaignInformationForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(campaignInformationService.validate(any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(CampaignInformationController.class)
            .saveCampaignInformation(projectId, form, bindingResult, ValidationType.PARTIAL, null)
        ))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf())
        .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(campaignInformationService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL), any());
    verify(campaignInformationService, times(1)).createOrUpdateCampaignInformation(any(), any());
  }
  @Test
  public void saveCampaignInformation_partialValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = CampaignInformationTestUtil.createCampaignInformationForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(campaignInformationService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(campaignInformationModelService.getCampaignInformationModelAndView(any(), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
        post(ReverseRouter.route(on(CampaignInformationController.class)
            .saveCampaignInformation(projectId, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isOk());

    verify(campaignInformationService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL), any());
    verify(campaignInformationService, times(0)).createOrUpdateCampaignInformation(any(), any());

  }
}