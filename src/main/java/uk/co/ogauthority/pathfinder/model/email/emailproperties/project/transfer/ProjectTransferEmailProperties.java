package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class ProjectTransferEmailProperties extends EmailProperties {

  private final String projectName;
  private final String transferReason;

  public ProjectTransferEmailProperties(NotifyTemplate template,
                                        String recipientName,
                                        String projectName,
                                        String transferReason) {
    super(template, recipientName);
    this.projectName = projectName;
    this.transferReason = transferReason;
  }

  @Override
  public Map<String, String> getEmailPersonalisation() {
    Map<String, String> emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("PROJECT_NAME", projectName);
    emailPersonalisation.put("TRANSFER_REASON", transferReason);
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
    ProjectTransferEmailProperties that = (ProjectTransferEmailProperties) o;
    return Objects.equals(projectName, that.projectName)
        && Objects.equals(transferReason, that.transferReason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), projectName, transferReason);
  }
}
