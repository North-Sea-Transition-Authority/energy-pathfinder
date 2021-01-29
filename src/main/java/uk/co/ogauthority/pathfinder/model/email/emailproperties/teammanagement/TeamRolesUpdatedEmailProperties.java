package uk.co.ogauthority.pathfinder.model.email.emailproperties.teammanagement;

import java.util.Map;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class TeamRolesUpdatedEmailProperties extends EmailProperties {

  private final String teamName;
  private final String updatedByUserName;
  private final String rolesCsv;
  private final String serviceLoginUrl;

  public TeamRolesUpdatedEmailProperties(String recipientIdentifier,
                                         String teamName,
                                         String updatedByUserName,
                                         String rolesCsv,
                                         String serviceLoginUrl) {
    super(NotifyTemplate.TEAM_ROLES_UPDATED, recipientIdentifier);
    this.teamName = teamName;
    this.updatedByUserName = updatedByUserName;
    this.rolesCsv = rolesCsv;
    this.serviceLoginUrl = serviceLoginUrl;
  }

  @Override
  public Map<String, String> getEmailPersonalisation() {
    Map<String, String> emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("TEAM_NAME", teamName);
    emailPersonalisation.put("UPDATED_BY_USER_NAME", updatedByUserName);
    emailPersonalisation.put("ROLES_CSV", rolesCsv);
    emailPersonalisation.put("SERVICE_LOGIN_URL", serviceLoginUrl);
    return emailPersonalisation;
  }
}
