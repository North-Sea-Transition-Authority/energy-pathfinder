package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class OutgoingOperatorProjectTransferEmailProperties extends ProjectTransferEmailProperties {

  private static final String DEFAULT_TRANSFERABLE_ITEM_NAME = ProjectType.INFRASTRUCTURE.getLowercaseDisplayName();

  public static final String OUTGOING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME = "OUTGOING_OPERATOR_INTRO_TEXT";
  public static final String OUTGOING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME = "OUTGOING_OPERATOR_SUBJECT_TEXT";

  public static final String DEFAULT_OUTGOING_OPERATOR_SUBJECT_TEXT = String.format(
      "You have been removed as the operator of a %s",
      DEFAULT_TRANSFERABLE_ITEM_NAME
  );

  public static final String DEFAULT_OUTGOING_OPERATOR_INTRO_TEXT = String.format(
      "The regulator has removed you as the operator for a %s",
      DEFAULT_TRANSFERABLE_ITEM_NAME
  );

  private final String newOperatorName;

  public OutgoingOperatorProjectTransferEmailProperties(String transferReason,
                                                        String newOperatorName) {
    super(NotifyTemplate.OUTGOING_OPERATOR_PROJECT_TRANSFER, transferReason);
    this.newOperatorName = newOperatorName;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("NEW_OPERATOR_NAME", newOperatorName);

    emailPersonalisation.put(
        OUTGOING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s.", DEFAULT_OUTGOING_OPERATOR_INTRO_TEXT)
    );

    emailPersonalisation.put(
        OUTGOING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s", DEFAULT_OUTGOING_OPERATOR_SUBJECT_TEXT)
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
    OutgoingOperatorProjectTransferEmailProperties that = (OutgoingOperatorProjectTransferEmailProperties) o;
    return Objects.equals(newOperatorName, that.newOperatorName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), newOperatorName);
  }
}
