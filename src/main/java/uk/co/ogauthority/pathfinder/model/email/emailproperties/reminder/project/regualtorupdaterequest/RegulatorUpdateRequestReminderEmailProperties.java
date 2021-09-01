package uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class RegulatorUpdateRequestReminderEmailProperties extends EmailProperties {

  public static final String UPDATE_REMINDER_INTRO_TEXT_MAIL_MERGE_FIELD_NAME = "UPDATE_REMINDER_INTRO_TEXT";
  public static final String UPDATE_REMINDER_SUBJECT_MAIL_MERGE_FIELD_NAME = "UPDATE_REMINDER_SUBJECT_TEXT";

  public static final String DEFAULT_UPDATE_REMINDER_INTRO_TEXT = "This is a reminder that the regulator has requested an update";
  public static final String DEFAULT_UPDATE_REMINDER_SUBJECT_TEXT = "Outstanding regulator requested update";

  private static final String UPDATE_REASON_MAIL_MERGE_FIELD_NAME = "REGULATOR_UPDATE_REASON";
  private static final String PROJECT_URL_MAIL_MERGE_FIELD_NAME = "SERVICE_LOGIN_URL";

  private final String regulatorUpdateReason;
  private final String projectUrl;

  public RegulatorUpdateRequestReminderEmailProperties(NotifyTemplate template,
                                                       String regulatorUpdateReason,
                                                       String projectUrl) {
    super(template);
    this.regulatorUpdateReason = regulatorUpdateReason;
    this.projectUrl = projectUrl;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var personalisation = super.getEmailPersonalisation();
    personalisation.put(UPDATE_REASON_MAIL_MERGE_FIELD_NAME, regulatorUpdateReason);
    personalisation.put(PROJECT_URL_MAIL_MERGE_FIELD_NAME, projectUrl);
    personalisation.put(UPDATE_REMINDER_INTRO_TEXT_MAIL_MERGE_FIELD_NAME, DEFAULT_UPDATE_REMINDER_INTRO_TEXT);
    personalisation.put(UPDATE_REMINDER_SUBJECT_MAIL_MERGE_FIELD_NAME, DEFAULT_UPDATE_REMINDER_SUBJECT_TEXT);
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
    if (!(o instanceof RegulatorUpdateRequestReminderEmailProperties)) {
      return false;
    }

    RegulatorUpdateRequestReminderEmailProperties that = (RegulatorUpdateRequestReminderEmailProperties) o;
    return Objects.equals(regulatorUpdateReason, that.regulatorUpdateReason)
        && Objects.equals(projectUrl, that.projectUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        regulatorUpdateReason,
        projectUrl
    );
  }
}
