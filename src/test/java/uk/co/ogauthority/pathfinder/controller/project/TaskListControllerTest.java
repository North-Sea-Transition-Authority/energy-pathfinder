package uk.co.ogauthority.pathfinder.controller.project;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline.DecommissionedPipelineService;
import uk.co.ogauthority.pathfinder.service.project.integratedrig.IntegratedRigService;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.subseainfrastructure.SubseaInfrastructureService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = TaskListController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class TaskListControllerTest extends ProjectContextAbstractControllerTest {

  @MockBean
  ProjectInformationService projectInformationService;

  @MockBean
  ProjectLocationService projectLocationService;

  @MockBean
  SelectOperatorService selectOperatorService;

  @MockBean
  UpcomingTenderService upcomingTenderService;

  @MockBean
  AwardedContractService awardedContractService;

  @MockBean
  CollaborationOpportunitiesService collaborationOpportunitiesService;

  @MockBean
  SubseaInfrastructureService subseaInfrastructureService;

  @MockBean
  IntegratedRigService integratedRigService;

  @MockBean
  DecommissionedPipelineService decommissionedPipelineService;

  @MockBean
  private PlatformsFpsosService platformsFpsosService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  private final ProjectDetail details = ProjectUtil.getProjectDetails();


  @Test
  public void authenticatedUser_hasAccessToTaskList() throws Exception {
    when(projectService.getLatestDetail(any())).thenReturn(Optional.of(details));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(details, authenticatedUser)).thenReturn(true);

    mockMvc.perform(get(ReverseRouter.route(on(TaskListController.class).viewTaskList(1, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessTaskList() throws Exception {
    when(projectService.getLatestDetail(any())).thenReturn(Optional.of(details));
    mockMvc.perform(get(ReverseRouter.route(on(TaskListController.class).viewTaskList(1, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }
}
