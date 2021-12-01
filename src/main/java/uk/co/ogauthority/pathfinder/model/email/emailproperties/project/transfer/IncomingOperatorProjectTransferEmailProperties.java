package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class IncomingOperatorProjectTransferEmailProperties extends ProjectTransferEmailProperties {

  private static final String DEFAULT_TRANSFERABLE_ITEM_NAME = ProjectType.INFRASTRUCTURE.getLowercaseDisplayName();

  public static final String INCOMING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME = "INCOMING_OPERATOR_INTRO_TEXT";
  public static final String INCOMING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME = "INCOMING_OPERATOR_SUBJECT_TEXT";

  public static final String DEFAULT_INCOMING_OPERATOR_SUBJECT_TEXT = String.format(
      "You have been added as the operator for a %s",
      DEFAULT_TRANSFERABLE_ITEM_NAME
  );

  public static final String DEFAULT_INCOMING_OPERATOR_INTRO_TEXT = String.format(
      "The regulator has added you as the operator for a %s",
      DEFAULT_TRANSFERABLE_ITEM_NAME
  );

  private final String previousOperatorName;
  private final String projectUrl;

  public IncomingOperatorProjectTransferEmailProperties(String transferReason,
                                                        String previousOperatorName,
                                                        String projectUrl) {
    super(NotifyTemplate.INCOMING_OPERATOR_PROJECT_TRANSFER, transferReason);
    this.previousOperatorName = previousOperatorName;
    this.projectUrl = projectUrl;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("PREVIOUS_OPERATOR_NAME", previousOperatorName);
    emailPersonalisation.put("PROJECT_URL", projectUrl);

    emailPersonalisation.put(
        INCOMING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s.", DEFAULT_INCOMING_OPERATOR_INTRO_TEXT)
    );

    emailPersonalisation.put(
        INCOMING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s", DEFAULT_INCOMING_OPERATOR_SUBJECT_TEXT)
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
    IncomingOperatorProjectTransferEmailProperties that = (IncomingOperatorProjectTransferEmailProperties) o;
    return Objects.equals(previousOperatorName, that.previousOperatorName)
        && Objects.equals(projectUrl, that.projectUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), previousOperatorName, projectUrl);
  }
}
