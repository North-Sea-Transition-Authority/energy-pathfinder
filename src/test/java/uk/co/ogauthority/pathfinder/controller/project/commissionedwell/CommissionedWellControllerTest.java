package uk.co.ogauthority.pathfinder.controller.project.commissionedwell;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.commissionedwell.CommissionedWellModelService;
import uk.co.ogauthority.pathfinder.service.project.commissionedwell.CommissionedWellScheduleService;
import uk.co.ogauthority.pathfinder.service.project.commissionedwell.CommissionedWellScheduleSummaryService;
import uk.co.ogauthority.pathfinder.service.project.commissionedwell.CommissionedWellScheduleValidationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.CommissionedWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = CommissionedWellController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class CommissionedWellControllerTest extends ProjectContextAbstractControllerTest {

  @MockitoBean
  private CommissionedWellModelService commissionedWellModelService;

  @MockitoBean
  private CommissionedWellScheduleService commissionedWellScheduleService;

  @MockitoBean
  private CommissionedWellScheduleValidationService commissionedWellScheduleValidationService;

  @MockitoBean
  CommissionedWellScheduleSummaryService commissionedWellScheduleSummaryService;

  private ProjectControllerTesterService projectControllerTesterService;

  private final int projectId = 1;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES
  );

  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);

  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.INFRASTRUCTURE);

  private final Set<ProjectPermission> requiredPermissions = ProjectControllerTesterService.PROJECT_CREATE_PERMISSION_SET;

  @Before
  public void setup() {
    projectControllerTesterService = new ProjectControllerTesterService(
        mockMvc,
        projectOperatorService,
        projectContributorsCommonService,
        teamService
    );

    when(projectService.getLatestDetailOrError(projectId)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUser)).thenReturn(true);
  }

  @Test
  public void viewWellsToCommission_projectContextSmokeTest() {
    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CommissionedWellController.class).viewWellsToCommission(projectId, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void addCommissioningSchedule_projectContextSmokeTest() {
    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CommissionedWellController.class).addCommissioningSchedule(projectId, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void createCommissioningSchedule_projectContextSmokeTest() {

    var bindingResult = new BeanPropertyBindingResult(CommissionedWellForm.class, "form");
    when(commissionedWellScheduleValidationService.validate(any(), any(), any())).thenReturn(bindingResult);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CommissionedWellController.class).createCommissioningSchedule(projectId, null, null, null, null),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void createCommissioningSchedule_whenFullValidationAndValidForm_thenRedirectToSummaryPage() throws Exception {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new CommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(commissionedWellScheduleValidationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(CommissionedWellController.class)
            .createCommissioningSchedule(projectId, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name(getViewCommissionedWellsSummaryUrl(projectId)));

    verify(commissionedWellScheduleValidationService, times(1)).validate(
        eq(form),
        any(),
        eq(ValidationType.FULL)
    );

    verify(commissionedWellScheduleService, times(1)).createCommissionWellSchedule(
        form,
        projectDetail
    );
  }

  @Test
  public void createCommissioningSchedule_whenFullValidationAndInvalidForm_thenNavigateToFormPage() throws Exception {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var validationType = ValidationType.FULL;

    var form = new CommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(commissionedWellScheduleValidationService.validate(any(), any(), any())).thenReturn(bindingResult);

    when(commissionedWellModelService.getCommissionedWellModelAndView(
        projectDetail,
        form
    )).thenReturn(new ModelAndView());

    mockMvc.perform(
            post(ReverseRouter.route(on(CommissionedWellController.class)
                .createCommissioningSchedule(projectId, form, bindingResult, validationType, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().isOk())
        .andExpect(view().name(getAddWellCommissioningScheduleUrl(projectId)));

    verify(commissionedWellScheduleValidationService, times(1)).validate(
        eq(form),
        any(),
        eq(validationType)
    );

    verify(commissionedWellScheduleService, never()).createCommissionWellSchedule(
        any(),
        any()
    );
  }

  @Test
  public void createCommissioningSchedule_whenPartialValidationAndValidForm_thenRedirectToTaskList() throws Exception {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var validationType = ValidationType.PARTIAL;

    var form = new CommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(commissionedWellScheduleValidationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(CommissionedWellController.class)
                .createCommissioningSchedule(projectId, form, bindingResult, validationType, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name(getViewCommissionedWellsSummaryUrl(projectId)));

    verify(commissionedWellScheduleValidationService, times(1)).validate(
        eq(form),
        any(),
        eq(validationType)
    );

    verify(commissionedWellScheduleService, times(1)).createCommissionWellSchedule(
        form,
        projectDetail
    );
  }

  @Test
  public void createCommissioningSchedule_whenPartialValidationAndInvalidForm_thenNavigateToFormPage() throws Exception {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var validationType = ValidationType.PARTIAL;

    var form = new CommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(commissionedWellScheduleValidationService.validate(any(), any(), any())).thenReturn(bindingResult);

    when(commissionedWellModelService.getCommissionedWellModelAndView(
        projectDetail,
        form
    )).thenReturn(new ModelAndView());

    mockMvc.perform(
            post(ReverseRouter.route(on(CommissionedWellController.class)
                .createCommissioningSchedule(projectId, form, bindingResult, validationType, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().isOk())
        .andExpect(view().name(getAddWellCommissioningScheduleUrl(projectId)));

    verify(commissionedWellScheduleValidationService, times(1)).validate(
        eq(form),
        any(),
        eq(validationType)
    );

    verify(commissionedWellScheduleService, never()).createCommissionWellSchedule(
        any(),
        any()
    );
  }

  @Test
  public void getCommissionedWellSchedule_projectContextSmokeTest() {
    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    var commissionedWellScheduleId = 10;

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenReturn(CommissionedWellTestUtil.getCommissionedWellSchedule());

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CommissionedWellController.class).getCommissionedWellSchedule(projectId, commissionedWellScheduleId, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void getCommissionedWellSchedule_whenNoCommissionedWellScheduleFound_verifyInteractions() throws Exception {

    var commissionedWellScheduleId = 10;

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenThrow(PathfinderEntityNotFoundException.class);

    mockMvc.perform(
        get(ReverseRouter.route(on(CommissionedWellController.class).getCommissionedWellSchedule(
            projectId,
            commissionedWellScheduleId,
            null
        )))
            .with(authenticatedUserAndSession(authenticatedUser))
    )
        .andExpect(status().is4xxClientError());

    verify(commissionedWellScheduleService, never()).getCommissionedWellsForSchedule(any());
    verify(commissionedWellScheduleService, never()).getForm(any(), anyList());
    verify(commissionedWellModelService, never()).getCommissionedWellModelAndView(any(), any());
  }

  @Test
  public void getCommissionedWellSchedule_whenCommissionedWellScheduleFound_verifyInteractions() throws Exception {

    var commissionedWellScheduleId = 10;
    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();
    commissionedWellSchedule.setProjectDetail(projectDetail);

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenReturn(commissionedWellSchedule);

    mockMvc.perform(
            get(ReverseRouter.route(on(CommissionedWellController.class).getCommissionedWellSchedule(
                projectId,
                commissionedWellScheduleId,
                null
            )))
                .with(authenticatedUserAndSession(authenticatedUser))
        )
        .andExpect(status().isOk());

    verify(commissionedWellScheduleService, times(1)).getCommissionedWellsForSchedule(commissionedWellSchedule);
    verify(commissionedWellScheduleService, times(1)).getForm(eq(commissionedWellSchedule), anyList());
    verify(commissionedWellModelService, times(1)).getCommissionedWellModelAndView(eq(projectDetail), any());
  }

  @Test
  public void updateCommissionedWellSchedule_projectContextSmokeTest() {

    var bindingResult = new BeanPropertyBindingResult(CommissionedWellForm.class, "form");
    when(commissionedWellScheduleValidationService.validate(any(), any(), any())).thenReturn(bindingResult);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);

    var commissionedWellScheduleId = 10;

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenReturn(CommissionedWellTestUtil.getCommissionedWellSchedule());

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CommissionedWellController.class).updateCommissionedWellSchedule(
            projectId,
            commissionedWellScheduleId,
            null,
            null,
            null,
            null
        ),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void updateCommissionedWellSchedule_whenNoCommissionedWellScheduleFound_verifyInteractions() throws Exception {

    var commissionedWellScheduleId = 10;

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenThrow(PathfinderEntityNotFoundException.class);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new CommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(commissionedWellScheduleValidationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(CommissionedWellController.class)
                .updateCommissionedWellSchedule(projectId, commissionedWellScheduleId, form, bindingResult, ValidationType.FULL, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().is4xxClientError());

    verify(commissionedWellScheduleValidationService, never()).validate(any(), any(), any());
    verify(commissionedWellModelService, never()).getCommissionedWellModelAndView(any(), any());
    verify(commissionedWellScheduleService, never()).updateCommissionedWellSchedule(any(), any());
  }

  @Test
  public void updateCommissionedWellSchedule_whenFullValidationAndValidForm_thenRedirectToSummaryPage() throws Exception {

    var commissionedWellScheduleId = 10;
    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenReturn(commissionedWellSchedule);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new CommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(commissionedWellScheduleValidationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(CommissionedWellController.class)
                .updateCommissionedWellSchedule(projectId, commissionedWellScheduleId, form, bindingResult, ValidationType.FULL, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name(getViewCommissionedWellsSummaryUrl(projectId)));

    verify(commissionedWellScheduleValidationService, times(1)).validate(
        eq(form),
        any(),
        eq(ValidationType.FULL)
    );

    verify(commissionedWellScheduleService, times(1)).updateCommissionedWellSchedule(
        commissionedWellSchedule,
        form
    );
  }

  @Test
  public void updateCommissionedWellSchedule_whenFullValidationAndInvalidForm_thenNavigateToFormPage() throws Exception {

    var commissionedWellScheduleId = 10;
    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenReturn(commissionedWellSchedule);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var validationType = ValidationType.FULL;

    var form = new CommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(commissionedWellScheduleValidationService.validate(any(), any(), any())).thenReturn(bindingResult);

    when(commissionedWellModelService.getCommissionedWellModelAndView(
        projectDetail,
        form
    )).thenReturn(new ModelAndView());

    mockMvc.perform(
            post(ReverseRouter.route(on(CommissionedWellController.class)
                .updateCommissionedWellSchedule(projectId, commissionedWellScheduleId, form, bindingResult, validationType, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().isOk())
        .andExpect(view().name(getEditWellCommissioningScheduleUrl(projectId, commissionedWellScheduleId)));

    verify(commissionedWellScheduleValidationService, times(1)).validate(
        eq(form),
        any(),
        eq(validationType)
    );

    verify(commissionedWellScheduleService, never()).updateCommissionedWellSchedule(
        any(),
        any()
    );
  }

  @Test
  public void updateCommissionedWellSchedule_whenPartialValidationAndValidForm_thenRedirectToTaskList() throws Exception {

    var commissionedWellScheduleId = 10;
    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenReturn(commissionedWellSchedule);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var validationType = ValidationType.PARTIAL;

    var form = new CommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(commissionedWellScheduleValidationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(CommissionedWellController.class)
                .updateCommissionedWellSchedule(projectId, commissionedWellScheduleId, form, bindingResult, validationType, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name(getViewCommissionedWellsSummaryUrl(projectId)));

    verify(commissionedWellScheduleValidationService, times(1)).validate(
        eq(form),
        any(),
        eq(validationType)
    );

    verify(commissionedWellScheduleService, times(1)).updateCommissionedWellSchedule(
        commissionedWellSchedule,
        form
    );
  }

  @Test
  public void updateCommissionedWellSchedule_whenPartialValidationAndInvalidForm_thenNavigateToFormPage() throws Exception {

    var commissionedWellScheduleId = 10;
    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenReturn(commissionedWellSchedule);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var validationType = ValidationType.PARTIAL;

    var form = new CommissionedWellForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(commissionedWellScheduleValidationService.validate(any(), any(), any())).thenReturn(bindingResult);

    when(commissionedWellModelService.getCommissionedWellModelAndView(
        projectDetail,
        form
    )).thenReturn(new ModelAndView());

    mockMvc.perform(
            post(ReverseRouter.route(on(CommissionedWellController.class)
                .updateCommissionedWellSchedule(projectId, commissionedWellScheduleId, form, bindingResult, validationType, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(params))
        .andExpect(status().isOk())
        .andExpect(view().name(getEditWellCommissioningScheduleUrl(projectId, commissionedWellScheduleId)));

    verify(commissionedWellScheduleValidationService, times(1)).validate(
        eq(form),
        any(),
        eq(validationType)
    );

    verify(commissionedWellScheduleService, never()).updateCommissionedWellSchedule(
        any(),
        any()
    );
  }

  @Test
  public void removeCommissionedWellScheduleConfirmation_projectContextSmokeTest() {
    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    var commissionedWellScheduleId = 10;
    var displayOrder = 100;
    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenReturn(commissionedWellSchedule);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CommissionedWellController.class).removeCommissionedWellScheduleConfirmation(
            projectId,
            commissionedWellScheduleId,
            displayOrder,
            null
        ),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void removeCommissionedWellScheduleConfirmation_whenNoCommissionedWellScheduleFound_then404() throws Exception {

    var commissionedWellScheduleId = 10;
    var displayOrder = 10;

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenThrow(PathfinderEntityNotFoundException.class);

    mockMvc.perform(
        get(ReverseRouter.route(on(CommissionedWellController.class).removeCommissionedWellScheduleConfirmation(
            projectId,
            commissionedWellScheduleId,
            displayOrder,
            null
        )))
            .with(authenticatedUserAndSession(authenticatedUser))
    )
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void removeCommissionedWellScheduleConfirmation_whenCommissionedWellScheduleFound_thenVerifyInteractions() throws Exception {

    var commissionedWellScheduleId = 10;
    var displayOrder = 10;
    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenReturn(commissionedWellSchedule);

    when(commissionedWellModelService.getRemoveCommissionedWellScheduleModelAndView(eq(projectId), any()))
        .thenReturn(new ModelAndView());

    mockMvc.perform(
        get(ReverseRouter.route(on(CommissionedWellController.class).removeCommissionedWellScheduleConfirmation(
            projectId,
            commissionedWellScheduleId,
            displayOrder,
            null
        )))
            .with(authenticatedUserAndSession(authenticatedUser))
    )
        .andExpect(status().isOk())
        .andExpect(view().name(getRemoveWellCommissioningScheduleUrl(projectId, commissionedWellScheduleId, displayOrder)));

    verify(commissionedWellScheduleSummaryService, times(1)).getCommissionedWellScheduleView(
        commissionedWellSchedule,
        displayOrder
    );
  }

  @Test
  public void removeCommissionedWellSchedule_projectContextSmokeTest() {
    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    var commissionedWellScheduleId = 10;
    var displayOrder = 100;
    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenReturn(commissionedWellSchedule);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CommissionedWellController.class).removeCommissionedWellSchedule(
            projectId,
            commissionedWellScheduleId,
            displayOrder,
            null
        ),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void removeCommissionedWellSchedule_whenNoCommissionedWellScheduleFound_then404() throws Exception {

    var commissionedWellScheduleId = 10;
    var displayOrder = 10;

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenThrow(PathfinderEntityNotFoundException.class);

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    mockMvc.perform(
        post(ReverseRouter.route(on(CommissionedWellController.class).removeCommissionedWellSchedule(
            projectId,
            commissionedWellScheduleId,
            displayOrder,
            null
        )))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams)
    )
        .andExpect(status().is4xxClientError());

    verify(commissionedWellScheduleService, never()).deleteCommissionedWellSchedule(any());
  }

  @Test
  public void removeCommissionedWellSchedule_whenCommissionedWellScheduleFound_thenVerifyInteractions() throws Exception {

    var commissionedWellScheduleId = 10;
    var displayOrder = 10;
    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleService.getCommissionedWellScheduleOrError(commissionedWellScheduleId, projectDetail))
        .thenReturn(commissionedWellSchedule);

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    mockMvc.perform(
        post(ReverseRouter.route(on(CommissionedWellController.class).removeCommissionedWellSchedule(
            projectId,
            commissionedWellScheduleId,
            displayOrder,
            null
        )))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams)
    )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name(getViewCommissionedWellsSummaryUrl(projectId)));

    verify(commissionedWellScheduleService, times(1)).deleteCommissionedWellSchedule(
        commissionedWellSchedule
    );
  }

  @Test
  public void completeWellsToCommission_projectContextSmokeTest() {

    when(commissionedWellScheduleSummaryService.determineViewValidationResult(anyList())).thenReturn(ValidationResult.VALID);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CommissionedWellController.class).completeWellsToCommission(
            projectId,
            null
        ),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void completeWellsToCommission_whenValidationResultIsValid_thenRedirectToTaskList() throws Exception {

    when(commissionedWellScheduleSummaryService.determineViewValidationResult(anyList())).thenReturn(ValidationResult.VALID);

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    mockMvc.perform(
        post(ReverseRouter.route(on(CommissionedWellController.class).completeWellsToCommission(
            projectId,
            null
        )))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams)
    )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:" + ControllerUtils.getBackToTaskListUrl(projectId)));

    verify(commissionedWellScheduleSummaryService, times(1)).getValidatedCommissionedWellScheduleViews(
        projectDetail
    );

    verify(commissionedWellModelService, never()).getViewCommissionedWellsModelAndView(
        any(),
        anyList(),
        any()
    );
  }

  @Test
  public void completeWellsToCommission_whenValidationResultIsNotValid_thenRemainOnSummaryPage() throws Exception {

    when(commissionedWellScheduleSummaryService.determineViewValidationResult(anyList())).thenReturn(ValidationResult.INVALID);

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    mockMvc.perform(
        post(ReverseRouter.route(on(CommissionedWellController.class).completeWellsToCommission(
            projectId,
            null
        )))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams)
    )
        .andExpect(status().isOk());

    verify(commissionedWellScheduleSummaryService, times(1)).getValidatedCommissionedWellScheduleViews(
        projectDetail
    );

    verify(commissionedWellModelService, times(1)).getViewCommissionedWellsModelAndView(
        eq(projectDetail),
        anyList(),
        any()
    );
  }

  private String getViewCommissionedWellsSummaryUrl(int projectId) {
    return ReverseRouter.redirect(
        on(CommissionedWellController.class).viewWellsToCommission(projectId, null)
    ).getViewName();
  }

  private String getAddWellCommissioningScheduleUrl(int projectId) {
    var url = ReverseRouter.route(on(CommissionedWellController.class)
        .addCommissioningSchedule(projectId, null));
    return StringUtils.removeStart(url, "/");
  }

  private String getEditWellCommissioningScheduleUrl(int projectId, int commissionedWellScheduleId) {
    var url = ReverseRouter.route(on(CommissionedWellController.class)
        .getCommissionedWellSchedule(projectId, commissionedWellScheduleId, null));
    return StringUtils.removeStart(url, "/");
  }

  private String getRemoveWellCommissioningScheduleUrl(int projectId, int commissionedWellScheduleId, int displayOrder) {
    var url = ReverseRouter.route(on(CommissionedWellController.class)
        .removeCommissionedWellScheduleConfirmation(projectId, commissionedWellScheduleId, displayOrder, null));
    return StringUtils.removeStart(url, "/");
  }

}