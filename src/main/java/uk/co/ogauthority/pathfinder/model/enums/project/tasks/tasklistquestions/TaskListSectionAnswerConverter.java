package uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions;

import com.google.common.base.Splitter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

@Converter
public class TaskListSectionAnswerConverter implements AttributeConverter<List<TaskListSectionAnswer>, String> {
  @Override
  public String convertToDatabaseColumn(List<TaskListSectionAnswer> taskListSectionAnswers) {
    if (taskListSectionAnswers != null) {
      List<String> answerList = taskListSectionAnswers.stream()
          .map(TaskListSectionAnswer::name)
          .collect(Collectors.toList());

      return String.join(",", answerList);
    }

    return null;
  }

  @Override
  public List<TaskListSectionAnswer> convertToEntityAttribute(String taskListSectionAnswers) {
    List<String> answerList =
        Splitter.on(",")
            .omitEmptyStrings()
            .trimResults()
            .splitToList(StringUtils.defaultString(taskListSectionAnswers));

    return answerList.stream()
        .map(taskListQuestionAnswer -> EnumUtils.getEnum(TaskListSectionAnswer.class, taskListQuestionAnswer))
        .collect(Collectors.toList());
  }
}
