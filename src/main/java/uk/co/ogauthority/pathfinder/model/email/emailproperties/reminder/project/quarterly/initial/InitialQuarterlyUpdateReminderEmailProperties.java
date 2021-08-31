package uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.initial;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.QuarterlyUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class InitialQuarterlyUpdateReminderEmailProperties extends QuarterlyUpdateReminderEmailProperties {

  private final String operatorName;
  private final List<String> remindableProjects;
  private final String serviceLoginUrl;

  public InitialQuarterlyUpdateReminderEmailProperties(String recipientIdentifier,
                                                       String operatorName,
                                                       List<String> remindableProjects,
                                                       String serviceLoginUrl) {
    super(NotifyTemplate.INITIAL_QUARTERLY_PROJECT_UPDATE_REMINDER, recipientIdentifier);
    this.operatorName = operatorName;
    this.remindableProjects = remindableProjects;
    this.serviceLoginUrl = serviceLoginUrl;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("OPERATOR_NAME", operatorName);
    emailPersonalisation.put("OPERATOR_PROJECTS", remindableProjects);
    emailPersonalisation.put("SERVICE_LOGIN_URL", serviceLoginUrl);
    return emailPersonalisation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof InitialQuarterlyUpdateReminderEmailProperties)) {
      return false;
    }

    InitialQuarterlyUpdateReminderEmailProperties that = (InitialQuarterlyUpdateReminderEmailProperties) o;
    return Objects.equals(operatorName, that.operatorName)
        && Objects.equals(remindableProjects, that.remindableProjects)
        && Objects.equals(serviceLoginUrl, that.serviceLoginUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        operatorName,
        remindableProjects,
        serviceLoginUrl
    );
  }
}
