package uk.co.ogauthority.pathfinder.service.project.cancellation.forwardworkplan;

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
public class ForwardWorkPlanCancelVersionServiceTest {

  private static final int FIRST_VERSION_NUMBER = 1;
  private static final int NOT_FIRST_VERSION_NUMBER = 2;

  private ProjectDetail projectDetail;

  private ForwardWorkPlanCancelVersionService forwardWorkPlanCancelVersionService;

  @Before
  public void setup() {
    forwardWorkPlanCancelVersionService = new ForwardWorkPlanCancelVersionService();

    projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    projectDetail.setStatus(ProjectStatus.DRAFT);
    projectDetail.setVersion(NOT_FIRST_VERSION_NUMBER);
  }

  @Test
  public void getSupportedProjectType_assertIsForwardWorkPlan() {
    final var supportedProjectType = forwardWorkPlanCancelVersionService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  public void isCancellable_projectStatusSmokeTest_whenForwardWorkPlanAndFirstVersion_onlyDraftPermitted() {

    final var permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);

    Arrays.asList(ProjectStatus.values()).forEach(projectStatus -> {

      projectDetail.setStatus(projectStatus);

      final var isCancellable = forwardWorkPlanCancelVersionService.isCancellable(projectDetail);

      if (permittedProjectStatuses.contains(projectStatus)) {
        assertThat(isCancellable).isTrue();
      } else {
        assertThat(isCancellable).isFalse();
      }

    });

  }

  @Test
  public void isCancellable_smokeTestProjectType_whenStatusIsDraftAndFirstVersion_onlyForwardWorkPlanPermitted() {

    final var permittedProjectType = forwardWorkPlanCancelVersionService.getSupportedProjectType();

    Arrays.asList(ProjectType.values()).forEach(projectType -> {

      projectDetail.setProjectType(projectType);

      final var isCancellable = forwardWorkPlanCancelVersionService.isCancellable(projectDetail);

      if (permittedProjectType.equals(projectType)) {
        assertThat(isCancellable).isTrue();
      } else {
        assertThat(isCancellable).isFalse();
      }

    });
  }

  @Test
  public void isCancellable_whenFirstVersion_allOtherConditionsMet_thenFalse() {

    projectDetail.setVersion(FIRST_VERSION_NUMBER);

    final var isCancellable = forwardWorkPlanCancelVersionService.isCancellable(projectDetail);

    assertThat(isCancellable).isFalse();
  }

  @Test
  public void isCancellable_whenNotFirstVersion_allOtherConditionsMet_thenTrue() {

    projectDetail.setVersion(NOT_FIRST_VERSION_NUMBER);

    final var isCancellable = forwardWorkPlanCancelVersionService.isCancellable(projectDetail);

    assertThat(isCancellable).isTrue();
  }

}