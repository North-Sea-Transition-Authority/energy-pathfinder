package uk.co.ogauthority.pathfinder.controller.project.integratedrig;

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
import uk.co.ogauthority.pathfinder.model.form.project.integratedrig.IntegratedRigForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.integratedrig.IntegratedRigService;
import uk.co.ogauthority.pathfinder.service.project.integratedrig.IntegratedRigSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.IntegratedRigTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = IntegratedRigController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class IntegratedRigControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer INTEGRATED_RIG_ID = 10;
  private static final Integer DISPLAY_ORDER = 2;

  @MockBean
  private IntegratedRigService integratedRigService;

  @MockBean
  private IntegratedRigSummaryService integratedRigSummaryService;

  private ProjectDetail projectDetail;

  private AuthenticatedUserAccount authenticatedUser;

  private AuthenticatedUserAccount unauthenticatedUser;

  @Before
  public void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.CREATE_PROJECT_PRIVILEGES);
    unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, unauthenticatedUser)).thenReturn(false);

    when(integratedRigService.getFacilityRestUrl()).thenReturn("testUrl");
    when(integratedRigService.createIntegratedRig(any(), any())).thenReturn(IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility());
  }

  @Test
  public void getIntegratedRigs_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(IntegratedRigController.class).viewIntegratedRigs(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getIntegratedRigs_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(IntegratedRigController.class).viewIntegratedRigs(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveIntegratedRigs_whenUnauthenticated_thenNoAccess() throws Exception {
    var integratedRigViews = List.of(
        IntegratedRigTestUtil.createIntegratedRigView(),
        IntegratedRigTestUtil.createIntegratedRigView()
    );

    when(integratedRigSummaryService.getValidatedIntegratedRigSummaryViews(projectDetail)).thenReturn(integratedRigViews);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .saveIntegratedRigs(PROJECT_ID, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(integratedRigSummaryService, times(0)).validateViews(any());
  }

  @Test
  public void saveIntegratedRigs_whenValid_thenRedirect() throws Exception {
    var integratedRigViews = List.of(
        IntegratedRigTestUtil.createIntegratedRigView(),
        IntegratedRigTestUtil.createIntegratedRigView()
    );

    when(integratedRigSummaryService.getValidatedIntegratedRigSummaryViews(projectDetail)).thenReturn(integratedRigViews);
    when(integratedRigSummaryService.validateViews(integratedRigViews)).thenReturn(ValidationResult.VALID);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .saveIntegratedRigs(PROJECT_ID, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(integratedRigSummaryService, times(1)).validateViews(any());
  }

  @Test
  public void saveIntegratedRigs_whenInvalid_thenReturnErrors() throws Exception {
    var integratedRigViews = List.of(
        IntegratedRigTestUtil.createIntegratedRigView(),
        IntegratedRigTestUtil.createIntegratedRigView()
    );

    when(integratedRigSummaryService.getValidatedIntegratedRigSummaryViews(projectDetail)).thenReturn(integratedRigViews);
    when(integratedRigSummaryService.validateViews(integratedRigViews)).thenReturn(ValidationResult.INVALID);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .saveIntegratedRigs(PROJECT_ID, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("errorList"));

    verify(integratedRigSummaryService, times(1)).validateViews(any());
    verify(integratedRigSummaryService, times(1)).getIntegratedRigViewErrors(any());
  }

  @Test
  public void addIntegratedRig_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(IntegratedRigController.class).addIntegratedRig(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void addIntegratedRig_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(IntegratedRigController.class).addIntegratedRig(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getIntegratedRig_whenAuthenticated_thenAccess() throws Exception {

    when(integratedRigService.getForm(INTEGRATED_RIG_ID, projectDetail))
        .thenReturn(new IntegratedRigForm());

    mockMvc.perform(get(ReverseRouter.route(
        on(IntegratedRigController.class).getIntegratedRig(PROJECT_ID, INTEGRATED_RIG_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getIntegratedRig_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(IntegratedRigController.class).getIntegratedRig(PROJECT_ID, INTEGRATED_RIG_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void createIntegratedRig_whenUnauthenticatedPartialSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .createIntegratedRig(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(integratedRigService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(integratedRigService, times(0)).createIntegratedRig(any(), any());
  }

  @Test
  public void createIntegratedRig_whenUnauthenticatedFullSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .createIntegratedRig(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isForbidden());

    verify(integratedRigService, times(0)).validate(any(), any(), eq(ValidationType.FULL));
    verify(integratedRigService, times(0)).createIntegratedRig(any(), any());
  }

  @Test
  public void createIntegratedRig_whenValidFormAndPartialSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .createIntegratedRig(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(integratedRigService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(integratedRigService, times(1)).createIntegratedRig(any(), any());
  }

  @Test
  public void createIntegratedRig_whenValidFormAndFullSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .createIntegratedRig(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(integratedRigService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(integratedRigService, times(1)).createIntegratedRig(any(), any());
  }

  @Test
  public void createIntegratedRig_whenInvalidFormAndFullSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .createIntegratedRig(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isOk());

    verify(integratedRigService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(integratedRigService, times(0)).createIntegratedRig(any(), any());
  }

  @Test
  public void createIntegratedRig_whenInvalidFormAndPartialSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .createIntegratedRig(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isOk());

    verify(integratedRigService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(integratedRigService, times(0)).createIntegratedRig(any(), any());
  }

  @Test
  public void updateIntegratedRig_whenUnauthenticatedPartialSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .updateIntegratedRig(PROJECT_ID, INTEGRATED_RIG_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(integratedRigService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(integratedRigService, times(0)).updateIntegratedRig(any(), any(), any());
  }

  @Test
  public void updateIntegratedRig_whenUnauthenticatedFullSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .updateIntegratedRig(PROJECT_ID, INTEGRATED_RIG_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isForbidden());

    verify(integratedRigService, times(0)).validate(any(), any(), eq(ValidationType.FULL));
    verify(integratedRigService, times(0)).updateIntegratedRig(any(), any(), any());
  }

  @Test
  public void updateIntegratedRig_whenValidFormAndPartialSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .updateIntegratedRig(PROJECT_ID, INTEGRATED_RIG_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(integratedRigService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(integratedRigService, times(1)).updateIntegratedRig(any(), any(), any());
  }

  @Test
  public void updateIntegratedRig_whenValidFormAndFullSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .updateIntegratedRig(PROJECT_ID, INTEGRATED_RIG_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(integratedRigService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(integratedRigService, times(1)).updateIntegratedRig(any(), any(), any());
  }

  @Test
  public void updateIntegratedRig_whenInvalidFormAndFullSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .updateIntegratedRig(PROJECT_ID, INTEGRATED_RIG_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isOk());

    verify(integratedRigService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(integratedRigService, times(0)).updateIntegratedRig(any(), any(), any());
  }

  @Test
  public void updateIntegratedRig_whenInvalidFormAndPartialSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new IntegratedRigForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(integratedRigService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .updateIntegratedRig(PROJECT_ID, INTEGRATED_RIG_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isOk());

    verify(integratedRigService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(integratedRigService, times(0)).updateIntegratedRig(any(), any(), any());
  }

  @Test
  public void removeIntegratedRigsConfirmation_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(IntegratedRigController.class).removeIntegratedRigsConfirmation(
            PROJECT_ID,
            INTEGRATED_RIG_ID,
            DISPLAY_ORDER,
            null
        )))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void removeIntegratedRigsConfirmation_whenAuthenticated_thenAccess() throws Exception {

    when(integratedRigSummaryService.getIntegratedRigSummaryView(
        INTEGRATED_RIG_ID,
        projectDetail,
        DISPLAY_ORDER
    )).thenReturn(
        IntegratedRigTestUtil.createIntegratedRigView()
    );

    mockMvc.perform(get(ReverseRouter.route(
        on(IntegratedRigController.class).removeIntegratedRigsConfirmation(
            PROJECT_ID,
            INTEGRATED_RIG_ID,
            DISPLAY_ORDER,
            null
        )))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void removeIntegratedRig_whenAuthenticated_thenAccess() throws Exception {

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility();

    when(integratedRigService.getIntegratedRig(any(), any())).thenReturn(
        integratedRig
    );

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .removeIntegratedRig(PROJECT_ID, INTEGRATED_RIG_ID, DISPLAY_ORDER, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(integratedRigService, times(1)).deleteIntegratedRig(integratedRig);

  }

  @Test
  public void removeIntegratedRig_whenUnauthenticated_thenNoAccess() throws Exception {

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility();

    when(integratedRigService.getIntegratedRig(any(), any())).thenReturn(
        integratedRig
    );

    mockMvc.perform(
        post(ReverseRouter.route(on(IntegratedRigController.class)
            .removeIntegratedRig(PROJECT_ID, INTEGRATED_RIG_ID, DISPLAY_ORDER, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isForbidden());

    verify(integratedRigService, times(0)).deleteIntegratedRig(integratedRig);

  }
}
