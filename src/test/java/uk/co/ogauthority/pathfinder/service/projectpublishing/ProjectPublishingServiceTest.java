package uk.co.ogauthority.pathfinder.service.projectpublishing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectpublishing.ProjectPublishingDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.projectpublishing.ProjectPublishingDetailRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectPublishingServiceTest {

  @Mock
  private ProjectPublishingDetailRepository projectPublishingDetailRepository;

  private ProjectPublishingService projectPublishingService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectPublishingService = new ProjectPublishingService(projectPublishingDetailRepository);

    when(projectPublishingDetailRepository.save(any(ProjectPublishingDetail.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void publishProject() {
    var projectPublishingDetail = projectPublishingService.publishProject(projectDetail, authenticatedUser);

    assertThat(projectPublishingDetail.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(projectPublishingDetail.getPublishedInstant()).isNotNull();
    assertThat(projectPublishingDetail.getPublisherWuaId()).isEqualTo(authenticatedUser.getWuaId());

    assertThat(projectDetail.getStatus()).isEqualTo(ProjectStatus.PUBLISHED);
  }
}
