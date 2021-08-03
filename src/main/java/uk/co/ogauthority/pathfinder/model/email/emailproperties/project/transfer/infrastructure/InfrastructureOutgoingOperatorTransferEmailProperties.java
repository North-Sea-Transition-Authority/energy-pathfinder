package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.infrastructure;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.OutgoingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class InfrastructureOutgoingOperatorTransferEmailProperties extends OutgoingOperatorProjectTransferEmailProperties {

  private final String customerMnemonic;

  private final String projectTitle;

  public InfrastructureOutgoingOperatorTransferEmailProperties(String transferReason,
                                                               String currentOperatorName,
                                                               String customerMnemonic,
                                                               String projectTitle) {
    super(transferReason, currentOperatorName);
    this.customerMnemonic = customerMnemonic;
    this.projectTitle = projectTitle;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {

    final var emailPersonalisation = super.getEmailPersonalisation();

    final var projectTypeLowerCaseDisplayName = ProjectType.INFRASTRUCTURE.getLowercaseDisplayName();

    emailPersonalisation.put(
        OutgoingOperatorProjectTransferEmailProperties.OUTGOING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "You have been removed as the operator for the %s: %s",
            projectTypeLowerCaseDisplayName,
            projectTitle
        )
    );

    emailPersonalisation.put(
        OutgoingOperatorProjectTransferEmailProperties.OUTGOING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("" +
            "The %s have removed you as the operator of the %s: %s.",
            customerMnemonic,
            projectTypeLowerCaseDisplayName,
            projectTitle
        )
    );

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
    InfrastructureOutgoingOperatorTransferEmailProperties that = (InfrastructureOutgoingOperatorTransferEmailProperties) o;
    return Objects.equals(customerMnemonic, that.customerMnemonic)
        && Objects.equals(projectTitle, that.projectTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), customerMnemonic, projectTitle);
  }
}
