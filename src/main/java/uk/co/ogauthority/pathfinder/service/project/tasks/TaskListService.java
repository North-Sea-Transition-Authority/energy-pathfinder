package uk.co.ogauthority.pathfinder.service.project.tasks;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.CancelDraftController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
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
