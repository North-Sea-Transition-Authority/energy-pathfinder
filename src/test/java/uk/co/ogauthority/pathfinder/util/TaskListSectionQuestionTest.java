package uk.co.ogauthority.pathfinder.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionQuestion;

@RunWith(MockitoJUnitRunner.class)
public class TaskListSectionQuestionTest {

  private static final List<ProjectTask> ALL_QUESTION_TASKS = List.of(
      ProjectTask.UPCOMING_TENDERS,
      ProjectTask.AWARDED_CONTRACTS,
      ProjectTask.COLLABORATION_OPPORTUNITIES,
      ProjectTask.WELLS,
      ProjectTask.PLATFORM_FPSO,
      ProjectTask.INTEGRATED_RIGS,
      ProjectTask.SUBSEA_INFRASTRUCTURE,
      ProjectTask.PIPELINES
  );

  private static final List<ProjectTask> NON_DECOM_TASKS = List.of(
      ProjectTask.UPCOMING_TENDERS,
      ProjectTask.AWARDED_CONTRACTS,
      ProjectTask.COLLABORATION_OPPORTUNITIES
  );

  @Test
  public void getAllValues_ordering() {
    var tasks = TaskListSectionQuestion.getAllValues()
        .stream().map(TaskListSectionQuestion::getProjectTask)
        .collect(Collectors.toList());

    assertThat(tasks).isEqualTo(ALL_QUESTION_TASKS);
  }

  @Test
  public void getNonDecommissioningRelatedValues_ordering() {
    var tasks = TaskListSectionQuestion.getNonDecommissioningRelatedValues()
        .stream().map(TaskListSectionQuestion::getProjectTask)
        .collect(Collectors.toList());

    assertThat(tasks).isEqualTo(NON_DECOM_TASKS);
  }
}
