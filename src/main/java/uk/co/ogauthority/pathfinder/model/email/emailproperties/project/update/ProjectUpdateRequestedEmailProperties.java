package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class ProjectUpdateRequestedEmailProperties extends EmailProperties {

  private final String projectName;
  private final String updateReason;
  private final String deadlineDate;
  private final String projectUrl;

  public ProjectUpdateRequestedEmailProperties(String recipientName,
                                               String projectName,
                                               String updateReason,
                                               String deadlineDate,
                                               String projectUrl) {
    super(NotifyTemplate.PROJECT_UPDATE_REQUESTED, recipientName);
    this.projectName = projectName;
    this.updateReason = updateReason;
    this.deadlineDate = deadlineDate;
    this.projectUrl = projectUrl;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("PROJECT_NAME", projectName);
    emailPersonalisation.put("UPDATE_REASON", updateReason);
    emailPersonalisation.put("DEADLINE_TEXT",
        deadlineDate.equals("") ? "" : String.format("An update to this project is due by %s.", deadlineDate)
    );
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
    ProjectUpdateRequestedEmailProperties that = (ProjectUpdateRequestedEmailProperties) o;
    return Objects.equals(projectName, that.projectName)
        && Objects.equals(updateReason, that.updateReason)
        && Objects.equals(deadlineDate, that.deadlineDate)
        && Objects.equals(projectUrl, that.projectUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), projectName, updateReason, deadlineDate, projectUrl);
  }
}
