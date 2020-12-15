package uk.co.ogauthority.pathfinder.service.project.tasks;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.CancelDraftController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTaskGroup;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListEntry;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListGroup;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@Service
public class TaskListService {

  public static final String TASK_LIST_TEMPLATE_PATH = "project/taskList";

  private final TaskListGroupsService taskListGroupsService;

  @Autowired
  public TaskListService(TaskListGroupsService taskListGroupsService) {
    this.taskListGroupsService = taskListGroupsService;
  }

  public ModelAndView getTaskListModelAndView(ProjectDetail detail) {
    return new ModelAndView(TASK_LIST_TEMPLATE_PATH)
        .addObject("isUpdate", !detail.isFirstVersion())
        .addObject("groups", taskListGroupsService.getTaskListGroups(detail))
        .addObject("cancelDraftUrl", ReverseRouter.route(on(CancelDraftController.class)
            .getCancelDraft(detail.getProject().getId(), null, null)));
  }
}
