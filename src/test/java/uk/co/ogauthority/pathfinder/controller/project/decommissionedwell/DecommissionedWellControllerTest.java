package uk.co.ogauthority.pathfinder.controller.project.decommissionedwell;

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
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell.DecommissionedWellForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.decommissionedwell.DecommissionedWellService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = DecommissionedWellController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class DecommissionedWellControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer DECOMMISSIONED_WELL_ID = 2;

  @MockBean
  private DecommissionedWellService decommissionedWellService;

  private AuthenticatedUserAccount authenticatedUser;

  private AuthenticatedUserAccount unauthenticatedUser;

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.CREATE_PROJECT_PRIVILEGES);
    unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    when(projectService.getLatestDetail(PROJECT_ID)).thenReturn(Optional.of(projectDetail));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, unauthenticatedUser)).thenReturn(false);
  }

  @Test
  public void viewWellsToBeDecommissioned_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedWellController.class).viewWellsToBeDecommissioned(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void viewWellsToBeDecommissioned_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedWellController.class).viewWellsToBeDecommissioned(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void addWellsToBeDecommissioned_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedWellController.class).addWellsToBeDecommissioned(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void addWellsToBeDecommissioned_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedWellController.class).addWellsToBeDecommissioned(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void createWellsToBeDecommissioned_whenPartialSaveAndAuthenticated_thenCreateAndRedirect() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedWellService.validate(any(), any(), eq(ValidationType.PARTIAL))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedWellController.class)
            .createWellsToBeDecommissioned(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(decommissionedWellService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissionedWellService, times(1)).createDecommissionedWell(any(), any());
  }

  @Test
  public void createWellsToBeDecommissioned_whenPartialSaveAndUnauthenticated_thenForbidden() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedWellController.class)
            .createWellsToBeDecommissioned(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(decommissionedWellService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissionedWellService, times(0)).createDecommissionedWell(any(), any());
  }

  @Test
  public void createWellsToBeDecommissioned_whenFullSaveAndAuthenticated_thenCreateAndRedirect() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedWellService.validate(any(), any(), eq(ValidationType.FULL))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedWellController.class)
            .createWellsToBeDecommissioned(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(decommissionedWellService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedWellService, times(1)).createDecommissionedWell(any(), any());
  }

  @Test
  public void createWellsToBeDecommissioned_whenFulSaveAndUnauthenticated_thenForbidden() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedWellController.class)
            .createWellsToBeDecommissioned(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(decommissionedWellService, times(0)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedWellService, times(0)).createDecommissionedWell(any(), any());
  }

  @Test
  public void createWellsToBeDecommissioned_whenFullSaveAndInvalidForm_thenNotCreated() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(decommissionedWellService.validate(any(), any(), eq(ValidationType.FULL))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedWellController.class)
            .createWellsToBeDecommissioned(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isOk());

    verify(decommissionedWellService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedWellService, times(0)).createDecommissionedWell(any(), any());
  }

  @Test
  public void getWellsToBeDecommissioned_whenAuthenticated_thenAccess() throws Exception {

    when(decommissionedWellService.getForm(DECOMMISSIONED_WELL_ID, projectDetail)).thenReturn(new DecommissionedWellForm());

    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedWellController.class).getWellsToBeDecommissioned(PROJECT_ID, DECOMMISSIONED_WELL_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getWellsToBeDecommissioned_whenUnauthenticated_thenForbidden() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissionedWellController.class).getWellsToBeDecommissioned(PROJECT_ID, DECOMMISSIONED_WELL_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void updateWellsToBeDecommissioned_whenPartialSaveAndAuthenticated_thenCreateAndRedirect() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedWellService.validate(any(), any(), eq(ValidationType.PARTIAL))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedWellController.class)
            .updateWellsToBeDecommissioned(
                PROJECT_ID,
                DECOMMISSIONED_WELL_ID,
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

    verify(decommissionedWellService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissionedWellService, times(1)).updateDecommissionedWell(any(), any(), any());
  }

  @Test
  public void updateWellsToBeDecommissioned_whenPartialSaveAndUnauthenticated_thenForbidden() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedWellController.class)
            .updateWellsToBeDecommissioned(
                PROJECT_ID,
                DECOMMISSIONED_WELL_ID,
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

    verify(decommissionedWellService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissionedWellService, times(0)).updateDecommissionedWell(any(), any(), any());
  }

  @Test
  public void updateWellsToBeDecommissioned_whenFullSaveAndAuthenticated_thenCreateAndRedirect() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissionedWellService.validate(any(), any(), eq(ValidationType.FULL))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedWellController.class)
            .updateWellsToBeDecommissioned(
                PROJECT_ID,
                DECOMMISSIONED_WELL_ID,
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

    verify(decommissionedWellService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedWellService, times(1)).updateDecommissionedWell(any(), any(), any());
  }

  @Test
  public void updateWellsToBeDecommissioned_whenFulSaveAndUnauthenticated_thenForbidden() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedWellController.class)
            .updateWellsToBeDecommissioned(
                PROJECT_ID,
                DECOMMISSIONED_WELL_ID,
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

    verify(decommissionedWellService, times(0)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedWellService, times(0)).updateDecommissionedWell(any(), any(), any());
  }

  @Test
  public void updateWellsToBeDecommissionedToBeDecommissioned_whenFullSaveAndInvalidForm_thenNotCreated() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(decommissionedWellService.validate(any(), any(), eq(ValidationType.FULL))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissionedWellController.class)
            .updateWellsToBeDecommissioned(
                PROJECT_ID,
                DECOMMISSIONED_WELL_ID,
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

    verify(decommissionedWellService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissionedWellService, times(0)).updateDecommissionedWell(any(), any(), any());
  }

}