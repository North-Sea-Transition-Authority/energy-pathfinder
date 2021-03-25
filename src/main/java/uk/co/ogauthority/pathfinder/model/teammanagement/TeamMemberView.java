package uk.co.ogauthority.pathfinder.model.teammanagement;

import java.util.Set;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserAction;

/**
 * View of single team member for team management screen.
 */
public class TeamMemberView {

  private final String forename;
  private final String surname;
  private final String emailAddress;
  private final String telephoneNo;

  private final UserAction editAction;
  private final UserAction removeAction;
  private final Set<TeamRoleView> roleViews;

  public TeamMemberView(Person person, UserAction editAction, UserAction removeAction, Set<TeamRoleView> teamRoleViews) {
    this.forename = person.getForename();
    this.surname = person.getSurname();
    this.emailAddress = person.getEmailAddress();
    this.telephoneNo = person.getTelephoneNo();
    this.roleViews = teamRoleViews;
    this.editAction = editAction;
    this.removeAction = removeAction;
  }

  public UserAction getEditAction() {
    return editAction;
  }

  public UserAction getRemoveAction() {
    return removeAction;
  }

  public String getForename() {
    return forename;
  }

  public String getSurname() {
    return surname;
  }

  public String getFullName() {
    return this.forename + " " + this.surname;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getTelephoneNo() {
    return telephoneNo;
  }

  public Set<TeamRoleView> getRoleViews() {
    return roleViews;
  }
}
