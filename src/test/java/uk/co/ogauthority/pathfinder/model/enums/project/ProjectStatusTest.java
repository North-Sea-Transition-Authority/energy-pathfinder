package uk.co.ogauthority.pathfinder.model.enums.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ProjectStatusTest {

  @Test
  void getPostSubmissionProjectStatuses_HasExpectedContents() {
    var preSubmissionProjectStatuses = Set.of(ProjectStatus.DRAFT);

    var expectedPostSubmissionProjectStatuses = Arrays.stream(ProjectStatus.values())
        .filter(projectStatus -> !preSubmissionProjectStatuses.contains(projectStatus))
        .collect(Collectors.toSet());
    var actualPostSubmissionProjectStatuses = ProjectStatus.getPostSubmissionProjectStatuses();

    assertThat(actualPostSubmissionProjectStatuses).isEqualTo(expectedPostSubmissionProjectStatuses);
  }
}
