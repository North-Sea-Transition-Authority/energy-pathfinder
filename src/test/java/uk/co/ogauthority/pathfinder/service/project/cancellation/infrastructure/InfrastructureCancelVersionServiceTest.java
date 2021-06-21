package uk.co.ogauthority.pathfinder.service.project.cancellation.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureCancelVersionServiceTest {

  private ProjectDetail projectDetail;

  private InfrastructureCancelVersionService infrastructureCancelVersionService;

  @Before
  public void setup() {
    infrastructureCancelVersionService = new InfrastructureCancelVersionService();

    projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    projectDetail.setStatus(ProjectStatus.DRAFT);
  }

  @Test
  public void getSupportedProjectType_assertIsInfrastructure() {
    final var supportedProjectType = infrastructureCancelVersionService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void isCancellable_projectStatusSmokeTest_whenInfrastructure_onlyDraftPermitted() {

    final var permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);

    Arrays.asList(ProjectStatus.values()).forEach(projectStatus -> {

      projectDetail.setStatus(projectStatus);

      final var isCancellable = infrastructureCancelVersionService.isCancellable(projectDetail);

      if (permittedProjectStatuses.contains(projectStatus)) {
        assertThat(isCancellable).isTrue();
      } else {
        assertThat(isCancellable).isFalse();
      }

    });

  }

  @Test
  public void isCancellable_smokeTestProjectType_whenStatusIsDraft_onlyInfrastructurePermitted() {

    final var permittedProjectType = infrastructureCancelVersionService.getSupportedProjectType();

    Arrays.asList(ProjectType.values()).forEach(projectType -> {

      projectDetail.setProjectType(projectType);

      final var isCancellable = infrastructureCancelVersionService.isCancellable(projectDetail);

      if (permittedProjectType.equals(projectType)) {
        assertThat(isCancellable).isTrue();
      } else {
        assertThat(isCancellable).isFalse();
      }

    });
  }

}