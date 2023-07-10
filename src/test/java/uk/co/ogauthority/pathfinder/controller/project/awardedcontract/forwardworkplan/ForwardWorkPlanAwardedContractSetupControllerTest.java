package uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.mvc.ReverseRouter.route;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupForm;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ForwardWorkPlanAwardedContractSetupController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class ForwardWorkPlanAwardedContractSetupControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Class<ForwardWorkPlanAwardedContractSetupController> CONTROLLER = ForwardWorkPlanAwardedContractSetupController.class;

  @MockBean
  private ForwardWorkPlanAwardedContractSetupService setupService;

  @MockBean
  private ValidationService validator;

  private ProjectDetail projectDetail;

  private AuthenticatedUserAccount authenticatedUser;

  private AuthenticatedUserAccount unauthenticatedUser;

  private ArgumentCaptor<ForwardWorkPlanAwardedContractSetupForm> formCaptor;
  private ArgumentCaptor<BindingResult> bindingResultCaptor;

  @Before
  public void setUp() {
    projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.CREATE_PROJECT_PRIVILEGES);
    unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    formCaptor = ArgumentCaptor.forClass(ForwardWorkPlanAwardedContractSetupForm.class);
    bindingResultCaptor = ArgumentCaptor.forClass(BindingResult.class);

    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(projectDetail, unauthenticatedUser)).thenReturn(false);
  }

  @Test
  public void getAwardedContractSetup_authenticatedUser_thenReturnEmptyForm() throws Exception {
    var form = new ForwardWorkPlanAwardedContractSetupForm();
    when(setupService.getForwardWorkPlanAwardedContractSetupFormFromDetail(projectDetail)).thenReturn(form);

    var modelAndView = mockMvc.perform(
        get(route(on(CONTROLLER).getAwardedContractSetup(PROJECT_ID, null, null)))
            .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk())
        .andExpect(view().name(ForwardWorkPlanAwardedContractSetupController.SETUP_TEMPLATE_PATH))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertThat(model).contains(
        entry("pageName", ForwardWorkPlanAwardedContractSetupController.PAGE_NAME),
        entry("form", form),
        entry("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(PROJECT_ID))
    );
  }

  @Test
  public void getAwardedContractSetup_unauthenticatedUser_thenForbidden() throws Exception {
    var form = new ForwardWorkPlanAwardedContractSetupForm();
    when(setupService.getForwardWorkPlanAwardedContractSetupFormFromDetail(projectDetail)).thenReturn(form);

    mockMvc.perform(
        get(route(on(CONTROLLER).getAwardedContractSetup(PROJECT_ID, null, null)))
            .with(authenticatedUserAndSession(unauthenticatedUser)))
    .andExpect(status().isForbidden())
    .andReturn()
    .getModelAndView();
  }

  @Test
  public void saveAwardedContractSetup_authenticatedUserAndValidForm() throws Exception {
    var expectedBindingResult = new BeanPropertyBindingResult(ForwardWorkPlanAwardedContractSetupForm.class, "form");
    when(validator.validate(any(), any(), eq(ValidationType.FULL)))
        .thenReturn(expectedBindingResult);

    mockMvc.perform(post(route(on(CONTROLLER).saveAwardedContractSetup(PROJECT_ID, null, null, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf())
        .param("hasContractToAdd", "true")
    ).andExpect(status().is3xxRedirection());

    verify(validator).validate(
        formCaptor.capture(),
        bindingResultCaptor.capture(),
        eq(ValidationType.FULL)
    );

    var form = formCaptor.getValue();
    assertThat(form.getHasContractToAdd()).isTrue();

    var bindingResult = bindingResultCaptor.getValue();
    assertThat(bindingResult.hasErrors()).isFalse();

    verify(setupService).saveAwardedContractSetup(form, projectDetail);
  }

  @Test
  public void saveAwardedContractSetup_authenticatedUserAndInvalidForm() throws Exception {
    var validationMessage = "validationMessage";
    doAnswer(invocation -> {
      var bindingResult = invocation.getArgument(1, BindingResult.class);
      bindingResult.rejectValue("hasContractToAdd", "hasContractToAdd.required", validationMessage);
      return null;
    }).when(validator).validate(
        any(ForwardWorkPlanAwardedContractSetupForm.class),
        any(BindingResult.class),
        eq(ValidationType.FULL)
    );

    mockMvc.perform(post(route(on(CONTROLLER).saveAwardedContractSetup(PROJECT_ID, null, null, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf())
        )
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name(ForwardWorkPlanAwardedContractSetupController.SETUP_TEMPLATE_PATH));

    verify(validator).validate(
        formCaptor.capture(),
        bindingResultCaptor.capture(),
        eq(ValidationType.FULL)
    );

    var form = formCaptor.getValue();
    assertThat(form.getHasContractToAdd()).isNull();

    var bindingResult = bindingResultCaptor.getValue();
    assertThat(bindingResult.getAllErrors())
        .hasSize(1)
        .first().matches(error -> validationMessage.equals(error.getDefaultMessage()));

    verify(setupService, never()).saveAwardedContractSetup(form, projectDetail);
  }

}
