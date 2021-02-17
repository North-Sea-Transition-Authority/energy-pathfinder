package uk.co.ogauthority.pathfinder.controller.project.platformsfpsos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

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
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoForm;
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosService;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PlatformsFpsosController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class PlatformsFpsosControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer PLATFORM_FPSO_ID = 1;
  private static final Integer DISPLAY_ORDER = 1;

  @MockBean
  private PlatformsFpsosService platformsFpsosService;

  @MockBean
  private PlatformsFpsosSummaryService platformsFpsosSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final PlatformFpso platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withPlatformAndSubstructuresRemoved(detail);


  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();


  @Before
  public void setUp() {
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(detail);
    when(platformsFpsosService.getOrError(PLATFORM_FPSO_ID)).thenReturn(platformFpso);
    var platformFpsoView = PlatformFpsoViewUtil.createView(
        platformFpso,
        DISPLAY_ORDER,
        PROJECT_ID
    );
    when(platformsFpsosSummaryService.getView(any(), any(), any())).thenReturn(platformFpsoView);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, unAuthenticatedUser)).thenReturn(false);
  }


  @Test
  public void authenticatedUser_hasAccessToAddPlatformFpso() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).addPlatformFpso(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessAddPlatformFpso() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).addPlatformFpso(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToPlatformsFpsosSummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).viewPlatformsFpsos(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessPlatformsFpsosSummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).viewPlatformsFpsos(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToPlatformsFpsosRemove() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).removePlatformFpsoConfirm(PROJECT_ID, PLATFORM_FPSO_ID, DISPLAY_ORDER, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessPlatformsFpsosRemove() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).removePlatformFpsoConfirm(PROJECT_ID, PLATFORM_FPSO_ID, DISPLAY_ORDER, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToPlatformsFpsosEdit() throws Exception {
    when(platformsFpsosService.getForm(platformFpso)).thenReturn(PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatformAndSubstructuresToBeRemoved());
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).editPlatformFpso(PROJECT_ID, PLATFORM_FPSO_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessPlatformsFpsosEdit() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).editPlatformFpso(PROJECT_ID, PLATFORM_FPSO_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void savePlatformsFpsos_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(PlatformFpsoForm.class, "form");
    when(platformsFpsosService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PlatformsFpsosController.class)
            .saveNewPlatformFpso(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(platformsFpsosService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(platformsFpsosService, times(1)).createPlatformFpso(any(), any());
  }

  @Test
  public void savePlatformsFpsos_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(PlatformFpsoForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(platformsFpsosService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PlatformsFpsosController.class)
            .saveNewPlatformFpso(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(platformsFpsosService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(platformsFpsosService, times(0)).createPlatformFpso(any(), any());
  }

  @Test
  public void savePlatformsFpsos_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(PlatformFpsoForm.class, "form");
    when(platformsFpsosService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PlatformsFpsosController.class)
            .saveNewPlatformFpso(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(platformsFpsosService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(platformsFpsosService, times(1)).createPlatformFpso(any(), any());
  }

  @Test
  public void updatePlatformsFpsos_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(PlatformFpsoForm.class, "form");
    when(platformsFpsosService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PlatformsFpsosController.class)
            .updatePlatformFpso(PROJECT_ID, PLATFORM_FPSO_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(platformsFpsosService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(platformsFpsosService, times(1)).updatePlatformFpso(any(), any(), any());
  }

  @Test
  public void updatePlatformsFpsos_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(PlatformFpsoForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(platformsFpsosService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PlatformsFpsosController.class)
            .updatePlatformFpso(PROJECT_ID, PLATFORM_FPSO_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(platformsFpsosService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(platformsFpsosService, times(0)).updatePlatformFpso(any(), any(), any());
  }

  @Test
  public void updatePlatformsFpsos_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(PlatformFpsoForm.class, "form");
    when(platformsFpsosService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PlatformsFpsosController.class)
            .updatePlatformFpso(PROJECT_ID, PLATFORM_FPSO_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(platformsFpsosService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(platformsFpsosService, times(1)).updatePlatformFpso(any(), any(), any());
  }
}
