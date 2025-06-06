package uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions;

import com.google.common.base.Splitter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

@Converter
public class TaskListSectionQuestionConverter implements AttributeConverter<List<TaskListSectionQuestion>, String> {
  @Override
  public String convertToDatabaseColumn(List<TaskListSectionQuestion> taskListSectionQuestions) {
    if (taskListSectionQuestions != null) {
      List<String> questionList = taskListSectionQuestions.stream()
          .map(TaskListSectionQuestion::name)
          .collect(Collectors.toList());

      return String.join(",", questionList);
    }

    return null;
  }

  @Override
  public List<TaskListSectionQuestion> convertToEntityAttribute(String taskListSectionQuestions) {
    List<String> questionList =
        Splitter.on(",")
            .omitEmptyStrings()
            .trimResults()
            .splitToList(StringUtils.defaultString(taskListSectionQuestions));

    return questionList.stream()
        .map(taskListSectionQuestion -> EnumUtils.getEnum(TaskListSectionQuestion.class, taskListSectionQuestion))
        .collect(Collectors.toList());
  }
}
