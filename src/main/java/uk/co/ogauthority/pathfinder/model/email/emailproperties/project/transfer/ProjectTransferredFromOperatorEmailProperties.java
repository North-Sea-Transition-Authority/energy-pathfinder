package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class ProjectTransferredFromOperatorEmailProperties extends ProjectTransferEmailProperties {

  private final String newOperatorName;

  public ProjectTransferredFromOperatorEmailProperties(String recipientName,
                                                       String projectName,
                                                       String transferReason,
                                                       String newOperatorName) {
    super(NotifyTemplate.PROJECT_TRANSFERRED_FROM_OPERATOR, recipientName, projectName, transferReason);
    this.newOperatorName = newOperatorName;
  }

  @Override
  public Map<String, String> getEmailPersonalisation() {
    Map<String, String> emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("NEW_OPERATOR_NAME", newOperatorName);
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
    ProjectTransferredFromOperatorEmailProperties that = (ProjectTransferredFromOperatorEmailProperties) o;
    return Objects.equals(newOperatorName, that.newOperatorName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), newOperatorName);
  }
}
