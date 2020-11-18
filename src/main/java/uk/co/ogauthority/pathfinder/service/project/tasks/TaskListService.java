package uk.co.ogauthority.pathfinder.service.project.tasks;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTaskGroup;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListEntry;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListGroup;

@Service
public class TaskListService {

  public static final String TASK_LIST_TEMPLATE_PATH = "project/taskList";

  private final ProjectTaskService projectTaskService;
  private final TaskListEntryFactory taskListEntryFactory;

  @Autowired
  public TaskListService(ProjectTaskService projectTaskService,
                         TaskListEntryFactory taskListEntryFactory) {
    this.projectTaskService = projectTaskService;
    this.taskListEntryFactory = taskListEntryFactory;
  }


  //Method to return a list of TaskListGroup view objects
  public List<TaskListGroup> getTaskListGroups(ProjectDetail detail) {
    Set<ProjectTask> tasks = new HashSet<>(getProjectTasksForDetail(detail));

    var groups = ProjectTaskGroup.asList().stream()
        .filter(taskGroup -> !SetUtils.intersection(taskGroup.getProjectTaskSet(), tasks).isEmpty())
        .map(group -> {
          // per group, filter out tasks that are not shown.
          var visibleTasksInGroup = group.getTasks().stream()
              .filter(tasks::contains)
              .collect(Collectors.toList());

          return new TaskListGroup(
              group.getDisplayName(),
              group.getDisplayOrder(), //TODO this should probably change depending on the position in the list? Maybe do in template?
              visibleTasksInGroup.stream()
                  .map(orderedTaskGroupTask -> taskListEntryFactory.createApplicationTaskListEntry(detail,
                      orderedTaskGroupTask))
                  // sort the tasks by their display order
                  .sorted(Comparator.comparing(TaskListEntry::getDisplayOrder))
                  .collect(Collectors.toList())
          );
        })
        .sorted(Comparator.comparing(TaskListGroup::getDisplayOrder))
        .collect(Collectors.toList());

    groups.add(taskListEntryFactory.createReviewAndSubmitGroup(detail, groups.size()+1));

    return groups;
  }

  //Return a list of the tasks that appear on the task list for a given detail
  public List<ProjectTask> getProjectTasksForDetail(ProjectDetail detail) {
    return ProjectTask.stream()
        .filter(task -> projectTaskService.canShowTask(task, detail))
        .collect(Collectors.toList());
  }


  public ModelAndView getTaskListModelAndView(ProjectDetail detail) {
    var modelAndView = new ModelAndView(TASK_LIST_TEMPLATE_PATH);

    modelAndView.addObject(
        "groups",
        getTaskListGroups(detail)
    );

    //build up groups

    //add review and submit task/group

    return modelAndView;
  }

}