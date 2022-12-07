package uk.co.ogauthority.pathfinder.service.project.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTaskServiceTest {

  @Mock
  private ApplicationContext springApplicationContext;

  @Mock
  private ProjectFormSectionService projectFormSectionService;

  private ProjectTaskService projectTaskService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() throws Exception {
    when(springApplicationContext.getBean(any(Class.class))).thenAnswer(invocation -> {
      Class clazz = invocation.getArgument(0);
      if (ProjectFormSectionService.class.isAssignableFrom(clazz)) {
        return projectFormSectionService;
      } else {
        return mock(clazz);
      }
    });

    projectTaskService = new ProjectTaskService(springApplicationContext);

  }

  @Test
  public void canShowTask_allConditionalTasksShown() {
    var relations = Set.of(UserToProjectRelationship.OPERATOR);
    when(projectFormSectionService.canShowInTaskList(detail, relations)).thenReturn(true);

    ProjectTask.stream().forEach(projectTask -> {
      assertThat(projectTaskService.canShowTask(projectTask, detail, relations)).isTrue();
    });
  }

  @Test
  public void canShowTask_noConditionalTasksShown() {
    ProjectTask.stream().forEach(projectTask -> {
      assertThat(projectTaskService.canShowTask(projectTask, detail, Set.of())).isFalse();
    });
  }

  @Test
  public void canShowTask_verifyInteractions() {
    var relations = Set.of(UserToProjectRelationship.OPERATOR);

    assertThat(projectTaskService.canShowTask(ProjectTask.PIPELINES, detail, relations))
        .isFalse();

    verify(projectFormSectionService, times(1)).canShowInTaskList(detail, relations);

  }

  @Test
  public void isTaskComplete_verifyInteractions() {
    when(projectFormSectionService.isComplete(detail)).thenReturn(true);

    assertThat(projectTaskService.isTaskComplete(ProjectTask.PIPELINES, detail)).isTrue();

    verify(projectFormSectionService, times(1)).isComplete(detail);
  }
}
