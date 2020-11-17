package uk.co.ogauthority.pathfinder.model.entity.project.tasks;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionAnswerConverter;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionQuestion;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionQuestionConverter;

@Table(name = "project_task_list_setup")
@Entity
public class ProjectTaskListSetup extends ProjectDetailEntity {

  @Convert(converter = TaskListSectionQuestionConverter.class)
  @Lob
  @Column(name = "task_list_sections", columnDefinition = "CLOB")
  List<TaskListSectionQuestion> taskListSectionQuestions;

  @Convert(converter = TaskListSectionAnswerConverter.class)
  @Lob
  @Column(name = "task_list_answers", columnDefinition = "CLOB")
  List<TaskListSectionAnswer> taskListAnswers;

  public ProjectTaskListSetup() {
  }

  public ProjectTaskListSetup(ProjectDetail detail) {
    this.projectDetail = detail;
  }

  public ProjectTaskListSetup(ProjectDetail detail,
                              List<TaskListSectionQuestion> taskListSectionQuestions,
                              List<TaskListSectionAnswer> taskListAnswers) {
    this.projectDetail = detail;
    this.taskListSectionQuestions = taskListSectionQuestions;
    this.taskListAnswers = taskListAnswers;
  }

  public List<TaskListSectionQuestion> getTaskListSectionQuestions() {
    return taskListSectionQuestions;
  }

  public void setTaskListSectionQuestions(
      List<TaskListSectionQuestion> taskListSectionQuestions) {
    this.taskListSectionQuestions = taskListSectionQuestions;
  }

  public List<TaskListSectionAnswer> getTaskListAnswers() {
    return taskListAnswers;
  }

  public void setTaskListAnswers(
      List<TaskListSectionAnswer> taskListAnswers) {
    this.taskListAnswers = taskListAnswers;
  }
}
