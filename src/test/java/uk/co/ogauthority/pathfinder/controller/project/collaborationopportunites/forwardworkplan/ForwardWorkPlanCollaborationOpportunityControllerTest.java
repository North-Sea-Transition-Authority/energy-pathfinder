package uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.controller.file.FileDownloadService;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationCompletionForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationCompletionService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunitiesSummaryService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLinkService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityModelService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationRoutingService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetupService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ForwardWorkPlanCollaborationOpportunityController.class,
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = ProjectContextService.class
    )
)
public class ForwardWorkPlanCollaborationOpportunityControllerTest extends ProjectContextAbstractControllerTest {

  @MockBean
  protected ForwardWorkPlanCollaborationOpportunityModelService forwardWorkPlanCollaborationOpportunityModelService;

  @MockBean
  protected ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService;

  @MockBean
  protected ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  @MockBean
  protected ProjectDetailFileService projectDetailFileService;

  @MockBean
  protected ForwardWorkPlanCollaborationRoutingService forwardWorkPlanCollaborationRoutingService;

  @MockBean
  protected ForwardWorkPlanCollaborationSetupService forwardWorkPlanCollaborationSetupService;

  @MockBean
  protected ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService;

  @MockBean
  protected ForwardWorkPlanCollaborationCompletionService forwardWorkPlanCollaborationCompletionService;

  @MockBean
  protected FileDownloadService fileDownloadService;

  @MockBean
  protected ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  private ProjectControllerTesterService projectControllerTesterService;

