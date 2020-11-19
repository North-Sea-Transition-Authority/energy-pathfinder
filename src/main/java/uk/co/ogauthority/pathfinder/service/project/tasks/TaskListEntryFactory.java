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

@Service
public class TaskListEntryFactory {

  public static final String REVIEW_AND_SUBMIT_GROUP_TITLE = "Review and submit";
  public static final int REVIEW_AND_SUBMIT_DISPLAY_ORDER = 999;
  private final ProjectTaskService projectTaskService;


  @Autowired
  public TaskListEntryFactory(ProjectTaskService projectTaskService) {
    this.projectTaskService = projectTaskService;
  }

  public TaskListEntry createTaskListEntry(ProjectDetail detail,
                                           GeneralPurposeProjectTask projectTask) {
    return new TaskListEntry(
        projectTask.getDisplayName(),
        projectTask.getTaskLandingPageRoute(detail.getProject()),
        projectTaskService.isTaskComplete(projectTask, detail),
        projectTask.getDisplayOrder()
    );
  }

  public static TaskListEntry createReviewAndSubmitTask(ProjectDetail detail) {
    return new TaskListEntry(
        REVIEW_AND_SUBMIT_GROUP_TITLE,
        ReverseRouter.route(on(SubmitProjectController.class)
            .getProjectSummary(detail.getProject().getId(), null)),
        false,
        false,
        REVIEW_AND_SUBMIT_DISPLAY_ORDER
    );
  }

  public static TaskListGroup createReviewAndSubmitGroup(ProjectDetail detail) {
    return new TaskListGroup(
        REVIEW_AND_SUBMIT_GROUP_TITLE,
        REVIEW_AND_SUBMIT_DISPLAY_ORDER,
        List.of(createReviewAndSubmitTask(detail))
    );
  }
}

