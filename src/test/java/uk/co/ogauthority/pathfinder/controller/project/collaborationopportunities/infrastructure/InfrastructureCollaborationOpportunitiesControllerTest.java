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
import java.util.Collections;
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
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.infrastructure.InfrastructureCollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure.InfrastructureCollaborationOpportunityViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectFileTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = InfrastructureCollaborationOpportunitiesController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class InfrastructureCollaborationOpportunitiesControllerTest extends ProjectContextAbstractControllerTest {
  private static final Integer PROJECT_ID = 1;
  private static final Integer COLLABORATION_OPPORTUNITY_ID = 1;
  private static final Integer DISPLAY_ORDER = 1;
  private static final Integer PROJECT_VERSION = 1;

  @MockBean
  private InfrastructureCollaborationOpportunitiesService infrastructureCollaborationOpportunitiesService;

  @MockBean
  private InfrastructureCollaborationOpportunitiesSummaryService infrastructureCollaborationOpportunitiesSummaryService;

  @MockBean
  protected ProjectDetailFileService projectDetailFileService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final ProjectDetailFile PROJECT_DETAIL_FILE = ProjectFileTestUtil.getProjectDetailFile(detail);

  private final InfrastructureCollaborationOpportunity opportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);


  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setUp() throws SQLException {
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(detail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, unAuthenticatedUser)).thenReturn(false);
    when(infrastructureCollaborationOpportunitiesService.getOrError(COLLABORATION_OPPORTUNITY_ID)).thenReturn(opportunity);
    UploadedFile file = ProjectFileTestUtil.getUploadedFile();

    var collaborationOpportunityView = InfrastructureCollaborationOpportunityViewUtil.createView(
        opportunity,
        DISPLAY_ORDER,
        Collections.emptyList()
    );
    when(infrastructureCollaborationOpportunitiesSummaryService.getView(opportunity, DISPLAY_ORDER)).thenReturn(collaborationOpportunityView);
    when(infrastructureCollaborationOpportunitiesService.createCollaborationOpportunity(any(), any(), any())).thenReturn(
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail));
    when(infrastructureCollaborationOpportunitiesService.updateCollaborationOpportunity(any(), any(), any())).thenReturn(
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail));
    when(projectDetailFileService.getProjectDetailFileByProjectDetailVersionAndFileId(any(), any(), any())).thenReturn(PROJECT_DETAIL_FILE);
    when(projectDetailFileService.getUploadedFileById(FILE_ID)).thenReturn(file);
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
  public void handleDownload_draftProject() throws Exception {
    makeDownloadRequest(ProjectStatus.DRAFT);
  }

  @Test
  public void handleDownload_qaProject() throws Exception {
    makeDownloadRequest(ProjectStatus.QA);
  }

  @Test
  public void handleDownload_publishedProject() throws Exception {
    makeDownloadRequest(ProjectStatus.PUBLISHED);
  }

  @Test
  public void handleDownload_archivedProject() throws Exception {
    makeDownloadRequest(ProjectStatus.ARCHIVED);
  }

  private void makeDownloadRequest(ProjectStatus status) throws Exception {
    var customStatusProject = detail;
    customStatusProject.setStatus(status);
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(customStatusProject);
    mockMvc.perform(get(ReverseRouter.route(
        on(InfrastructureCollaborationOpportunitiesController.class).handleDownload(PROJECT_ID, PROJECT_VERSION, ProjectFileTestUtil.FILE_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

}
