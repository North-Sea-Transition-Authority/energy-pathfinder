package uk.co.ogauthority.pathfinder.model.email.emailproperties;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class ProjectUpdateEmailProperties extends EmailProperties {

  private final String projectName;
  private final String serviceLoginUrl;

  public ProjectUpdateEmailProperties(String projectName, String serviceLoginUrl) {
    super(NotifyTemplate.PROJECT_UPDATE_SUBMITTED);
    this.projectName = projectName;
    this.serviceLoginUrl = serviceLoginUrl;
  }

  @Override
  public Map<String, String> getEmailPersonalisation() {
    Map<String, String> emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("PROJECT_NAME", projectName);
    emailPersonalisation.put("SERVICE_LOGIN_URL", serviceLoginUrl);
    return emailPersonalisation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectUpdateEmailProperties that = (ProjectUpdateEmailProperties) o;
    return Objects.equals(projectName, that.projectName)
        && Objects.equals(serviceLoginUrl, that.serviceLoginUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectName, serviceLoginUrl);
  }
}
