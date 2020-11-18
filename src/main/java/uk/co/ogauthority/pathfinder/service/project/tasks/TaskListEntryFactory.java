package uk.co.ogauthority.pathfinder.service.project.tasks;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.submission.SubmitProjectController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.GeneralPurposeProjectTask;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListEntry;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListGroup;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Service
public class TaskListEntryFactory {

  public static final String REVIEW_AND_SUBMIT_GROUP_TITLE = "Review and submit";
  private final ProjectTaskService projectTaskService;


  @Autowired
  public TaskListEntryFactory(ProjectTaskService projectTaskService) {
    this.projectTaskService = projectTaskService;
  }

  public TaskListEntry createApplicationTaskListEntry(ProjectDetail detail,
                                               GeneralPurposeProjectTask projectTask) {
    return new TaskListEntry(
        projectTask.getDisplayName(),
        projectTask.getTaskLandingPageRoute(detail.getProject()),
        projectTaskService.isTaskComplete(projectTask, detail),
        projectTask.getDisplayOrder()
    );
  }

  //TODO do we need this??
  public TaskListEntry createNoTasksEntry(ProjectDetail detail) {
    return new TaskListEntry(
        "No tasks",
        ControllerUtils.getBackToTaskListUrl(detail.getProject().getId()),
        false,
        false,
        0
    );
  }

  public TaskListEntry createReviewAndSubmitTask(ProjectDetail detail) {
    return new TaskListEntry(
        SubmitProjectController.PAGE_NAME,
        ReverseRouter.route(on(SubmitProjectController.class)
            .getProjectSummary(detail.getProject().getId(), null)),
        false,
        false,
        999
    );
  }

  public TaskListGroup createReviewAndSubmitGroup(ProjectDetail detail, int displayOrder) {
    return new TaskListGroup(
        REVIEW_AND_SUBMIT_GROUP_TITLE,
        displayOrder,
        List.of(createReviewAndSubmitTask(detail))
    );
  }
}

