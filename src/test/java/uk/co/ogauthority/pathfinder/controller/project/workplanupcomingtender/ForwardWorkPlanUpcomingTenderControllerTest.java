package uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderCompletionForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderSetupForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.ForwardWorkPlanUpcomingTenderViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanTenderCompletionService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanTenderRoutingService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanTenderSetupService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderModelService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderSummaryService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ForwardWorkPlanUpcomingTenderController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class ForwardWorkPlanUpcomingTenderControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer UPCOMING_TENDER_ID = 1;
  private static final Integer DISPLAY_ORDER = 1;

  @MockBean
  private ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  @MockBean
  private ForwardWorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService;

  @MockBean
  private ForwardWorkPlanUpcomingTenderModelService workPlanUpcomingTenderModelService;

  @MockBean
  protected ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;

  @MockBean
  protected ForwardWorkPlanTenderRoutingService forwardWorkPlanTenderRoutingService;

  @MockBean
  protected ForwardWorkPlanTenderCompletionService forwardWorkPlanTenderCompletionService;

  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  private final ForwardWorkPlanUpcomingTender workPlanUpcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);

  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.FORWARD_WORK_PLAN);

  private final Set<ProjectPermission> requiredPermissions = ProjectControllerTesterService.PROJECT_CREATE_PERMISSION_SET;

  private ProjectControllerTesterService projectControllerTesterService;

  @Before
  public void setup() {
    when(workPlanUpcomingTenderService.getOrError(UPCOMING_TENDER_ID)).thenReturn(workPlanUpcomingTender);
    var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        workPlanUpcomingTender,
        DISPLAY_ORDER
    );
    when(workPlanUpcomingTenderSummaryService.getUpcomingTenderView(workPlanUpcomingTender, DISPLAY_ORDER)).thenReturn(upcomingTenderView);
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(projectDetail, unauthenticatedUser)).thenReturn(false);
    when(workPlanUpcomingTenderService.createUpcomingTender(any(), any())).thenReturn(ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail));
    when(workPlanUpcomingTenderService.updateUpcomingTender(any(), any())).thenReturn(ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail));
    when(workPlanUpcomingTenderModelService.getUpcomingTenderFormModelAndView(eq(projectDetail), any())).thenReturn(new ModelAndView(""));

    projectControllerTesterService = new ProjectControllerTesterService(
        mockMvc,
        projectOperatorService,
        projectContributorsCommonService,
        teamService
    );
  }

  @Test
  public void authenticatedUser_hasAccessToUpcomingTender() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ForwardWorkPlanUpcomingTenderController.class).viewUpcomingTenders(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessUpcomingTender() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ForwardWorkPlanUpcomingTenderController.class).viewUpcomingTenders(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_canAddUpcomingTender() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ForwardWorkPlanUpcomingTenderController.class).addUpcomingTender(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAddUpcomingTender() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ForwardWorkPlanUpcomingTenderController.class).addUpcomingTender(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToUpcomingTenderEdit() throws Exception {
    when(workPlanUpcomingTenderService.getForm(workPlanUpcomingTender)).thenReturn(ForwardWorkPlanUpcomingTenderUtil.getCompleteForm());
    mockMvc.perform(get(ReverseRouter.route(
        on(ForwardWorkPlanUpcomingTenderController.class).editUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessToUpcomingTenderEdit() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ForwardWorkPlanUpcomingTenderController.class).editUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToUpcomingTenderRemove() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ForwardWorkPlanUpcomingTenderController.class).removeUpcomingTenderConfirm(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessUpcomingTenderRemove() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ForwardWorkPlanUpcomingTenderController.class).removeUpcomingTenderConfirm(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveUpcomingTender_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanUpcomingTenderForm.class, "form");
    when(workPlanUpcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .saveUpcomingTender(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(workPlanUpcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(workPlanUpcomingTenderService, times(1)).createUpcomingTender(any(), any());
  }

   @Test
   public void saveUpcomingTender_fullValidation_invalid() throws Exception {
     MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
     add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
     }};

     var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanUpcomingTenderForm.class, "form");
     bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
     when(workPlanUpcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

     mockMvc.perform(
         post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
             .saveUpcomingTender(PROJECT_ID, null, null, null, null)
         ))
             .with(authenticatedUserAndSession(authenticatedUser))
             .with(csrf())
             .params(completeParams))
         .andExpect(status().is2xxSuccessful());

     verify(workPlanUpcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
     verify(workPlanUpcomingTenderService, times(0)).createUpcomingTender(any(), any());
   }

  @Test
  public void saveUpcomingTender_partialValidation_valid() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanUpcomingTenderForm.class, "form");
    when(workPlanUpcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .saveUpcomingTender(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(workPlanUpcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(workPlanUpcomingTenderService, times(1)).createUpcomingTender(any(), any());
  }

  @Test
  public void saveUpcomingTender_partialValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanUpcomingTenderForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(workPlanUpcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .saveUpcomingTender(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is2xxSuccessful());

    verify(workPlanUpcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(workPlanUpcomingTenderService, times(0)).createUpcomingTender(any(), any());
  }

  @Test
  public void updateUpcomingTender_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanUpcomingTenderForm.class, "form");
    when(workPlanUpcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .updateUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(workPlanUpcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(workPlanUpcomingTenderService, times(1)).updateUpcomingTender(any(), any());
  }

  @Test
  public void updateUpcomingTender_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanUpcomingTenderForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(workPlanUpcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .updateUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(workPlanUpcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(workPlanUpcomingTenderService, times(0)).updateUpcomingTender(any(), any());
  }

  @Test
  public void updateUpcomingTender_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanUpcomingTenderForm.class, "form");
    when(workPlanUpcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .updateUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(workPlanUpcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(workPlanUpcomingTenderService, times(1)).updateUpcomingTender(any(), any());

  }

  @Test
  public void removeUpcomingTender_authenticatedUser_thenValid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};
    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .removeUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(workPlanUpcomingTenderService, times(1)).getOrError(any());
    verify(workPlanUpcomingTenderService, times(1)).delete(any());
  }

  @Test
  public void removeUpcomingTender_unAuthenticatedUser_thenInvalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};
    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .removeUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isForbidden());

    verify(workPlanUpcomingTenderService, never()).getOrError(any());
    verify(workPlanUpcomingTenderService, never()).delete(any());
  }

  @Test
  public void saveUpcomingTenders_unAuthenticated_thenInvalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .saveUpcomingTenders(PROJECT_ID, null, null, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveUpcomingTenders_authenticatedAndInvalid_thenStayOnSummary() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    when(workPlanUpcomingTenderSummaryService.validateViews(any())).thenReturn(ValidationResult.INVALID);

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .saveUpcomingTenders(PROJECT_ID, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isOk());

    verify(forwardWorkPlanTenderCompletionService, never()).saveForwardWorkPlanTenderCompletionForm(any(), any());
    verify(forwardWorkPlanTenderRoutingService, never()).getPostSaveUpcomingTendersRoute(any(), any());
  }

  @Test
  public void saveUpcomingTenders_authenticatedAndValid_thenRedirect() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    when(workPlanUpcomingTenderSummaryService.validateViews(any())).thenReturn(ValidationResult.VALID);

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanTenderCompletionForm.class, "form");
    when(forwardWorkPlanTenderCompletionService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .saveUpcomingTenders(PROJECT_ID, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        // this is an ok status as the forwardWorkPlanTenderRoutingService determines which route to
        // return. As this is a mock for the test it returns null and hence an ok status. The call to
        // the routing service is verified below
        .andExpect(status().isOk());

    verify(forwardWorkPlanTenderCompletionService, times(1)).saveForwardWorkPlanTenderCompletionForm(any(), any());
    verify(forwardWorkPlanTenderRoutingService, times(1)).getPostSaveUpcomingTendersRoute(any(), any());
  }

  @Test
  public void getUpcomingTenderSetup_projectContextSmokeTest() {

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ForwardWorkPlanUpcomingTenderController.class).getUpcomingTenderSetup(
            projectDetail.getProject().getId(),
            null,
            null
        ),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void saveUpcomingTenderSetup_projectContextSmokeTest() {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanTenderSetupForm.class, "form");

    when(forwardWorkPlanTenderSetupService.validate(any(), any(), any())).thenReturn(bindingResult);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ForwardWorkPlanUpcomingTenderController.class).saveUpcomingTenderSetup(
            projectDetail.getProject().getId(),
            null,
            null,
            null,
            null
        ),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void saveUpcomingTenderSetup_whenValidForm_verifyInteractions() throws Exception {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanTenderSetupForm.class, "form");

    when(forwardWorkPlanTenderSetupService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .saveUpcomingTenderSetup(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(forwardWorkPlanTenderSetupService, times(1)).saveForwardWorkPlanTenderSetup(
        any(),
        eq(projectDetail)
    );

    verify(forwardWorkPlanTenderRoutingService, times(1)).getPostSaveUpcomingTenderSetupRoute(
        any(),
        eq(projectDetail)
    );
  }

  @Test
  public void saveUpcomingTenderSetup_whenInvalidForm_verifyInteractions() throws Exception {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanTenderSetupForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(forwardWorkPlanTenderSetupService.validate(any(), any(), any())).thenReturn(bindingResult);

    when(workPlanUpcomingTenderModelService.getUpcomingTenderSetupModelAndView(eq(projectDetail), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class)
            .saveUpcomingTenderSetup(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(forwardWorkPlanTenderSetupService, never()).saveForwardWorkPlanTenderSetup(
        any(),
        eq(projectDetail)
    );

    verify(forwardWorkPlanTenderRoutingService, never()).getPostSaveUpcomingTenderSetupRoute(
        any(),
        eq(projectDetail)
    );

    verify(workPlanUpcomingTenderModelService, times(1)).getUpcomingTenderSetupModelAndView(
        eq(projectDetail),
        any()
    );
  }
}