  private final int projectId = 1;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES
  );

  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);

  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.FORWARD_WORK_PLAN);

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
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(any(), any())).thenReturn(true);
  }

  @Test
  public void viewCollaborationOpportunities_projectContextSmokeTest() {

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ForwardWorkPlanCollaborationOpportunityController.class).viewCollaborationOpportunities(projectId, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void addCollaborationOpportunity_projectContextSmokeTest() {

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ForwardWorkPlanCollaborationOpportunityController.class).addCollaborationOpportunity(projectId, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void editCollaborationOpportunity_projectContextSmokeTest() {

    final var opportunityId = 10;
    when(forwardWorkPlanCollaborationOpportunityService.getOrError(10))
        .thenReturn(ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail));

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ForwardWorkPlanCollaborationOpportunityController.class).editCollaborationOpportunity(
            projectId,
            opportunityId,
            null
        ),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void saveCollaborationOpportunity_projectContextSmokeTest() {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationOpportunityForm.class, "form");
    when(forwardWorkPlanCollaborationOpportunityService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(forwardWorkPlanCollaborationOpportunityService.createCollaborationOpportunity(any(), any(), any())).thenReturn(new ForwardWorkPlanCollaborationOpportunity());

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ForwardWorkPlanCollaborationOpportunityController.class).saveCollaborationOpportunity(
            projectId,
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
  public void saveCollaborationOpportunity_whenValidFormAndFullValidation_thenRedirection() {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationOpportunityForm.class, "form");
    when(forwardWorkPlanCollaborationOpportunityService.validate(any(), any(), any())).thenReturn(bindingResult);

    makeSaveCollaborationOpportunityRequest(
        ValidationTypeArgumentResolver.COMPLETE,
        status().is3xxRedirection()
    );

    verify(forwardWorkPlanCollaborationOpportunityService, times(1)).createCollaborationOpportunity(any(), any(), any());

  }

  @Test
  public void saveCollaborationOpportunity_whenValidFormAndPartialValidation_thenRedirection() {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationOpportunityForm.class, "form");
    when(forwardWorkPlanCollaborationOpportunityService.validate(any(), any(), any())).thenReturn(bindingResult);

    makeSaveCollaborationOpportunityRequest(
        ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER,
        status().is3xxRedirection()
    );

    verify(forwardWorkPlanCollaborationOpportunityService, times(1)).createCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void saveCollaborationOpportunity_whenInvalidFormAndFullValidation_thenOk() {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationOpportunityForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(forwardWorkPlanCollaborationOpportunityService.validate(any(), any(), any())).thenReturn(bindingResult);

    makeSaveCollaborationOpportunityRequest(
        ValidationTypeArgumentResolver.COMPLETE,
        status().isOk()
    );

    verify(forwardWorkPlanCollaborationOpportunityService, never()).createCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void saveCollaborationOpportunity_whenInvalidFormAndPartialValidation_thenOk() {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationOpportunityForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(forwardWorkPlanCollaborationOpportunityService.validate(any(), any(), any())).thenReturn(bindingResult);

    makeSaveCollaborationOpportunityRequest(
        ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER,
        status().isOk()
    );

    verify(forwardWorkPlanCollaborationOpportunityService, never()).createCollaborationOpportunity(any(), any(), any());
  }

  private void makeSaveCollaborationOpportunityRequest(String validationTypeArgument, ResultMatcher expectedMatcher) {

    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUser)).thenReturn(true);
    when(forwardWorkPlanCollaborationOpportunityService.createCollaborationOpportunity(any(), any(), any())).thenReturn(new ForwardWorkPlanCollaborationOpportunity());
    when(forwardWorkPlanCollaborationOpportunityModelService.getCollaborationOpportunityModelAndView(any(), any(), anyInt())).thenReturn(new ModelAndView());

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(validationTypeArgument, validationTypeArgument);

    projectControllerTesterService.makeRequestAndAssertMatcher(
        on(ForwardWorkPlanCollaborationOpportunityController.class).saveCollaborationOpportunity(
            projectId,
            null,
            null,
            null,
            null
        ),
        expectedMatcher
    );
  }

  @Test
  public void updateCollaborationOpportunity_projectContextSmokeTest() {

    final var collaborationOpportunityId = 100;
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    when(forwardWorkPlanCollaborationOpportunityService.getOrError(any())).thenReturn(collaborationOpportunity);
    when(forwardWorkPlanCollaborationOpportunityService.updateCollaborationOpportunity(any(), any(), any())).thenReturn(collaborationOpportunity);

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationOpportunityForm.class, "form");
    when(forwardWorkPlanCollaborationOpportunityService.validate(any(), any(), any())).thenReturn(bindingResult);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ForwardWorkPlanCollaborationOpportunityController.class).updateCollaborationOpportunity(
            projectId,
            collaborationOpportunityId,
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
  public void updateCollaborationOpportunity_whenValidFormAndFullValidation_thenRedirection() {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationOpportunityForm.class, "form");
    when(forwardWorkPlanCollaborationOpportunityService.validate(any(), any(), any())).thenReturn(bindingResult);

    makeUpdateCollaborationOpportunityRequest(
        ValidationTypeArgumentResolver.COMPLETE,
        status().is3xxRedirection()
    );

    verify(forwardWorkPlanCollaborationOpportunityService, times(1)).updateCollaborationOpportunity(any(), any(), any());

  }

  @Test
  public void updateCollaborationOpportunity_whenValidFormAndPartialValidation_thenRedirection() {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationOpportunityForm.class, "form");
    when(forwardWorkPlanCollaborationOpportunityService.validate(any(), any(), any())).thenReturn(bindingResult);

    makeUpdateCollaborationOpportunityRequest(
        ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER,
        status().is3xxRedirection()
    );

    verify(forwardWorkPlanCollaborationOpportunityService, times(1)).updateCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void updateCollaborationOpportunity_whenInvalidFormAndFullValidation_thenOk() {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationOpportunityForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(forwardWorkPlanCollaborationOpportunityService.validate(any(), any(), any())).thenReturn(bindingResult);

    makeUpdateCollaborationOpportunityRequest(
        ValidationTypeArgumentResolver.COMPLETE,
        status().isOk()
    );

    verify(forwardWorkPlanCollaborationOpportunityService, never()).updateCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void updateCollaborationOpportunity_whenInvalidFormAndPartialValidation_thenOk() {

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationOpportunityForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(forwardWorkPlanCollaborationOpportunityService.validate(any(), any(), any())).thenReturn(bindingResult);

    makeUpdateCollaborationOpportunityRequest(
        ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER,
        status().isOk()
    );

    verify(forwardWorkPlanCollaborationOpportunityService, never()).updateCollaborationOpportunity(any(), any(), any());
  }

  private void makeUpdateCollaborationOpportunityRequest(String validationTypeArgument, ResultMatcher expectedMatcher) {

    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUser)).thenReturn(true);

    final var collaborationOpportunityId = 100;
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    when(forwardWorkPlanCollaborationOpportunityService.getOrError(any())).thenReturn(collaborationOpportunity);
    when(forwardWorkPlanCollaborationOpportunityService.updateCollaborationOpportunity(any(), any(), any())).thenReturn(collaborationOpportunity);
    when(forwardWorkPlanCollaborationOpportunityModelService.getCollaborationOpportunityModelAndView(any(), any(), anyInt())).thenReturn(new ModelAndView());

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(validationTypeArgument, validationTypeArgument);

    projectControllerTesterService.makeRequestAndAssertMatcher(
        on(ForwardWorkPlanCollaborationOpportunityController.class).updateCollaborationOpportunity(
            projectId,
            collaborationOpportunityId,
            null,
            null,
            null,
            null
        ),
        expectedMatcher
    );
  }

  @Test
  public void removeCollaborationOpportunityConfirm_projectContextSmokeTest() {

    var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    when(forwardWorkPlanCollaborationOpportunityService.getOrError(any())).thenReturn(collaborationOpportunity);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ForwardWorkPlanCollaborationOpportunityController.class)
            .removeCollaborationOpportunityConfirm(projectId, 1, 2, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void removeCollaborationOpportunity_projectContextSmokeTest() {

    final var collaborationOpportunityId = 100;

    removeCollaborationOpportunity_setup(collaborationOpportunityId);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ForwardWorkPlanCollaborationOpportunityController.class).removeCollaborationOpportunity(
            projectId,
            collaborationOpportunityId,
            1,
            null
        ),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void removeCollaborationOpportunity_assertRedirectionWhenPosted() {

    final var collaborationOpportunityId = 200;

    removeCollaborationOpportunity_setup(collaborationOpportunityId);

    projectControllerTesterService.makeRequestAndAssertMatcher(
        on(ForwardWorkPlanCollaborationOpportunityController.class).removeCollaborationOpportunity(
            projectId,
            collaborationOpportunityId,
            1,
            null
        ),
        status().is3xxRedirection()
    );
  }

  @Test
  public void saveCollaborationOpportunities_whenAuthenticatedAndValidPage_thenRedirect() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.validateViews(any())).thenReturn(ValidationResult.VALID);

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationCompletionForm.class, "form");
    when(forwardWorkPlanCollaborationCompletionService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
            .saveCollaborationOpportunities(projectId, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        // this is an ok status as the ForwardWorkPlanCollaborationRoutingService determines which route to
        // return. As this is a mock for the test it returns null and hence an ok status. The call to
        // the routing service is verified below
        .andExpect(status().isOk());

    verify(forwardWorkPlanCollaborationCompletionService, times(1)).saveCollaborationCompletionForm(any(), any());
    verify(forwardWorkPlanCollaborationRoutingService, times(1)).getPostSaveCollaborationsRoute(any(), any());
  }

  @Test
  public void saveCollaborationOpportunities_whenAuthenticatedAndInvalidPage_thenStayOnSummary() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.validateViews(any())).thenReturn(ValidationResult.INVALID);

    final var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanCollaborationCompletionForm.class, "form");
    when(forwardWorkPlanCollaborationCompletionService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
            .saveCollaborationOpportunities(projectId, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isOk());

    verify(forwardWorkPlanCollaborationCompletionService, never()).saveCollaborationCompletionForm(any(), any());
    verify(forwardWorkPlanCollaborationRoutingService, never()).getPostSaveCollaborationsRoute(any(), any());
  }

  private void removeCollaborationOpportunity_setup(int collaborationOpportunityId) {

    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUser)).thenReturn(true);

    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    when(forwardWorkPlanCollaborationOpportunityService.getOrError(collaborationOpportunityId)).thenReturn(collaborationOpportunity);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE)
        .withProjectContributorAccess();
  }

  @Test
  public void editCollaborationOpportunity_userCantAccessTender_thenAccessForbidden() throws Exception {
    final var collaborationOpportunity =
        ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);
    when(forwardWorkPlanCollaborationOpportunityService.getOrError(any())).thenReturn(collaborationOpportunity);
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        any(),
        any())
    ).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
                .editCollaborationOpportunity(projectId, 1, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void updateCollaborationOpportunity_userCantAccessTender_thenAccessForbidden() throws Exception {
    final var collaborationOpportunity =
        ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);
    when(forwardWorkPlanCollaborationOpportunityService.getOrError(any())).thenReturn(collaborationOpportunity);
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        any(),
        any())
    ).thenReturn(false);

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    mockMvc.perform(
            post(ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
                .updateCollaborationOpportunity(
                    projectId,
                    1,
                    new ForwardWorkPlanCollaborationOpportunityForm(),
                    null,
                    null,
                    null
                )
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf())
                .params(completeParams))
        .andExpect(status().isForbidden());
  }

  @Test
  public void removeCollaborationOpportunityConfirm_userCantAccessTender_thenAccessForbidden() throws Exception {
    final var collaborationOpportunity =
        ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);
    when(forwardWorkPlanCollaborationOpportunityService.getOrError(any())).thenReturn(collaborationOpportunity);
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        any(),
        any())
    ).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
                .removeCollaborationOpportunityConfirm(projectId, 1, 1, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void removeCollaborationOpportunity_userCantAccessTender_thenAccessForbidden() throws Exception {
    final var collaborationOpportunity =
        ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);
    when(forwardWorkPlanCollaborationOpportunityService.getOrError(any())).thenReturn(collaborationOpportunity);
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        any(),
        any())
    ).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
                .removeCollaborationOpportunity(
                    projectId,
                    1,
                    1,
                    null
                )
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}