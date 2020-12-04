package uk.co.ogauthority.pathfinder.service.projectupdate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectUpdateServiceTest {

  @Mock
  private ProjectService projectService;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private AwardedContractService awardedContractService;

  private ProjectUpdateService projectUpdateService;

  @Before
  public void setup() {
    projectUpdateService = new ProjectUpdateService(
        projectService,
        List.of(projectInformationService, awardedContractService)
    );
  }

  @Test
  public void createNewProjectVersion_verifyServiceInteractions() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var user = UserTestingUtil.getAuthenticatedUserAccount();

    projectUpdateService.createNewProjectVersion(fromProjectDetail, user);

    verify(projectService, times(1)).createNewProjectDetailVersion(fromProjectDetail, user);
    verify(projectInformationService, times(1)).copySectionData(eq(fromProjectDetail), any());
    verify(awardedContractService, times(1)).copySectionData(eq(fromProjectDetail), any());
  }
}