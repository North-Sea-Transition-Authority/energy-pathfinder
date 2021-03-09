package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class OutgoingOperatorProjectTransferEmailProperties extends ProjectTransferEmailProperties {

  private final String newOperatorName;

  public OutgoingOperatorProjectTransferEmailProperties(String recipientName,
                                                        String projectName,
                                                        String transferReason,
                                                        String newOperatorName) {
    super(NotifyTemplate.OUTGOING_OPERATOR_PROJECT_TRANSFER_V1, recipientName, projectName, transferReason);
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
    OutgoingOperatorProjectTransferEmailProperties that = (OutgoingOperatorProjectTransferEmailProperties) o;
    return Objects.equals(newOperatorName, that.newOperatorName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), newOperatorName);
  }
}
