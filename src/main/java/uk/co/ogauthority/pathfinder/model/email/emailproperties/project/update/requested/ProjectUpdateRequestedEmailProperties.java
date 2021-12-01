package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class ProjectUpdateRequestedEmailProperties extends EmailProperties {

  public static final String UPDATE_REQUESTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME = "UPDATE_REQUESTED_INTRO_TEXT";
  public static final String UPDATE_REQUESTED_SUBJECT_MAIL_MERGE_FIELD_NAME = "UPDATE_REQUESTED_SUBJECT_TEXT";

  public static final String DEFAULT_UPDATE_REQUESTED_INTRO_TEXT = "An update has been requested by the regulator";
  public static final String DEFAULT_UPDATE_REQUESTED_SUBJECT_TEXT = "An update has been requested";

  private final String updateReason;
  private final String deadlineDate;
  private final String projectUrl;

  public ProjectUpdateRequestedEmailProperties(String updateReason,
                                               String deadlineDate,
                                               String projectUrl) {
    super(NotifyTemplate.PROJECT_UPDATE_REQUESTED);
    this.updateReason = updateReason;
    this.deadlineDate = deadlineDate;
    this.projectUrl = projectUrl;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    final var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("UPDATE_REASON", updateReason);
    emailPersonalisation.put("DEADLINE_TEXT",
        deadlineDate.equals("") ? "" : String.format("An update is due by %s.", deadlineDate)
    );
    emailPersonalisation.put("PROJECT_URL", projectUrl);

    emailPersonalisation.put(
        UPDATE_REQUESTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s.", DEFAULT_UPDATE_REQUESTED_INTRO_TEXT)
    );

    emailPersonalisation.put(
        UPDATE_REQUESTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format("%s", DEFAULT_UPDATE_REQUESTED_SUBJECT_TEXT)
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
    ProjectUpdateRequestedEmailProperties that = (ProjectUpdateRequestedEmailProperties) o;
    return Objects.equals(updateReason, that.updateReason)
        && Objects.equals(deadlineDate, that.deadlineDate)
        && Objects.equals(projectUrl, that.projectUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), updateReason, deadlineDate, projectUrl);
  }
}
