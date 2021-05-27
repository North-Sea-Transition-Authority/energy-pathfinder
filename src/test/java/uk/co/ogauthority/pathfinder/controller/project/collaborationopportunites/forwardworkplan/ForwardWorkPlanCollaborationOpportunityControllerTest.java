package uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan;

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
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerSmokeTesterService;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityModelService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
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

  private ProjectControllerSmokeTesterService projectControllerSmokeTesterService;

  private final int projectId = 1;
  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES
  );

  @Before
  public void setup() {
    projectControllerSmokeTesterService = new ProjectControllerSmokeTesterService(mockMvc, projectOperatorService);
    when(projectService.getLatestDetailOrError(projectId)).thenReturn(projectDetail);
  }

  @Test
  public void viewCollaborationOpportunities_projectStatusSmokeTest_assertOnlyDraft() {

    projectControllerSmokeTesterService
        .setHttpRequestMethod(HttpMethod.GET)
        .setProjectDetail(projectDetail)
        .setUserToTestWith(authenticatedUser);

    projectControllerSmokeTesterService.smokeTestProjectStatus(
        on(ForwardWorkPlanCollaborationOpportunityController.class).viewCollaborationOpportunities(projectId, null),
        Set.of(ProjectStatus.DRAFT),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void viewCollaborationOpportunities_projectTypeSmokeTest_assertOnlyForwardWorkPlan() {

    projectControllerSmokeTesterService
        .setHttpRequestMethod(HttpMethod.GET)
        .setProjectDetail(projectDetail)
        .setUserToTestWith(authenticatedUser);

    projectControllerSmokeTesterService.smokeTestProjectType(
        on(ForwardWorkPlanCollaborationOpportunityController.class).viewCollaborationOpportunities(projectId, null),
        Set.of(ProjectType.FORWARD_WORK_PLAN),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void viewCollaborationOpportunities_projectPermissionSmokeTest_assertOnlyEditSubmit() {

    projectControllerSmokeTesterService
        .setHttpRequestMethod(HttpMethod.GET)
        .setProjectDetail(projectDetail);

    projectControllerSmokeTesterService.smokeTestProjectPermissions(
        on(ForwardWorkPlanCollaborationOpportunityController.class).viewCollaborationOpportunities(projectId, null),
        ProjectControllerSmokeTesterService.PROJECT_CREATE_PERMISSION_SET,
        status().isOk(),
        status().isForbidden()
    );
  }

}