package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.infrastructure;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.IncomingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class InfrastructureIncomingOperatorTransferEmailProperties extends IncomingOperatorProjectTransferEmailProperties {

  private final String customerMnemonic;

  private final String projectTitle;

  public InfrastructureIncomingOperatorTransferEmailProperties(String transferReason,
                                                               String previousOperatorName,
                                                               String projectUrl,
                                                               String customerMnemonic,
                                                               String projectTitle) {
    super(transferReason, previousOperatorName, projectUrl);
    this.customerMnemonic = customerMnemonic;
    this.projectTitle = projectTitle;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {

    final var emailPersonalisation = super.getEmailPersonalisation();

    final var projectTypeLowerCaseDisplayName = ProjectType.INFRASTRUCTURE.getLowercaseDisplayName();

    emailPersonalisation.put(
        IncomingOperatorProjectTransferEmailProperties.INCOMING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "You have been added as the operator/developer for the %s: %s",
            projectTypeLowerCaseDisplayName,
            projectTitle
        )
    );

    emailPersonalisation.put(
        IncomingOperatorProjectTransferEmailProperties.INCOMING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "The %s have added you as the operator/developer for the %s: %s.",
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
    InfrastructureIncomingOperatorTransferEmailProperties that = (InfrastructureIncomingOperatorTransferEmailProperties) o;
    return Objects.equals(customerMnemonic, that.customerMnemonic)
        && Objects.equals(projectTitle, that.projectTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), customerMnemonic, projectTitle);
  }
}
