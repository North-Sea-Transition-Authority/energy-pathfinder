package uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLinkService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityModelService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

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
    projectControllerTesterService = new ProjectControllerTesterService(mockMvc, projectOperatorService);
    when(projectService.getLatestDetailOrError(projectId)).thenReturn(projectDetail);
  }

  @Test
  public void viewCollaborationOpportunities_projectContextSmokeTest() {

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

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
        .withRequiredProjectPermissions(requiredPermissions);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ForwardWorkPlanCollaborationOpportunityController.class).addCollaborationOpportunity(projectId, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void editCollaborationOpportunity_projectContextSmokeTest() {

    final var opportunityId = 10;
    when(forwardWorkPlanCollaborationOpportunityService.getOrError(10)).thenReturn(new ForwardWorkPlanCollaborationOpportunity());

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

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
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);

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

    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);
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
    final var collaborationOpportunity = new ForwardWorkPlanCollaborationOpportunity();

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
        .withRequestParam(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);

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

    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);

    final var collaborationOpportunityId = 100;
    final var collaborationOpportunity = new ForwardWorkPlanCollaborationOpportunity();

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
}