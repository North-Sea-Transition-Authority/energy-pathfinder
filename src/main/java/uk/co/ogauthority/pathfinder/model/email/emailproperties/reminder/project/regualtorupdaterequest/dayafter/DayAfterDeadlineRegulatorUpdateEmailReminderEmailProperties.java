package uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.dayafter;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.RegulatorUpdateRequestReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties extends RegulatorUpdateRequestReminderEmailProperties {

  public static final String DEFAULT_INTRODUCTION_TEXT_PREFIX = "This is a final reminder that the regulator requested update";

  private final String subjectText;
  private final String emailIntroductionText;

  public DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties(String regulatorUpdateReason,
                                                                     String projectUrl) {
    this(
        regulatorUpdateReason,
        projectUrl,
        RegulatorUpdateRequestReminderEmailProperties.DEFAULT_UPDATE_REMINDER_SUBJECT_TEXT,
        String.format("%s was due yesterday.", DEFAULT_INTRODUCTION_TEXT_PREFIX)
    );
  }

  public DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties(String regulatorUpdateReason,
                                                                     String projectUrl,
                                                                     String subjectText,
                                                                     String emailIntroductionText) {
    super(
        NotifyTemplate.DAY_AFTER_DEADLINE_REGULATOR_UPDATE_REMINDER,
        regulatorUpdateReason,
        projectUrl
    );
    this.subjectText = subjectText;
    this.emailIntroductionText = emailIntroductionText;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var personalisation = super.getEmailPersonalisation();
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

    if (!(o instanceof DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties)) {
      return false;
    }

    DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties that = (DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties) o;
    return Objects.equals(subjectText, that.subjectText)
        && Objects.equals(emailIntroductionText, that.emailIntroductionText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        subjectText,
        emailIntroductionText
    );
  }
}
