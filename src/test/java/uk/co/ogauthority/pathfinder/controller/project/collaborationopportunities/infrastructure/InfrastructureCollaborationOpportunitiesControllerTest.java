package uk.co.ogauthority.pathfinder.controller.project.collaborationopportunities.infrastructure;

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
import static uk.co.ogauthority.pathfinder.testutil.ProjectFileTestUtil.FILE_ID;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.hibernate.AssertionFailure;
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
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.controller.file.FileDownloadService;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.infrastructure.InfrastructureCollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure.InfrastructureCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure.InfrastructureCollaborationOpportunityViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectFileTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(SpringRunner.class)
@WebMvcTest(value = InfrastructureCollaborationOpportunitiesController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class InfrastructureCollaborationOpportunitiesControllerTest extends ProjectContextAbstractControllerTest {
  private static final Integer PROJECT_ID = 1;
  private static final Integer COLLABORATION_OPPORTUNITY_ID = 1;
  private static final Integer DISPLAY_ORDER = 1;
  private static final Integer PROJECT_VERSION = 1;
  private final PortalOrganisationGroup addedByPortalOrganisationGroup =
      TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  @MockBean
  private InfrastructureCollaborationOpportunitiesService infrastructureCollaborationOpportunitiesService;

  @MockBean
  private InfrastructureCollaborationOpportunitiesSummaryService infrastructureCollaborationOpportunitiesSummaryService;

  @MockBean
  protected ProjectDetailFileService projectDetailFileService;

  @MockBean
  protected FileDownloadService fileDownloadService;

  @MockBean
  protected ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  private ProjectControllerTesterService projectControllerTesterService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final ProjectDetailFile PROJECT_DETAIL_FILE = ProjectFileTestUtil.getProjectDetailFile(detail);

  private final InfrastructureCollaborationOpportunity opportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);

  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);

  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.INFRASTRUCTURE);

  private final Set<ProjectPermission> requiredPermissions = ProjectControllerTesterService.PROJECT_CREATE_PERMISSION_SET;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setUp() throws SQLException {
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(detail);
    when(projectOperatorService.isUserInProjectTeam(detail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(detail, unAuthenticatedUser)).thenReturn(false);
    when(infrastructureCollaborationOpportunitiesService.getOrError(COLLABORATION_OPPORTUNITY_ID)).thenReturn(opportunity);
    UploadedFile file = ProjectFileTestUtil.getUploadedFile();

    InfrastructureCollaborationOpportunityView collaborationOpportunityView = new InfrastructureCollaborationOpportunityViewUtil.InfrastructureCollaborationOpportunityViewBuilder(
        opportunity,
        DISPLAY_ORDER,
        Collections.emptyList(),
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(true)
        .build();
    when(infrastructureCollaborationOpportunitiesSummaryService.getView(opportunity, DISPLAY_ORDER)).thenReturn(collaborationOpportunityView);
    when(infrastructureCollaborationOpportunitiesService.createCollaborationOpportunity(any(), any(), any())).thenReturn(
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail));
    when(infrastructureCollaborationOpportunitiesService.updateCollaborationOpportunity(any(), any(), any())).thenReturn(
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail));
    when(projectDetailFileService.getProjectDetailFileByProjectDetailVersionAndFileId(any(), any(), any())).thenReturn(PROJECT_DETAIL_FILE);
    when(projectDetailFileService.getUploadedFileById(FILE_ID)).thenReturn(file);
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(any(), any())).thenReturn(true);

    projectControllerTesterService = new ProjectControllerTesterService(
        mockMvc,
        projectOperatorService,
        projectContributorsCommonService,
        teamService
    );
  }

  @Test
  public void viewCollaborationOpportunities_projectContextSmokeTest() {
    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(detail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(InfrastructureCollaborationOpportunitiesController.class).viewCollaborationOpportunities(detail.getProject().getId(), null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void saveCollaborationOpportunities_projectContextSmokeTest() {
    var infrastructureCollaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    InfrastructureCollaborationOpportunityView collaborationOpportunityView = new InfrastructureCollaborationOpportunityViewUtil.InfrastructureCollaborationOpportunityViewBuilder(
        infrastructureCollaborationOpportunity,
        DISPLAY_ORDER,
        Collections.emptyList(),
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(true)
        .build();
    var infrastructureCollaborationOpportunityViews = List.of(collaborationOpportunityView);
    when(infrastructureCollaborationOpportunitiesSummaryService.getValidatedSummaryViews(detail))
        .thenReturn(infrastructureCollaborationOpportunityViews);
    when(infrastructureCollaborationOpportunitiesSummaryService.validateViews(infrastructureCollaborationOpportunityViews))
        .thenReturn(ValidationResult.VALID);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(detail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(InfrastructureCollaborationOpportunitiesController.class).saveCollaborationOpportunities(
            detail.getProject().getId(),
            null
        ),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void addCollaborationOpportunity_projectContextSmokeTest() {
    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(detail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(InfrastructureCollaborationOpportunitiesController.class).addCollaborationOpportunity(
            detail.getProject().getId(),
            null
        ),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void saveCollaborationOpportunity_projectContextSmokeTest() {
    var form = new InfrastructureCollaborationOpportunityForm();
    var bindingResult = new BeanPropertyBindingResult(InfrastructureCollaborationOpportunityForm.class, "form");
    when(infrastructureCollaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(detail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(InfrastructureCollaborationOpportunitiesController.class).saveCollaborationOpportunity(
            detail.getProject().getId(),
            form,
            bindingResult,
            null,
            null
            ),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void editCollaborationOpportunity_projectContextSmokeTest() {
    var form = new InfrastructureCollaborationOpportunityForm();
    when(infrastructureCollaborationOpportunitiesService.getForm(opportunity))
        .thenReturn(form);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(detail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(InfrastructureCollaborationOpportunitiesController.class).editCollaborationOpportunity(
            detail.getProject().getId(),
            COLLABORATION_OPPORTUNITY_ID,
            null
        ),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void updateCollaborationOpportunity_projectContextSmokeTest() {
    var opportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    var form = new InfrastructureCollaborationOpportunityForm();
    when(infrastructureCollaborationOpportunitiesService.getOrError(COLLABORATION_OPPORTUNITY_ID))
        .thenReturn(opportunity);
    var bindingResult = new BeanPropertyBindingResult(InfrastructureCollaborationOpportunityForm.class, "form");
    when(infrastructureCollaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(detail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(InfrastructureCollaborationOpportunitiesController.class).updateCollaborationOpportunity(
            detail.getProject().getId(),
            COLLABORATION_OPPORTUNITY_ID,
            form,
            bindingResult,
            null,
            null
        ),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void removeCollaborationOpportunityConfirm_projectContextSmokeTest() {
    var opportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    InfrastructureCollaborationOpportunityView collaborationOpportunityView = new InfrastructureCollaborationOpportunityViewUtil.InfrastructureCollaborationOpportunityViewBuilder(
        opportunity,
        DISPLAY_ORDER,
        Collections.emptyList(),
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(true)
        .build();
    when(infrastructureCollaborationOpportunitiesService.getOrError(COLLABORATION_OPPORTUNITY_ID))
        .thenReturn(opportunity);
    when(infrastructureCollaborationOpportunitiesSummaryService.getView(opportunity, DISPLAY_ORDER))
        .thenReturn(collaborationOpportunityView);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(detail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(InfrastructureCollaborationOpportunitiesController.class).removeCollaborationOpportunityConfirm(
            detail.getProject().getId(),
            COLLABORATION_OPPORTUNITY_ID,
            DISPLAY_ORDER,
            null
        ),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void removeCollaborationOpportunity_projectContextSmokeTest() {
    var opportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    when(infrastructureCollaborationOpportunitiesService.getOrError(COLLABORATION_OPPORTUNITY_ID))
        .thenReturn(opportunity);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(detail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(InfrastructureCollaborationOpportunitiesController.class).removeCollaborationOpportunity(
            detail.getProject().getId(),
            COLLABORATION_OPPORTUNITY_ID,
            DISPLAY_ORDER,
            null
        ),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void authenticatedUser_hasAccessToCollaborationOpportunity() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(InfrastructureCollaborationOpportunitiesController.class).addCollaborationOpportunity(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessCollaborationOpportunity() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(InfrastructureCollaborationOpportunitiesController.class).addCollaborationOpportunity(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToCollaborationOpportunitySummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(InfrastructureCollaborationOpportunitiesController.class).viewCollaborationOpportunities(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessCollaborationOpportunitySummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(InfrastructureCollaborationOpportunitiesController.class).viewCollaborationOpportunities(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToCollaborationOpportunityRemove() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(InfrastructureCollaborationOpportunitiesController.class).removeCollaborationOpportunityConfirm(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, DISPLAY_ORDER, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessCollaborationOpportunityRemove() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(InfrastructureCollaborationOpportunitiesController.class).removeCollaborationOpportunityConfirm(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, DISPLAY_ORDER, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToCollaborationOpportunityEdit() throws Exception {
    when(infrastructureCollaborationOpportunitiesService.getForm(opportunity)).thenReturn(
        InfrastructureCollaborationOpportunityTestUtil.getCompleteForm());
    mockMvc.perform(get(ReverseRouter.route(
        on(InfrastructureCollaborationOpportunitiesController.class).editCollaborationOpportunity(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessCollaborationOpportunityEdit() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(InfrastructureCollaborationOpportunitiesController.class).editCollaborationOpportunity(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveCollaborationOpportunity_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(InfrastructureCollaborationOpportunityForm.class, "form");
    when(infrastructureCollaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
            .saveCollaborationOpportunity(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(infrastructureCollaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(infrastructureCollaborationOpportunitiesService, times(1)).createCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void saveCollaborationOpportunity_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(InfrastructureCollaborationOpportunityForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(infrastructureCollaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
            .saveCollaborationOpportunity(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(infrastructureCollaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(infrastructureCollaborationOpportunitiesService, times(0)).createCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void saveCollaborationOpportunity_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(InfrastructureCollaborationOpportunityForm.class, "form");
    when(infrastructureCollaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
            .saveCollaborationOpportunity(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(infrastructureCollaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(infrastructureCollaborationOpportunitiesService, times(1)).createCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void updateCollaborationOpportunity_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(InfrastructureCollaborationOpportunityForm.class, "form");
    when(infrastructureCollaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
            .updateCollaborationOpportunity(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(infrastructureCollaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(infrastructureCollaborationOpportunitiesService, times(1)).updateCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void updateCollaborationOpportunity_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(InfrastructureCollaborationOpportunityForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(infrastructureCollaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
            .updateCollaborationOpportunity(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(infrastructureCollaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(infrastructureCollaborationOpportunitiesService, times(0)).updateCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void updateCollaborationOpportunity_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(InfrastructureCollaborationOpportunityForm.class, "form");
    when(infrastructureCollaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
            .updateCollaborationOpportunity(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(infrastructureCollaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(infrastructureCollaborationOpportunitiesService, times(1)).updateCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void handDownload_whenUserCanAccessProjectFiles_projectStatusSmokeTest() {

    var userWithViewPriv = UserTestingUtil.getAuthenticatedUserAccount(List.of(UserPrivilege.PATHFINDER_PROJECT_VIEWER));

    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, userWithViewPriv)).thenReturn(true);

    when(projectDetailFileService.canAccessFiles(detail, userWithViewPriv.getLinkedPerson()))
        .thenReturn(true);

    var allowedProjectStatuses = Set.of(
        ProjectStatus.DRAFT,
        ProjectStatus.QA,
        ProjectStatus.PUBLISHED,
        ProjectStatus.ARCHIVED
    );

    Arrays.asList(ProjectStatus.values()).forEach(projectStatus -> {

      detail.setStatus(projectStatus);

      try {
        if (allowedProjectStatuses.contains(projectStatus)) {
          makeDownloadRequest(userWithViewPriv, status().isOk());
        } else {
          makeDownloadRequest(userWithViewPriv, status().isForbidden());
        }
      } catch (Exception exception) {
        throw new AssertionFailure(
            String.format(
                "Encountered exception when testing access to downloading a file from a project with status %s",
                projectStatus
            ),
            exception
        );
      }
    });
  }

  @Test
  public void handDownload_whenUserCannotAccessProjectFiles_projectStatusSmokeTest() {

    var userWithViewPriv = UserTestingUtil.getAuthenticatedUserAccount(List.of(UserPrivilege.PATHFINDER_PROJECT_VIEWER));

    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, userWithViewPriv)).thenReturn(true);

    when(projectDetailFileService.canAccessFiles(detail, userWithViewPriv.getLinkedPerson()))
        .thenReturn(false);

    Arrays.asList(ProjectStatus.values()).forEach(projectStatus -> {

      detail.setStatus(projectStatus);

      try {
        makeDownloadRequest(userWithViewPriv, status().isForbidden());
      } catch (Exception exception) {
        throw new AssertionFailure(String.format(
            "Expected a 403 response when downloading a file from a project with status %s but encountered exception %s",
            projectStatus,
            exception
        ));
      }
    });
  }

  private void makeDownloadRequest(AuthenticatedUserAccount userAccessingEndpoint, ResultMatcher expectedResponseStatus) throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
            on(InfrastructureCollaborationOpportunitiesController.class).handleDownload(PROJECT_ID, PROJECT_VERSION, ProjectFileTestUtil.FILE_ID, null)))
            .with(authenticatedUserAndSession(userAccessingEndpoint)))
        .andExpect(expectedResponseStatus);
  }

  @Test
  public void editCollaborationOpportunity_userCantAccessCollabOpportunity_thenAccessForbidden() throws Exception {
    final var collaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    when(infrastructureCollaborationOpportunitiesService.getOrError(COLLABORATION_OPPORTUNITY_ID))
        .thenReturn(collaborationOpportunity);
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        any(),
        any())
    ).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
                .editCollaborationOpportunity(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void updateCollaborationOpportunity_userCantAccessCollabOpportunity_thenAccessForbidden() throws Exception {
    final var collaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    when(infrastructureCollaborationOpportunitiesService.getOrError(COLLABORATION_OPPORTUNITY_ID))
        .thenReturn(collaborationOpportunity);
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        any(),
        any())
    ).thenReturn(false);

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    mockMvc.perform(
            post(ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
                .updateCollaborationOpportunity(
                    PROJECT_ID,
                    COLLABORATION_OPPORTUNITY_ID,
                    new InfrastructureCollaborationOpportunityForm(),
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
  public void removeCollaborationOpportunityConfirm_userCantAccessCollabOpportunity_thenAccessForbidden() throws Exception {
    final var collaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    when(infrastructureCollaborationOpportunitiesService.getOrError(COLLABORATION_OPPORTUNITY_ID))
        .thenReturn(collaborationOpportunity);
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        any(),
        any())
    ).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
                .removeCollaborationOpportunityConfirm(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, DISPLAY_ORDER, null)
            ))
                .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void removeCollaborationOpportunity_userCantAccessCollabOpportunity_thenAccessForbidden() throws Exception {
    final var collaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    when(infrastructureCollaborationOpportunitiesService.getOrError(COLLABORATION_OPPORTUNITY_ID))
        .thenReturn(collaborationOpportunity);
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        any(),
        any())
    ).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
                .removeCollaborationOpportunity(
                    PROJECT_ID,
                    COLLABORATION_OPPORTUNITY_ID,
                    DISPLAY_ORDER,
                    null
                )
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}
