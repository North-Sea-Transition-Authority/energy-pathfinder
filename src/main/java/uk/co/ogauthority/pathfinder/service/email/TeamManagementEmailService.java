package uk.co.ogauthority.pathfinder.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.teammanagement.AddedToTeamEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.teammanagement.RemovedFromTeamEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.teammanagement.TeamRolesUpdatedEmailProperties;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.model.team.TeamType;

@Service
public class TeamManagementEmailService {

  private final EmailService emailService;
  private final EmailLinkService emailLinkService;
  private final String serviceName;

  @Autowired
  public TeamManagementEmailService(EmailService emailService,
                                    EmailLinkService emailLinkService,
                                    @Value("${service.name}") String serviceName) {
    this.emailService = emailService;
    this.emailLinkService = emailLinkService;
    this.serviceName = serviceName;
  }

  public void sendAddedToTeamEmail(Team team, Person person, String rolesCsv, WebUserAccount addedByUser) {
    var emailProperties = new AddedToTeamEmailProperties(
        person.getForename(),
        formatTeamName(team),
        addedByUser.getFullName(),
        rolesCsv,
        emailLinkService.getWorkAreaUrl()
    );
    emailService.sendEmail(emailProperties, person.getEmailAddress());
  }

  public void sendTeamRolesUpdatedEmail(Team team, Person person, String rolesCsv, WebUserAccount updatedByUser) {
    var emailProperties = new TeamRolesUpdatedEmailProperties(
        person.getForename(),
        formatTeamName(team),
        updatedByUser.getFullName(),
        rolesCsv,
        emailLinkService.getWorkAreaUrl()
    );
    emailService.sendEmail(emailProperties, person.getEmailAddress());
  }

  public void sendRemovedFromTeamEmail(Team team, Person person, WebUserAccount removedByUser) {
    var emailProperties = new RemovedFromTeamEmailProperties(
        person.getForename(),
        formatTeamName(team),
        removedByUser.getFullName()
    );
    emailService.sendEmail(emailProperties, person.getEmailAddress());
  }

  private String formatTeamName(Team team) {
    return team.getType().equals(TeamType.ORGANISATION)
        ? String.format("%s %s team", team.getName(), serviceName)
        : team.getName();
  }
}
