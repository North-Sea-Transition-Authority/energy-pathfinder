package uk.co.ogauthority.pathfinder.controller.project.collaborationopportunities;

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
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.CollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.CollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CollaborationOpportunitiesController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class CollaborationOpportunitiesControllerTest extends ProjectContextAbstractControllerTest {
  private static final Integer PROJECT_ID = 1;
  private static final Integer COLLABORATION_OPPORTUNITY_ID = 1;
  private static final Integer DISPLAY_ORDER = 1;

  @MockBean
  private CollaborationOpportunitiesService collaborationOpportunitiesService;

  @MockBean
  private CollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService;

  @MockBean
  protected ProjectDetailFileService projectDetailFileService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final CollaborationOpportunity opportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);


  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setUp() {
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(detail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, unAuthenticatedUser)).thenReturn(false);
    when(collaborationOpportunitiesService.getOrError(COLLABORATION_OPPORTUNITY_ID)).thenReturn(opportunity);

    var collaborationOpportunityView = CollaborationOpportunityViewUtil.createView(
        opportunity,
        DISPLAY_ORDER,
        Collections.emptyList()
    );
    when(collaborationOpportunitiesSummaryService.getView(opportunity, DISPLAY_ORDER)).thenReturn(collaborationOpportunityView);
  }

  @Test
  public void authenticatedUser_hasAccessToCollaborationOpportunity() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(CollaborationOpportunitiesController.class).addCollaborationOpportunity(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessCollaborationOpportunity() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(CollaborationOpportunitiesController.class).addCollaborationOpportunity(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToCollaborationOpportunitySummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessCollaborationOpportunitySummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToCollaborationOpportunityRemove() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(CollaborationOpportunitiesController.class).removeCollaborationOpportunityConfirm(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, DISPLAY_ORDER, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessCollaborationOpportunityRemove() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(CollaborationOpportunitiesController.class).removeCollaborationOpportunityConfirm(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, DISPLAY_ORDER, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToCollaborationOpportunityEdit() throws Exception {
    when(collaborationOpportunitiesService.getForm(opportunity)).thenReturn(CollaborationOpportunityTestUtil.getCompleteForm());
    mockMvc.perform(get(ReverseRouter.route(
        on(CollaborationOpportunitiesController.class).editCollaborationOpportunity(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessCollaborationOpportunityEdit() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(CollaborationOpportunitiesController.class).editCollaborationOpportunity(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveCollaborationOpportunity_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(CollaborationOpportunityForm.class, "form");
    when(collaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(CollaborationOpportunitiesController.class)
            .saveCollaborationOpportunity(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(collaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(collaborationOpportunitiesService, times(1)).createCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void saveCollaborationOpportunity_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(CollaborationOpportunityForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(collaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(CollaborationOpportunitiesController.class)
            .saveCollaborationOpportunity(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(collaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(collaborationOpportunitiesService, times(0)).createCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void saveCollaborationOpportunity_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(CollaborationOpportunityForm.class, "form");
    when(collaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(CollaborationOpportunitiesController.class)
            .saveCollaborationOpportunity(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(collaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(collaborationOpportunitiesService, times(1)).createCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void updateCollaborationOpportunity_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(CollaborationOpportunityForm.class, "form");
    when(collaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(CollaborationOpportunitiesController.class)
            .updateCollaborationOpportunity(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(collaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(collaborationOpportunitiesService, times(1)).updateCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void updateCollaborationOpportunity_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(CollaborationOpportunityForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(collaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(CollaborationOpportunitiesController.class)
            .updateCollaborationOpportunity(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(collaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(collaborationOpportunitiesService, times(0)).updateCollaborationOpportunity(any(), any(), any());
  }

  @Test
  public void updateCollaborationOpportunity_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(CollaborationOpportunityForm.class, "form");
    when(collaborationOpportunitiesService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(CollaborationOpportunitiesController.class)
            .updateCollaborationOpportunity(PROJECT_ID, COLLABORATION_OPPORTUNITY_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(collaborationOpportunitiesService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(collaborationOpportunitiesService, times(1)).updateCollaborationOpportunity(any(), any(), any());
  }

}
