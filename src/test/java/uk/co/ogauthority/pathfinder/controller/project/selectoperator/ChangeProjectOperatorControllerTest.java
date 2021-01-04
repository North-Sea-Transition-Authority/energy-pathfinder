package uk.co.ogauthority.pathfinder.controller.project.selectoperator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.selectoperator.SelectOperatorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ChangeProjectOperatorController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class ChangeProjectOperatorControllerTest extends ProjectContextAbstractControllerTest {
  private static final Integer PROJECT_ID = 1;

  @MockBean
  private SelectOperatorService selectOperatorService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  @Before
  public void setUp() {
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(detail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    when(selectOperatorService.getSelectOperatorModelAndView(
        any(),
        any(),
        any(),
        any()
    )).thenReturn(new ModelAndView("test/blankTemplate")); //Just return a model and view that needs no params.
  }

  @Test
  public void startProject_validForm() throws Exception {
    when(selectOperatorService.getOrganisationGroupOrError(any(), any())).thenReturn(
        ProjectOperatorTestUtil.ORG_GROUP
    );


    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(SelectProjectOperatorController.PRIMARY_BUTTON_TEXT, SelectProjectOperatorController.PRIMARY_BUTTON_TEXT);
      add("organisationGroup", "1");
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectOperatorForm.class, "form");
    when(selectOperatorService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ChangeProjectOperatorController.class)
            .saveProjectOperator(null, PROJECT_ID, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(selectOperatorService, times(1)).updateProjectOperator(any(), any());
  }

  @Test
  public void startProject_inValidForm() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ChangeProjectOperatorController.PRIMARY_BUTTON_TEXT, ChangeProjectOperatorController.PRIMARY_BUTTON_TEXT);
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectOperatorForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(selectOperatorService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ChangeProjectOperatorController.class)
            .saveProjectOperator(null, PROJECT_ID, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(selectOperatorService, times(0)).updateProjectOperator(any(), any());
  }
}
