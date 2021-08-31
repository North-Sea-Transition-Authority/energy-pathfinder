package uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly;

import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public abstract class QuarterlyUpdateReminderEmailProperties extends EmailProperties {

  public QuarterlyUpdateReminderEmailProperties(NotifyTemplate template,
                                                String recipientIdentifier) {
    super(template, recipientIdentifier);
  }
}