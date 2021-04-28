package uk.co.ogauthority.pathfinder.model.email.emailproperties.teammanagement;

import java.util.Map;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class RemovedFromTeamEmailProperties extends EmailProperties {

  private final String teamName;
  private final String removedByUserName;

  public RemovedFromTeamEmailProperties(String recipientIdentifier, String teamName, String removedByUserName) {
    super(NotifyTemplate.REMOVED_FROM_TEAM, recipientIdentifier);
    this.teamName = teamName;
    this.removedByUserName = removedByUserName;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("TEAM_NAME", teamName);
    emailPersonalisation.put("REMOVED_BY_USER_NAME", removedByUserName);
    return emailPersonalisation;
  }
}
