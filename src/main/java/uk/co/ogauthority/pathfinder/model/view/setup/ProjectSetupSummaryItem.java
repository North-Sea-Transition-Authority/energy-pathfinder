package uk.co.ogauthority.pathfinder.model.view.setup;

import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionQuestion;

public class ProjectSetupSummaryItem {

  private TaskListSectionQuestion question;

  private String prompt;

  private String answerValue;

  public ProjectSetupSummaryItem(TaskListSectionQuestion question, String prompt) {
    this.question = question;
    this.prompt = prompt;
  }

  public TaskListSectionQuestion getQuestion() {
    return question;
  }

  public void setQuestion(TaskListSectionQuestion question) {
    this.question = question;
  }

  public String getPrompt() {
    return prompt;
  }

  public void setPrompt(String prompt) {
    this.prompt = prompt;
  }

  public String getAnswerValue() {
    return answerValue;
  }

  public void setAnswerValue(String answerValue) {
    this.answerValue = answerValue;
  }
}
