package uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter;

import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class NoProjectsUpdatedNewsletterEmailProperties extends ProjectNewsletterEmailProperties {

  public NoProjectsUpdatedNewsletterEmailProperties(String recipientIdentifier,
                                                    String manageSubscriptionUrl,
                                                    String serviceName,
                                                    String customerMnemonic) {
    super(
        NotifyTemplate.NEWSLETTER_NO_PROJECTS_UPDATED,
        recipientIdentifier,
        manageSubscriptionUrl,
        String.format(
            "No %s projects have been updated in the last month.",
            serviceName
        ),
        serviceName,
        customerMnemonic
    );
  }
}
