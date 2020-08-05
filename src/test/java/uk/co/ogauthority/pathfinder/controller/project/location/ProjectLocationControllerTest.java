package uk.co.ogauthority.pathfinder.controller.project.location;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(ProjectLocationController.class)
public class ProjectLocationControllerTest extends AbstractControllerTest {
  private static final Integer PROJECT_ID = 1;

  @MockBean
  private ProjectService projectService;

  @MockBean
  private ProjectLocationService projectLocationService;

  private ProjectDetail details = ProjectUtil.getProjectDetails();


  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();


  @Before
  public void setUp() throws Exception {
    when(projectService.getLatestDetail(PROJECT_ID)).thenReturn(Optional.of(details));
  }

  @Test
  public void authenticatedUser_hasAccessToProjectLocation() throws Exception {
    when(projectLocationService.getForm(details)).thenReturn(new ProjectLocationForm());
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectLocationController.class).getLocationDetails(authenticatedUser, PROJECT_ID)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessProjecLocation() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectLocationController.class).getLocationDetails(unAuthenticatedUser, PROJECT_ID)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveProjectLocation_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectLocationForm.class, "form");
    when(projectLocationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectLocationController.class)
            .saveProjectLocation(null, PROJECT_ID, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(projectLocationService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(projectLocationService, times(1)).createOrUpdate(any(), any());
  }



  @Test
  public void saveProjectLocation_fullValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectLocationForm.class, "form");
    bindingResult.addError(new ObjectError("Error", "ErrorMessage"));
    when(projectLocationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectLocationController.class)
            .saveProjectLocation(null, PROJECT_ID, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is2xxSuccessful());

    verify(projectLocationService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(projectLocationService, times(0)).createOrUpdate(any(), any());
  }

  @Test
  public void saveProjectLocation_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
      add("fieldid", "123");
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectLocationForm.class, "form");
    when(projectLocationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectLocationController.class)
            .saveProjectLocation(null, PROJECT_ID, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(projectLocationService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(projectLocationService, times(1)).createOrUpdate(any(), any());
  }

}
