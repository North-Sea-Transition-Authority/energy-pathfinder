package uk.co.ogauthority.pathfinder.service.email;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.team.Team;

public interface TeamManagementEmailService {

  void sendAddedToTeamEmail(Team team, Person person, String rolesCsv, WebUserAccount addedByUser);

  void sendTeamRolesUpdatedEmail(Team team, Person person, String rolesCsv, WebUserAccount updatedByUser);

  void sendRemovedFromTeamEmail(Team team, Person person, WebUserAccount removedByUser);

}
