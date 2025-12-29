package uk.co.ogauthority.pathfinder.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.team.Team;

@Service
@Profile("disable-team-management-emails")
class NoOpTeamManagementService implements TeamManagementEmailService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NoOpTeamManagementService.class);

  NoOpTeamManagementService() {
    LOGGER.warn("Team management emails are disabled, pending accounts service phase 2 go live");
  }

  @Override
  public void sendAddedToTeamEmail(Team team, Person person, String rolesCsv, WebUserAccount addedByUser) {

  }

  @Override
  public void sendTeamRolesUpdatedEmail(Team team, Person person, String rolesCsv, WebUserAccount updatedByUser) {

  }

  @Override
  public void sendRemovedFromTeamEmail(Team team, Person person, WebUserAccount removedByUser) {

  }
}
