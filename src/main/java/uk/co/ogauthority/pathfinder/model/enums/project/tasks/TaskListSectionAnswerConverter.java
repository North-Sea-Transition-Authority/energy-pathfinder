package uk.co.ogauthority.pathfinder.model.enums.project.tasks;

import com.google.common.base.Splitter;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

@Converter
public class TaskListSectionAnswerConverter implements AttributeConverter<List<TaskListSectionAnswer>, String> {
  @Override
  public String convertToDatabaseColumn(List<TaskListSectionAnswer> taskListSectionAnswers) {
    if (taskListSectionAnswers != null) {
      List<String> mnemonicList = taskListSectionAnswers.stream()
          .map(TaskListSectionAnswer::name)
          .collect(Collectors.toList());

      return String.join(",", mnemonicList);
    }

    return null;
  }

  @Override
  public List<TaskListSectionAnswer> convertToEntityAttribute(String taskListSectionAnswers) {
    List<String> questionList =
        Splitter.on(",")
            .omitEmptyStrings()
            .trimResults()
            .splitToList(StringUtils.defaultString(taskListSectionAnswers));

    return questionList.stream()
        .map(taskListQuestionAnswer -> EnumUtils.getEnum(TaskListSectionAnswer.class, taskListQuestionAnswer))
        .collect(Collectors.toList());
  }
}
