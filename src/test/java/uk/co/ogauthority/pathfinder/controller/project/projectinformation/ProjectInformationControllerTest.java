package uk.co.ogauthority.pathfinder.controller.project.projectinformation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
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
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetails;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(ProjectInformationController.class)
public class ProjectInformationControllerTest extends AbstractControllerTest {

  private static final Integer PROJECT_ID = 1;

  @MockBean
  private ProjectService projectService;

  @MockBean
  private BreadcrumbService breadcrumbService;

  @MockBean
  private ProjectInformationService projectInformationService;
  
  private ProjectDetails details = ProjectUtil.getProjectDetails();


  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();


  @Before
  public void setUp() throws Exception {
    doCallRealMethod().when(breadcrumbService).fromTaskList(any(), any(), any());
    when(projectService.getLatestDetail(PROJECT_ID)).thenReturn(Optional.of(details));
  }

  @Test
  public void authenticatedUser_hasAccessToProjectInformation() throws Exception {
    when(projectInformationService.getForm(details)).thenReturn(new ProjectInformationForm());
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectInformationController.class).getProjectInformation(authenticatedUser, PROJECT_ID)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessProjectInformation() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectInformationController.class).getProjectInformation(unAuthenticatedUser, PROJECT_ID)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveProjectInformation_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add("Save and complete later", "Save and complete later");
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectInformationForm.class, "form");
    when(projectInformationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectInformationController.class)
            .saveProjectInformation(null, PROJECT_ID, null, null, null)
          ))
          .with(authenticatedUserAndSession(authenticatedUser))
          .with(csrf())
          .params(completeLaterParams))
      .andExpect(status().is3xxRedirection());

    verify(projectInformationService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(projectInformationService, times(1)).createOrUpdate(any(), any());
  }



  @Test
  public void saveProjectInformation_fullValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add("Complete", "Complete");
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectInformationForm.class, "form");
    bindingResult.addError(new ObjectError("Error", "ErrorMessage"));
    when(projectInformationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectInformationController.class)
            .saveProjectInformation(null, PROJECT_ID, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is2xxSuccessful());

    verify(projectInformationService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(projectInformationService, times(0)).createOrUpdate(any(), any());
  }
}
