package uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter;

import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class NoProjectsUpdatedNewsletterEmailProperties extends ProjectNewsletterEmailProperties {

  private static final String INTRODUCTION_TEXT = String.format(
      "No %s projects have been updated in the last month.",
      EmailProperties.SERVICE_NAME
  );

  public NoProjectsUpdatedNewsletterEmailProperties(String recipientIdentifier,
                                                    String unsubscribeUrl) {
    super(
        NotifyTemplate.NEWSLETTER_NO_PROJECTS_UPDATED,
        recipientIdentifier,
        unsubscribeUrl,
        INTRODUCTION_TEXT
    );
  }
}
