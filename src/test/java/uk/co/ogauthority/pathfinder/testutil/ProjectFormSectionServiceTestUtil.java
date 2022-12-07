package uk.co.ogauthority.pathfinder.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Set;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

public class ProjectFormSectionServiceTestUtil {

  private ProjectFormSectionServiceTestUtil() {
    throw new IllegalStateException("ProjectFormSectionServiceTestUtil is a utility class and should not be instantiated");
  }

  public static void canShowInTaskList_userToProjectRelationshipSmokeTest(
      ProjectFormSectionService serviceClass,
      ProjectDetail detail,
      Set<UserToProjectRelationship> permittedRelationships
  ) {
    Arrays.asList(UserToProjectRelationship.values())
        .forEach(userToProjectRelationship -> {
          if (permittedRelationships.contains(userToProjectRelationship)) {
            assertThat(serviceClass.canShowInTaskList(detail, Set.of(userToProjectRelationship))).isTrue();
          } else {
            assertThat(serviceClass.canShowInTaskList(detail, Set.of(userToProjectRelationship))).isFalse();
          }
        });
  }
}
