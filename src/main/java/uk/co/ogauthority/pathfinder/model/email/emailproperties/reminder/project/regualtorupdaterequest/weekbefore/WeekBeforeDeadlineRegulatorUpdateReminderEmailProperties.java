package uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.weekbefore;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.RegulatorUpdateRequestReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties extends RegulatorUpdateRequestReminderEmailProperties {

  private final String formattedDeadlineDate;
  private final String subjectText;
  private final String emailIntroductionText;

  public WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties(String formattedDeadlineDate,
                                                                  String regulatorUpdateReason,
                                                                  String projectUrl) {
    this(
        formattedDeadlineDate,
        regulatorUpdateReason,
        projectUrl,
        RegulatorUpdateRequestReminderEmailProperties.DEFAULT_UPDATE_REMINDER_SUBJECT_TEXT,
        String.format("%s.", RegulatorUpdateRequestReminderEmailProperties.DEFAULT_UPDATE_REMINDER_INTRO_TEXT)
    );
  }

  public WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties(String formattedDeadlineDate,
                                                                  String regulatorUpdateReason,
                                                                  String projectUrl,
                                                                  String subjectText,
                                                                  String emailIntroductionText) {
    super(
        NotifyTemplate.WEEK_BEFORE_DEADLINE_REGULATOR_UPDATE_REMINDER,
        regulatorUpdateReason,
        projectUrl
    );
    this.formattedDeadlineDate = formattedDeadlineDate;
    this.subjectText = subjectText;
    this.emailIntroductionText = emailIntroductionText;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var personalisation = super.getEmailPersonalisation();
    personalisation.put("DEADLINE_DATE", formattedDeadlineDate);
    personalisation.put(
        RegulatorUpdateRequestReminderEmailProperties.UPDATE_REMINDER_SUBJECT_MAIL_MERGE_FIELD_NAME,
        subjectText
    );
    personalisation.put(
        RegulatorUpdateRequestReminderEmailProperties.UPDATE_REMINDER_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        emailIntroductionText
    );
    return personalisation;
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }

    if (this == o) {
      return true;
    }

    if (!(o instanceof WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties)) {
      return false;
    }

    WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties that = (WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties) o;
    return Objects.equals(formattedDeadlineDate, that.formattedDeadlineDate)
        && Objects.equals(subjectText, that.subjectText)
        && Objects.equals(emailIntroductionText, that.emailIntroductionText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        formattedDeadlineDate,
        subjectText,
        emailIntroductionText
    );
  }
}
