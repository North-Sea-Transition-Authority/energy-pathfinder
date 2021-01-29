package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class NoUpdateNotificationEmailProperties extends ProjectUpdateEmailProperties {

  private final String noUpdateReason;

  public NoUpdateNotificationEmailProperties(String projectName, String serviceLoginUrl, String noUpdateReason) {
    super(NotifyTemplate.NO_UPDATE_NOTIFICATION, projectName, serviceLoginUrl);
    this.noUpdateReason = noUpdateReason;
  }

  @Override
  public Map<String, String> getEmailPersonalisation() {
    Map<String, String> emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("NO_UPDATE_REASON", noUpdateReason);
    return emailPersonalisation;
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (this == o) {
      return true;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    NoUpdateNotificationEmailProperties that = (NoUpdateNotificationEmailProperties) o;
    return Objects.equals(noUpdateReason, that.noUpdateReason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), noUpdateReason);
  }
}
