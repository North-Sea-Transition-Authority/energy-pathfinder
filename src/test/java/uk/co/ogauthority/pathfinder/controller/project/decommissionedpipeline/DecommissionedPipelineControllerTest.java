package uk.co.ogauthority.pathfinder.controller.project.decommissionedpipeline;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline.DecommissionedPipelineService;
import uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline.DecommissionedPipelineSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.DecommissionedPipelineTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = DecommissionedPipelineController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class DecommissionedPipelineControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer DECOMMISSIONED_PIPELINE_ID = 10;
  private static final Integer DISPLAY_ORDER = 2;

  @MockBean
  private DecommissionedPipelineService decommissionedPipelineService;

  @MockBean
  private DecommissionedPipelineSummaryService decommissionedPipelineSummaryService;

  private ProjectDetail projectDetail;

  private AuthenticatedUserAccount authenticatedUser;

  private AuthenticatedUserAccount unauthenticatedUser;

  @Before
  public void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.CREATE_PROJECT_PRIVILEGES);
    unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    when(projectService.getLatestDetail(PROJECT_ID)).thenReturn(Optional.of(projectDetail));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, unauthenticatedUser)).thenReturn(false);

    when(decommissionedPipelineService.getPipelineRestUrl()).thenReturn("testUrl");
  }

  @Test
  public void getPipelines_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedPipelineController.class).getPipelines(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getPipelines_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedPipelineController.class).getPipelines(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void savePipelines_whenUnauthenticated_thenNoAccess() throws Exception {
    var decommissionedPipelineViews = List.of(
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView(),
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView()
    );

    when(decommissionedPipelineSummaryService.getValidatedDecommissionedPipelineSummaryViews(projectDetail)).thenReturn(decommissionedPipelineViews);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .savePipelines(PROJECT_ID, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(decommissionedPipelineSummaryService, times(0)).validateViews(any());
  }

  @Test
  public void savePipelines_whenValid_thenRedirect() throws Exception {
    var decommissionedPipelineViews = List.of(
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView(),
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView()
    );

    when(decommissionedPipelineSummaryService.getValidatedDecommissionedPipelineSummaryViews(projectDetail)).thenReturn(decommissionedPipelineViews);
    when(decommissionedPipelineSummaryService.validateViews(decommissionedPipelineViews)).thenReturn(ValidationResult.VALID);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .savePipelines(PROJECT_ID, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(decommissionedPipelineSummaryService, times(1)).validateViews(any());
  }

  @Test
  public void savePipelines_whenInvalid_thenReturnErrors() throws Exception {
    var decommissionedPipelineViews = List.of(
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView(),
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView()
    );

    when(decommissionedPipelineSummaryService.getValidatedDecommissionedPipelineSummaryViews(projectDetail)).thenReturn(decommissionedPipelineViews);
    when(decommissionedPipelineSummaryService.validateViews(decommissionedPipelineViews)).thenReturn(ValidationResult.INVALID);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .savePipelines(PROJECT_ID, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("errorList"));

    verify(decommissionedPipelineSummaryService, times(1)).validateViews(any());
    verify(decommissionedPipelineSummaryService, times(1)).getDecommissionedPipelineViewErrors(any());
  }

  @Test
  public void addPipeline_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedPipelineController.class).addPipeline(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void addPipeline_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedPipelineController.class).addPipeline(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getPipeline_whenAuthenticated_thenAccess() throws Exception {

    when(decommissionedPipelineService.getForm(DECOMMISSIONED_PIPELINE_ID, projectDetail))
        .thenReturn(new DecommissionedPipelineForm());

    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedPipelineController.class).getPipeline(PROJECT_ID, DECOMMISSIONED_PIPELINE_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getPipeline_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedPipelineController.class).getPipeline(PROJECT_ID, DECOMMISSIONED_PIPELINE_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void createPipeline_whenUnauthenticatedPartialSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .createPipeline(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(decommissionedPipelineService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissionedPipelineService, times(0)).createDecommissionedPipeline(any(), any());
  }

  @Test
  public void createPipeline_whenUnauthenticatedFullSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .createPipeline(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isForbidden());

    verify(decommissionedPipelineService, times(0)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedPipelineService, times(0)).createDecommissionedPipeline(any(), any());
  }

  @Test
  public void createPipeline_whenValidFormAndPartialSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .createPipeline(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(decommissionedPipelineService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissionedPipelineService, times(1)).createDecommissionedPipeline(any(), any());
  }

  @Test
  public void createPipeline_whenValidFormAndFullSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .createPipeline(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(decommissionedPipelineService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedPipelineService, times(1)).createDecommissionedPipeline(any(), any());
  }

  @Test
  public void createPipeline_whenInvalidFormAndFullSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .createPipeline(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isOk());

    verify(decommissionedPipelineService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedPipelineService, times(0)).createDecommissionedPipeline(any(), any());
  }

  @Test
  public void createPipeline_whenInvalidFormAndPartialSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .createPipeline(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isOk());

    verify(decommissionedPipelineService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissionedPipelineService, times(0)).createDecommissionedPipeline(any(), any());
  }

  @Test
  public void updatePipeline_whenUnauthenticatedPartialSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .updatePipeline(PROJECT_ID, DECOMMISSIONED_PIPELINE_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(decommissionedPipelineService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissionedPipelineService, times(0)).updateDecommissionedPipeline(any(), any(), any());
  }

  @Test
  public void updatePipeline_whenUnauthenticatedFullSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .updatePipeline(PROJECT_ID, DECOMMISSIONED_PIPELINE_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isForbidden());

    verify(decommissionedPipelineService, times(0)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedPipelineService, times(0)).updateDecommissionedPipeline(any(), any(), any());
  }

  @Test
  public void updatePipeline_whenValidFormAndPartialSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .updatePipeline(PROJECT_ID, DECOMMISSIONED_PIPELINE_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(decommissionedPipelineService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissionedPipelineService, times(1)).updateDecommissionedPipeline(any(), any(), any());
  }

  @Test
  public void updatePipeline_whenValidFormAndFullSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .updatePipeline(PROJECT_ID, DECOMMISSIONED_PIPELINE_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(decommissionedPipelineService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedPipelineService, times(1)).updateDecommissionedPipeline(any(), any(), any());
  }

  @Test
  public void updatePipeline_whenInvalidFormAndFullSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .updatePipeline(PROJECT_ID, DECOMMISSIONED_PIPELINE_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isOk());

    verify(decommissionedPipelineService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedPipelineService, times(0)).updateDecommissionedPipeline(any(), any(), any());
  }

  @Test
  public void updatePipeline_whenInvalidFormAndPartialSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedPipelineForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(decommissionedPipelineService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .updatePipeline(PROJECT_ID, DECOMMISSIONED_PIPELINE_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isOk());

    verify(decommissionedPipelineService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedPipelineService, times(0)).updateDecommissionedPipeline(any(), any(), any());
  }

  @Test
  public void removePipelineConfirmation_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedPipelineController.class).removePipelineConfirmation(
            PROJECT_ID,
            DECOMMISSIONED_PIPELINE_ID,
            DISPLAY_ORDER,
            null
        )))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void removePipelineConfirmation_whenAuthenticated_thenAccess() throws Exception {

    when(decommissionedPipelineSummaryService.getDecommissionedPipelineSummaryView(
        DECOMMISSIONED_PIPELINE_ID,
        projectDetail,
        DISPLAY_ORDER
    )).thenReturn(
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView()
    );

    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedPipelineController.class).removePipelineConfirmation(
            PROJECT_ID,
            DECOMMISSIONED_PIPELINE_ID,
            DISPLAY_ORDER,
            null
        )))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void removePipeline_whenAuthenticated_thenAccess() throws Exception {

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    when(decommissionedPipelineService.getDecommissionedPipeline(any(), any())).thenReturn(
        decommissionedPipeline
    );

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .removePipeline(PROJECT_ID, DECOMMISSIONED_PIPELINE_ID, DISPLAY_ORDER, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(decommissionedPipelineService, times(1)).deleteDecommissionedPipeline(decommissionedPipeline);

  }

  @Test
  public void removePipeline_whenUnauthenticated_thenNoAccess() throws Exception {

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    when(decommissionedPipelineService.getDecommissionedPipeline(any(), any())).thenReturn(
        decommissionedPipeline
    );

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedPipelineController.class)
            .removePipeline(PROJECT_ID, DECOMMISSIONED_PIPELINE_ID, DISPLAY_ORDER, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isForbidden());

    verify(decommissionedPipelineService, times(0)).deleteDecommissionedPipeline(decommissionedPipeline);

  }
}
