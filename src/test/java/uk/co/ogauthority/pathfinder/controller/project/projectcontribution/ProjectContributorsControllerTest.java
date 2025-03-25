package uk.co.ogauthority.pathfinder.controller.project.projectcontribution;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.controller.project.projectcontributor.ProjectContributorsController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsManagementService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;


@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ProjectContributorsController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class ProjectContributorsControllerTest extends ProjectContextAbstractControllerTest {

  private final int projectId = 1;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES
  );

  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);

  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.INFRASTRUCTURE);

  private final Set<ProjectPermission> requiredPermissions = ProjectControllerTesterService.PROJECT_CREATE_PERMISSION_SET;

  @MockitoBean
  private ProjectContributorsManagementService projectContributorsManagementService;

  private ProjectControllerTesterService projectControllerTesterService;

  @Before
  public void setup() {
    projectControllerTesterService = new ProjectControllerTesterService(
        mockMvc,
        projectOperatorService,
        projectContributorsCommonService,
        teamService
    );
    when(projectService.getLatestDetailOrError(projectId)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUser)).thenReturn(true);
  }

  @Test
  public void renderProjectContributorsForm_projectContextSmokeTest() {

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ProjectContributorsController.class).renderProjectContributorsForm(projectId, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void saveProjectContributors_projectContextSmokeTest() {
    var bindingResult = new BeanPropertyBindingResult(CampaignInformationForm.class, "form");
    when(projectContributorsManagementService.validate(any(), any(), any(), any())).thenReturn(bindingResult);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ProjectContributorsController.class).saveProjectContributors(projectId, null, null, null, null),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void saveProjectContributors_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};
    var form = new ProjectContributorsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(projectContributorsManagementService.validate(any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProjectContributorsController.class)
                .saveProjectContributors(projectId, form, bindingResult, ValidationType.FULL, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().is3xxRedirection());
    verify(projectContributorsManagementService, times(1))
        .validate(any(), any(), eq(ValidationType.FULL), any());
    verify(projectContributorsManagementService, times(1)).saveProjectContributors(any(), any());
  }

  @Test
  public void saveProjectContributors_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};
    var form = new ProjectContributorsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(projectContributorsManagementService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(projectContributorsManagementService.getProjectContributorsFormModelAndView(any(), any(), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectContributorsController.class)
            .saveProjectContributors(projectId, form, bindingResult, ValidationType.FULL, null)))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().isOk());
    verify(projectContributorsManagementService, times(1))
        .validate(any(), any(), eq(ValidationType.FULL), any());
    verify(projectContributorsManagementService, never()).saveProjectContributors(any(), any());
  }

  @Test
  public void saveProjectContributors_partialValidation_valid() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};
    var form = new ProjectContributorsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(projectContributorsManagementService.validate(any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProjectContributorsController.class)
                .saveProjectContributors(projectId, form, bindingResult, ValidationType.FULL, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().is3xxRedirection());
    verify(projectContributorsManagementService, times(1))
        .validate(any(), any(), eq(ValidationType.PARTIAL), any());
    verify(projectContributorsManagementService, times(1)).saveProjectContributors(any(), any());
  }

  @Test
  public void saveProjectContributors_partialValidation_invalid() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};
    var form = new ProjectContributorsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(projectContributorsManagementService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(projectContributorsManagementService.getProjectContributorsFormModelAndView(any(), any(), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
            post(ReverseRouter.route(on(ProjectContributorsController.class)
                .saveProjectContributors(projectId, form, bindingResult, ValidationType.FULL, null)))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().isOk());
    verify(projectContributorsManagementService, times(1))
        .validate(any(), any(), eq(ValidationType.PARTIAL), any());
    verify(projectContributorsManagementService, never()).saveProjectContributors(any(), any());
  }
}