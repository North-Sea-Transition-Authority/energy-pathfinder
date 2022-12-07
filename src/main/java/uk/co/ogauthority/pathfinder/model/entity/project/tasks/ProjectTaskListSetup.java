package uk.co.ogauthority.pathfinder.model.entity.project.tasks;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionAnswerConverter;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionQuestion;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionQuestionConverter;

@Table(name = "project_task_list_setup")
@Entity
public class ProjectTaskListSetup extends ProjectDetailEntity {

  @Convert(converter = TaskListSectionQuestionConverter.class)
  @Lob
  @Column(name = "task_list_sections", columnDefinition = "CLOB")
  List<TaskListSectionQuestion> taskListSections;

  @Convert(converter = TaskListSectionAnswerConverter.class)
  @Lob
  @Column(name = "task_list_answers", columnDefinition = "CLOB")
  List<TaskListSectionAnswer> taskListAnswers;

  public ProjectTaskListSetup() {
  }

  public ProjectTaskListSetup(ProjectDetail detail) {
    this.projectDetail = detail;
  }

  public List<TaskListSectionQuestion> getTaskListSections() {
    return taskListSections;
  }

  public void setTaskListSections(
      List<TaskListSectionQuestion> taskListSectionQuestions) {
    this.taskListSections = taskListSectionQuestions;
  }

  public List<TaskListSectionAnswer> getTaskListAnswers() {
    return taskListAnswers;
  }

  public void setTaskListAnswers(List<TaskListSectionAnswer> taskListAnswers) {
    this.taskListAnswers = taskListAnswers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof ProjectTaskListSetup)) {
      return false;
    }
    ProjectTaskListSetup that = (ProjectTaskListSetup) o;
    return Objects.equals(taskListSections, that.taskListSections)
        && Objects.equals(taskListAnswers, that.taskListAnswers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        taskListSections,
        taskListAnswers
    );
  }

  @Override
  public String toString() {
    return "ProjectTaskListSetup{" +
        "taskListSections=" + taskListSections +
        ", taskListAnswers=" + taskListAnswers +
        '}';
  }
}
