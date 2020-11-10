package uk.co.ogauthority.pathfinder.model.form.project.setup;

import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionQuestion;

public class ProjectSetupForm {

  private List<TaskListSectionQuestion> taskListSectionQuestions = new ArrayList<>();

  public ProjectSetupForm() {
  }

  public List<TaskListSectionQuestion> getTaskListSectionQuestions() {
    return taskListSectionQuestions;
  }

  public void setTaskListSectionQuestions(
      List<TaskListSectionQuestion> taskListSectionQuestions) {
    this.taskListSectionQuestions = taskListSectionQuestions;
  }
}
