package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class ProjectTransferredToOperatorEmailProperties extends ProjectTransferEmailProperties {

  private final String previousOperatorName;
  private final String projectUrl;

  public ProjectTransferredToOperatorEmailProperties(String recipientName,
                                                     String projectName,
                                                     String transferReason,
                                                     String previousOperatorName,
                                                     String projectUrl) {
    super(NotifyTemplate.PROJECT_TRANSFERRED_TO_OPERATOR, recipientName, projectName, transferReason);
    this.previousOperatorName = previousOperatorName;
    this.projectUrl = projectUrl;
  }

  @Override
  public Map<String, String> getEmailPersonalisation() {
    Map<String, String> emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("PREVIOUS_OPERATOR_NAME", previousOperatorName);
    emailPersonalisation.put("SERVICE_LOGIN_TEXT", EmailProperties.DEFAULT_SERVICE_LOGIN_TEXT);
    emailPersonalisation.put("PROJECT_URL", projectUrl);
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
    ProjectTransferredToOperatorEmailProperties that = (ProjectTransferredToOperatorEmailProperties) o;
    return Objects.equals(previousOperatorName, that.previousOperatorName)
        && Objects.equals(projectUrl, that.projectUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), previousOperatorName, projectUrl);
  }
}
