package uk.co.ogauthority.pathfinder.model.enums.project.tasks;

import com.google.common.base.Splitter;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

@Converter
public class TaskListSectionQuestionsConverter implements AttributeConverter<List<TaskListSectionQuestion>, String> {
  @Override
  public String convertToDatabaseColumn(List<TaskListSectionQuestion> taskListSectionQuestions) {
    if (taskListSectionQuestions != null) {
      List<String> mnemonicList = taskListSectionQuestions.stream()
          .map(TaskListSectionQuestion::name)
          .collect(Collectors.toList());

      return String.join(",", mnemonicList);
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
