package uk.co.ogauthority.pathfinder.controller.project.subseainfrastructure;

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
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaInfrastructureForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.subseainfrastructure.SubseaInfrastructureService;
import uk.co.ogauthority.pathfinder.service.project.subseainfrastructure.SubseaInfrastructureSummaryService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = SubseaInfrastructureController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class SubseaInfrastructureControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer SUBSEA_INFRASTRUCTURE_ID = 10;
  private static final Integer DISPLAY_ORDER = 2;

  @MockBean
  private SubseaInfrastructureService subseaInfrastructureService;

  @MockBean
  SubseaInfrastructureSummaryService subseaInfrastructureSummaryService;

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

    when(subseaInfrastructureService.getFacilityRestUrl()).thenReturn("testUrl");
  }

  @Test
  public void getSubseaStructures_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubseaInfrastructureController.class).getSubseaStructures(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getSubseaStructures_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubseaInfrastructureController.class).getSubseaStructures(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void addSubseaInfrastructure_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubseaInfrastructureController.class).addSubseaInfrastructure(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void addSubseaInfrastructure_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubseaInfrastructureController.class).addSubseaInfrastructure(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getSubseaInfrastructure_whenAuthenticated_thenAccess() throws Exception {

    when(subseaInfrastructureService.getForm(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(new SubseaInfrastructureForm());

    mockMvc.perform(get(ReverseRouter.route(
        on(SubseaInfrastructureController.class).getSubseaInfrastructure(PROJECT_ID, SUBSEA_INFRASTRUCTURE_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getSubseaInfrastructure_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubseaInfrastructureController.class).getSubseaInfrastructure(PROJECT_ID, SUBSEA_INFRASTRUCTURE_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void createSubseaInfrastructure_whenUnauthenticatedPartialSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new SubseaInfrastructureForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(subseaInfrastructureService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(SubseaInfrastructureController.class)
            .createSubseaInfrastructure(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(subseaInfrastructureService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(subseaInfrastructureService, times(0)).createSubseaInfrastructure(any(), any());
  }

  @Test
  public void createSubseaInfrastructure_whenUnauthenticatedFullSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new SubseaInfrastructureForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(subseaInfrastructureService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(SubseaInfrastructureController.class)
            .createSubseaInfrastructure(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isForbidden());

    verify(subseaInfrastructureService, times(0)).validate(any(), any(), eq(ValidationType.FULL));
    verify(subseaInfrastructureService, times(0)).createSubseaInfrastructure(any(), any());
  }

  @Test
  public void createSubseaInfrastructure_whenValidFormAndPartialSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new SubseaInfrastructureForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(subseaInfrastructureService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(SubseaInfrastructureController.class)
            .createSubseaInfrastructure(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(subseaInfrastructureService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(subseaInfrastructureService, times(1)).createSubseaInfrastructure(any(), any());
  }

  @Test
  public void createSubseaInfrastructure_whenValidFormAndFullSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new SubseaInfrastructureForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(subseaInfrastructureService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(SubseaInfrastructureController.class)
            .createSubseaInfrastructure(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(subseaInfrastructureService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(subseaInfrastructureService, times(1)).createSubseaInfrastructure(any(), any());
  }

  @Test
  public void createSubseaInfrastructure_whenInvalidFormAndFullSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new SubseaInfrastructureForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(subseaInfrastructureService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(SubseaInfrastructureController.class)
            .createSubseaInfrastructure(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isOk());

    verify(subseaInfrastructureService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(subseaInfrastructureService, times(0)).createSubseaInfrastructure(any(), any());
  }

  @Test
  public void createSubseaInfrastructure_whenInvalidFormAndPartialSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new SubseaInfrastructureForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(subseaInfrastructureService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(SubseaInfrastructureController.class)
            .createSubseaInfrastructure(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isOk());

    verify(subseaInfrastructureService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(subseaInfrastructureService, times(0)).createSubseaInfrastructure(any(), any());
  }

  @Test
  public void removeSubseaInfrastructuresConfirmation_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubseaInfrastructureController.class).removeSubseaInfrastructuresConfirmation(
            PROJECT_ID,
            SUBSEA_INFRASTRUCTURE_ID,
            DISPLAY_ORDER,
            null
        )))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void removeSubseaInfrastructuresConfirmation_whenAuthenticated_thenAccess() throws Exception {

    when(subseaInfrastructureSummaryService.getSubseaInfrastructureSummaryView(
        SUBSEA_INFRASTRUCTURE_ID,
        projectDetail,
        DISPLAY_ORDER
    )).thenReturn(
        SubseaInfrastructureTestUtil.createSubseaInfrastructureView()
    );

    mockMvc.perform(get(ReverseRouter.route(
        on(SubseaInfrastructureController.class).removeSubseaInfrastructuresConfirmation(
            PROJECT_ID,
            SUBSEA_INFRASTRUCTURE_ID,
            DISPLAY_ORDER,
            null
        )))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void removeSubseaInfrastructure_whenAuthenticated_thenAccess() throws Exception {

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withDevUkFacility();

    when(subseaInfrastructureService.getSubseaInfrastructure(any(), any())).thenReturn(
        subseaInfrastructure
    );

    mockMvc.perform(
        post(ReverseRouter.route(on(SubseaInfrastructureController.class)
            .removeSubseaInfrastructure(PROJECT_ID, SUBSEA_INFRASTRUCTURE_ID, DISPLAY_ORDER, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(subseaInfrastructureService, times(1)).deleteSubseaInfrastructure(subseaInfrastructure);

  }

  @Test
  public void removeSubseaInfrastructure_whenUnauthenticated_thenNoAccess() throws Exception {

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withDevUkFacility();

    when(subseaInfrastructureService.getSubseaInfrastructure(any(), any())).thenReturn(
        subseaInfrastructure
    );

    mockMvc.perform(
        post(ReverseRouter.route(on(SubseaInfrastructureController.class)
            .removeSubseaInfrastructure(PROJECT_ID, SUBSEA_INFRASTRUCTURE_ID, DISPLAY_ORDER, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isForbidden());

    verify(subseaInfrastructureService, times(0)).deleteSubseaInfrastructure(subseaInfrastructure);

  }

}