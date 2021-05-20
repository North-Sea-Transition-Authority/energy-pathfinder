package uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class ProjectsUpdatedNewsletterEmailProperties extends ProjectNewsletterEmailProperties {

  private static final String INTRODUCTION_TEXT = String.format(
      "The following %s projects have been updated in the last month:",
      EmailProperties.SERVICE_NAME
  );

  private final List<String> projectsUpdated;

  public ProjectsUpdatedNewsletterEmailProperties(String recipientIdentifier,
                                                  String unsubscribeUrl,
                                                  List<String> projectsUpdated) {
    super(
        NotifyTemplate.NEWSLETTER_WITH_PROJECTS_UPDATED,
        recipientIdentifier,
        unsubscribeUrl,
        INTRODUCTION_TEXT
    );
    this.projectsUpdated = projectsUpdated;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("UPDATED_PROJECTS_LIST", projectsUpdated);
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
    ProjectsUpdatedNewsletterEmailProperties that = (ProjectsUpdatedNewsletterEmailProperties) o;
    return Objects.equals(projectsUpdated, that.projectsUpdated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), projectsUpdated);
  }
}
