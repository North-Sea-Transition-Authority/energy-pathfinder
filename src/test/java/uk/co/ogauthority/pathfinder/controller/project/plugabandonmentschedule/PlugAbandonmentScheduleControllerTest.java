package uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule;

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
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule.PlugAbandonmentScheduleService;
import uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule.PlugAbandonmentWellService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = PlugAbandonmentScheduleController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class PlugAbandonmentScheduleControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer PLUG_ABANDONMENT_SCHEDULE_ID = 2;

  @MockBean
  private PlugAbandonmentScheduleService plugAbandonmentScheduleService;

  @MockBean
  private PlugAbandonmentWellService plugAbandonmentWellService;

  private AuthenticatedUserAccount authenticatedUser;

  private AuthenticatedUserAccount unauthenticatedUser;

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.CREATE_PROJECT_PRIVILEGES);
    unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, unauthenticatedUser)).thenReturn(false);

    when(plugAbandonmentScheduleService.getWellboreRestUrl()).thenReturn("testurl");
  }

  @Test
  public void viewPlugAbandonmentSchedules_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlugAbandonmentScheduleController.class).viewPlugAbandonmentSchedules(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void viewPlugAbandonmentSchedules_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlugAbandonmentScheduleController.class).viewPlugAbandonmentSchedules(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void addPlugAbandonmentSchedule_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlugAbandonmentScheduleController.class).addPlugAbandonmentSchedule(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void addPlugAbandonmentSchedule_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlugAbandonmentScheduleController.class).addPlugAbandonmentSchedule(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void createPlugAbandonmentSchedule_whenPartialSaveAndAuthenticated_thenCreateAndRedirect() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new PlugAbandonmentScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(plugAbandonmentScheduleService.validate(any(), any(), eq(ValidationType.PARTIAL))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PlugAbandonmentScheduleController.class)
            .createPlugAbandonmentSchedule(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(plugAbandonmentScheduleService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(plugAbandonmentScheduleService, times(1)).createPlugAbandonmentSchedule(any(), any());
  }

  @Test
  public void createPlugAbandonmentSchedule_whenPartialSaveAndUnauthenticated_thenForbidden() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new PlugAbandonmentScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    mockMvc.perform(
        post(ReverseRouter.route(on(PlugAbandonmentScheduleController.class)
            .createPlugAbandonmentSchedule(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(plugAbandonmentScheduleService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(plugAbandonmentScheduleService, times(0)).createPlugAbandonmentSchedule(any(), any());
  }

  @Test
  public void createPlugAbandonmentSchedule_whenFullSaveAndAuthenticated_thenCreateAndRedirect() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new PlugAbandonmentScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(plugAbandonmentScheduleService.validate(any(), any(), eq(ValidationType.FULL))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PlugAbandonmentScheduleController.class)
            .createPlugAbandonmentSchedule(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(plugAbandonmentScheduleService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(plugAbandonmentScheduleService, times(1)).createPlugAbandonmentSchedule(any(), any());
  }

  @Test
  public void createPlugAbandonmentSchedule_whenFulSaveAndUnauthenticated_thenForbidden() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new PlugAbandonmentScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    mockMvc.perform(
        post(ReverseRouter.route(on(PlugAbandonmentScheduleController.class)
            .createPlugAbandonmentSchedule(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(plugAbandonmentScheduleService, times(0)).validate(any(), any(), eq(ValidationType.FULL));
    verify(plugAbandonmentScheduleService, times(0)).createPlugAbandonmentSchedule(any(), any());
  }

  @Test
  public void createPlugAbandonmentSchedule_whenFullSaveAndInvalidForm_thenNotCreated() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new PlugAbandonmentScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(plugAbandonmentScheduleService.validate(any(), any(), eq(ValidationType.FULL))).thenReturn(bindingResult);

    when(plugAbandonmentScheduleService.getPlugAbandonmentScheduleModelAndView(eq(PROJECT_ID), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
        post(ReverseRouter.route(on(PlugAbandonmentScheduleController.class)
            .createPlugAbandonmentSchedule(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isOk());

    verify(plugAbandonmentScheduleService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(plugAbandonmentScheduleService, times(0)).createPlugAbandonmentSchedule(any(), any());
  }

  @Test
  public void getPlugAbandonmentSchedule_whenAuthenticated_thenAccess() throws Exception {
    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();

    when(plugAbandonmentScheduleService.getPlugAbandonmentScheduleOrError(PLUG_ABANDONMENT_SCHEDULE_ID, projectDetail)).thenReturn(
        plugAbandonmentSchedule
    );
    when(plugAbandonmentScheduleService.getForm(plugAbandonmentSchedule)).thenReturn(new PlugAbandonmentScheduleForm());

    mockMvc.perform(get(ReverseRouter.route(
        on(PlugAbandonmentScheduleController.class).getPlugAbandonmentSchedule(PROJECT_ID, PLUG_ABANDONMENT_SCHEDULE_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getPlugAbandonmentSchedule_whenUnauthenticated_thenForbidden() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlugAbandonmentScheduleController.class).getPlugAbandonmentSchedule(PROJECT_ID, PLUG_ABANDONMENT_SCHEDULE_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void updatePlugAbandonmentSchedule_whenPartialSaveAndAuthenticated_thenCreateAndRedirect() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new PlugAbandonmentScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(plugAbandonmentScheduleService.validate(any(), any(), eq(ValidationType.PARTIAL))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PlugAbandonmentScheduleController.class)
            .updatePlugAbandonmentSchedule(
                PROJECT_ID,
                PLUG_ABANDONMENT_SCHEDULE_ID,
                form,
                bindingResult,
                ValidationType.PARTIAL,
                null
            )
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(plugAbandonmentScheduleService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(plugAbandonmentScheduleService, times(1)).updatePlugAbandonmentSchedule(any(), any(), any());
  }

  @Test
  public void updatePlugAbandonmentSchedule_whenPartialSaveAndUnauthenticated_thenForbidden() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new PlugAbandonmentScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    mockMvc.perform(
        post(ReverseRouter.route(on(PlugAbandonmentScheduleController.class)
            .updatePlugAbandonmentSchedule(
                PROJECT_ID,
                PLUG_ABANDONMENT_SCHEDULE_ID,
                form,
                bindingResult,
                ValidationType.PARTIAL,
                null
            )
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(plugAbandonmentScheduleService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(plugAbandonmentScheduleService, times(0)).updatePlugAbandonmentSchedule(any(), any(), any());
  }

  @Test
  public void updatePlugAbandonmentSchedule_whenFullSaveAndAuthenticated_thenCreateAndRedirect() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new PlugAbandonmentScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(plugAbandonmentScheduleService.validate(any(), any(), eq(ValidationType.FULL))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PlugAbandonmentScheduleController.class)
            .updatePlugAbandonmentSchedule(
                PROJECT_ID,
                PLUG_ABANDONMENT_SCHEDULE_ID,
                form,
                bindingResult,
                ValidationType.FULL,
                null
            )
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(plugAbandonmentScheduleService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(plugAbandonmentScheduleService, times(1)).updatePlugAbandonmentSchedule(any(), any(), any());
  }

  @Test
  public void updatePlugAbandonmentSchedule_whenFulSaveAndUnauthenticated_thenForbidden() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new PlugAbandonmentScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    mockMvc.perform(
        post(ReverseRouter.route(on(PlugAbandonmentScheduleController.class)
            .updatePlugAbandonmentSchedule(
                PROJECT_ID,
                PLUG_ABANDONMENT_SCHEDULE_ID,
                form,
                bindingResult,
                ValidationType.FULL,
                null
            )
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(plugAbandonmentScheduleService, times(0)).validate(any(), any(), eq(ValidationType.FULL));
    verify(plugAbandonmentScheduleService, times(0)).updatePlugAbandonmentSchedule(any(), any(), any());
  }

  @Test
  public void updatePlugAbandonmentSchedule_whenFullSaveAndInvalidForm_thenNotCreated() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new PlugAbandonmentScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(plugAbandonmentScheduleService.validate(any(), any(), eq(ValidationType.FULL))).thenReturn(bindingResult);

    when(plugAbandonmentScheduleService.getPlugAbandonmentScheduleModelAndView(eq(PROJECT_ID), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
        post(ReverseRouter.route(on(PlugAbandonmentScheduleController.class)
            .updatePlugAbandonmentSchedule(
                PROJECT_ID,
                PLUG_ABANDONMENT_SCHEDULE_ID,
                form,
                bindingResult,
                ValidationType.FULL,
                null
            )
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isOk());

    verify(plugAbandonmentScheduleService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(plugAbandonmentScheduleService, times(0)).updatePlugAbandonmentSchedule(any(), any(), any());
  }

}