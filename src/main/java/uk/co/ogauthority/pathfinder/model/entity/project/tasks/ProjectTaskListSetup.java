package uk.co.ogauthority.pathfinder.model.entity.project.tasks;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionQuestion;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionQuestionsConverter;

@Table(name = "project_task_list_setup")
@Entity
public class ProjectTaskListSetup extends ProjectDetailEntity {

  @Convert(converter = TaskListSectionQuestionsConverter.class)
  @Lob
  @Column(name = "task_list_sections", columnDefinition = "CLOB")
  List<TaskListSectionQuestion> taskListSectionQuestions;

  public ProjectTaskListSetup() {
  }

  public ProjectTaskListSetup(ProjectDetail detail) {
    this.projectDetail = detail;
  }

  public ProjectTaskListSetup(ProjectDetail detail, List<TaskListSectionQuestion> taskListSectionQuestions) {
    this.projectDetail = detail;
    this.taskListSectionQuestions = taskListSectionQuestions;
  }

  public List<TaskListSectionQuestion> getTaskListSectionQuestions() {
    return taskListSectionQuestions;
  }

  public void setTaskListSectionQuestions(
      List<TaskListSectionQuestion> taskListSectionQuestions) {
    this.taskListSectionQuestions = taskListSectionQuestions;
  }


}
