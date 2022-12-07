package uk.co.ogauthority.pathfinder.service.project.overview;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class OverviewFormSectionServiceTest {

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private OverviewFormSectionService overviewFormSectionService;

  @Before
  public void setup() {
    overviewFormSectionService = new OverviewFormSectionService();
  }

  @Test
  public void isComplete_assertTrue() {
    assertThat(overviewFormSectionService.isComplete(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_forAllRelations_trueOnlyForContributors() {
    var allowedRelationships = Set.of(UserToProjectRelationship.CONTRIBUTOR);
    for (UserToProjectRelationship relation : UserToProjectRelationship.values()) {
      if (allowedRelationships.contains(relation)) {
        assertThat(overviewFormSectionService.canShowInTaskList(detail, Set.of(relation))).isTrue();
      } else {
        assertThat(overviewFormSectionService.canShowInTaskList(detail, Set.of(relation))).isFalse();
      }
    }
  }

  @Test
  public void isTaskValidForProjectDetail_assertTrue() {
    assertThat(overviewFormSectionService.isTaskValidForProjectDetail(detail)).isTrue();
  }

  @Test
  public void getSupportedProjectTypes_assertValidTypes() {
    assertThat(overviewFormSectionService.getSupportedProjectTypes())
        .containsExactlyInAnyOrder(ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN);
  }
}