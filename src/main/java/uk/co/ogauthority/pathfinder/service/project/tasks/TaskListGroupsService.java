package uk.co.ogauthority.pathfinder.service.project.tasks;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTaskGroup;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListEntry;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListGroup;

@Service
public class TaskListGroupsService {

  private final ProjectTaskService projectTaskService;
  private final TaskListEntryCreatorService taskListEntryCreatorService;

  public TaskListGroupsService(ProjectTaskService projectTaskService,
                               TaskListEntryCreatorService taskListEntryCreatorService) {
    this.projectTaskService = projectTaskService;
    this.taskListEntryCreatorService = taskListEntryCreatorService;
  }

  /**
   * Get a list of TaskListGroup view objects for the given project.
   * @param detail the project detail to get the task list groups for.
   * @return a list of TaskListGroup objects for each group present on the project with the appropriate tasks.
   */
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
              group.getDisplayOrder(),
              visibleTasksInGroup.stream()
                  .map(orderedTask -> taskListEntryCreatorService.createTaskListEntry(detail, orderedTask))
                  // sort the tasks by their display order
                  .sorted(Comparator.comparing(TaskListEntry::getDisplayOrder))
                  .collect(Collectors.toList())
          );
        })
        .sorted(Comparator.comparing(TaskListGroup::getDisplayOrder))
        .collect(Collectors.toList());

    groups.add(TaskListEntryCreatorService.createReviewAndSubmitGroup(detail));

    setDisplayOrderForGroups(groups);

    return groups;
  }

  /**
   * Set the displayOrder for each item based on it's position in the sorted list.
   * @param groups the list of TaskListGroups to set the displayOrder for
   */
  private void setDisplayOrderForGroups(List<TaskListGroup> groups) {
    IntStream.range(0, groups.size())
        .forEach(index -> {
          var group = groups.get(index);
          group.setDisplayOrder(index + 1);
        });
  }

  private List<ProjectTask> getProjectTasksForDetail(ProjectDetail detail) {
    return ProjectTask.stream()
        .filter(task -> projectTaskService.canShowTask(task, detail))
        .collect(Collectors.toList());
  }
}
