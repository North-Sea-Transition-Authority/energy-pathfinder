package uk.co.ogauthority.pathfinder.service.project.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.CancelDraftProjectVersionController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListGroup;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TaskListTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class TaskListServiceTest {

  @Mock
  private TaskListGroupsService taskListGroupsService;

  private TaskListService taskListService;

  private static final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() throws Exception {
    taskListService = new TaskListService(taskListGroupsService);
  }

  @Test
  public void getTaskListModelAndView_whenFirstVersion() {
    detail.setVersion(1);

    var groups = List.of(
        TaskListTestUtil.getTaskListGroup(),
        TaskListTestUtil.getTaskListGroup()
    );

    when(taskListGroupsService.getTaskListGroups(detail)).thenReturn(groups);

    var modelAndView = taskListService.getTaskListModelAndView(detail);
    assertTaskListModelAndView(modelAndView, false, groups);
  }

  @Test
  public void getTaskListModelAndView_whenUpdate() {
    detail.setVersion(2);

    var groups = List.of(
        TaskListTestUtil.getTaskListGroup(),
        TaskListTestUtil.getTaskListGroup()
    );

    when(taskListGroupsService.getTaskListGroups(detail)).thenReturn(groups);

    var modelAndView = taskListService.getTaskListModelAndView(detail);
    assertTaskListModelAndView(modelAndView, true, groups);
  }

  private void assertTaskListModelAndView(ModelAndView modelAndView,
                                          boolean isUpdate,
                                          List<TaskListGroup> groups) {
    assertThat(modelAndView.getViewName()).isEqualTo(TaskListService.TASK_LIST_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("isUpdate", isUpdate),
        entry("groups", groups),
        entry("cancelDraftUrl", ReverseRouter.route(on(CancelDraftProjectVersionController.class)
            .getCancelDraft(detail.getProject().getId(), null, null)))
    );
  }
}
