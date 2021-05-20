package uk.co.ogauthority.pathfinder.model.email.emailproperties.teammanagement;

import java.util.Map;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class AddedToTeamEmailProperties extends EmailProperties {

  private final String teamName;
  private final String addedByUserName;
  private final String rolesCsv;
  private final String serviceLoginUrl;

  public AddedToTeamEmailProperties(String recipientIdentifier,
                                    String teamName,
                                    String addedByUserName,
                                    String rolesCsv,
                                    String serviceLoginUrl) {
    super(NotifyTemplate.ADDED_TO_TEAM, recipientIdentifier);
    this.teamName = teamName;
    this.addedByUserName = addedByUserName;
    this.rolesCsv = rolesCsv;
    this.serviceLoginUrl = serviceLoginUrl;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("TEAM_NAME", teamName);
    emailPersonalisation.put("ADDED_BY_USER_NAME", addedByUserName);
    emailPersonalisation.put("ROLES_CSV", rolesCsv);
    emailPersonalisation.put("SERVICE_LOGIN_URL", serviceLoginUrl);
    emailPersonalisation.put("SERVICE_LOGIN_TEXT", EmailProperties.DEFAULT_SERVICE_LOGIN_TEXT);
    return emailPersonalisation;
  }
}
