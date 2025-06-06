package uk.co.ogauthority.pathfinder.service.teammanagement;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.team.PortalTeamManagementController;
import uk.co.ogauthority.pathfinder.energyportal.model.WebUserAccountStatus;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.repository.PersonRepository;
import uk.co.ogauthority.pathfinder.energyportal.repository.WebUserAccountRepository;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.UserRolesForm;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.Link;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserAction;
import uk.co.ogauthority.pathfinder.model.team.OrganisationRole;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.model.team.RegulatorRole;
import uk.co.ogauthority.pathfinder.model.team.Role;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.model.team.TeamMember;
import uk.co.ogauthority.pathfinder.model.team.TeamType;
import uk.co.ogauthority.pathfinder.model.teammanagement.TeamMemberView;
import uk.co.ogauthority.pathfinder.model.teammanagement.TeamRoleView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.email.TeamManagementEmailService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Service
public class TeamManagementService {

  public static final String ADD_USER_ACTION_PROMPT = "Add user";
  private static final Logger LOGGER = LoggerFactory.getLogger(TeamManagementService.class);

  private final TeamService teamService;
  private final TeamManagementEmailService teamManagementEmailService;
  private final PersonRepository personRepository;
  private final WebUserAccountRepository webUserAccountRepository;

  public TeamManagementService(TeamService teamService,
                               TeamManagementEmailService teamManagementEmailService,
                               PersonRepository personRepository,
                               WebUserAccountRepository webUserAccountRepository) {
    this.teamService = teamService;
    this.teamManagementEmailService = teamManagementEmailService;
    this.personRepository = personRepository;
    this.webUserAccountRepository = webUserAccountRepository;
  }

  public Team getTeamOrError(Integer resId) {
    return teamService.getTeamByResId(resId);
  }

  public Person getPerson(int personId) {
    return personRepository.findById(personId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException("Person with id " + personId + " not found"));
  }

  /**
   * Convert TeamMember of a Team into TeamUserViews for team management screens.
   */
  public List<TeamMemberView> getTeamMemberViewsForTeam(Team team, AuthenticatedUserAccount userAccount) {
    List<TeamMember> teamMembers = teamService.getTeamMembers(team);

    List<TeamMemberView> users = new ArrayList<>();

    for (TeamMember teamMember : teamMembers) {
      users.add(convertTeamMemberToTeamUserView(teamMember, team, userAccount));
    }

    return users;
  }

  /**
   * Return an optional wrapping a TeamMemberView of a person if they are a member of the provided team.
   */
  public Optional<TeamMemberView> getTeamMemberViewForTeamAndPerson(Team team,
                                                                    Person person,
                                                                    AuthenticatedUserAccount userAccount) {
    return teamService.getMembershipOfPersonInTeam(team, person)
        .map(teamMember -> convertTeamMemberToTeamUserView(teamMember, team, userAccount));

  }

  TeamMemberView convertTeamMemberToTeamUserView(TeamMember teamMember, Team team, AuthenticatedUserAccount userAccount) {
    Person teamMemberPerson = teamMember.getPerson();
    final var canManageTeam = canManageTeam(team, userAccount);

    Set<TeamRoleView> roleViews = teamMember.getRoleSet().stream()
        .map(TeamRoleView::createTeamRoleViewFrom)
        .collect(Collectors.toSet());

    final var teamMemberActionScreenReaderText = String.format("%s user", teamMemberPerson.getFullName());

    String editRoute = ReverseRouter.route(on(PortalTeamManagementController.class).renderMemberRoles(
        team.getId(),
        teamMemberPerson.getId().asInt(),
        null,
        null
    ));

    var editAction = new Link(
        "Edit",
        editRoute,
        canManageTeam,
        teamMemberActionScreenReaderText
    );

    String removeRoute = ReverseRouter.route(on(PortalTeamManagementController.class).renderRemoveTeamMember(
        team.getId(),
        teamMemberPerson.getId().asInt(),
        null,
        null
    ));

    var removeAction = new Link(
        "Remove",
        removeRoute,
        canManageTeam,
        teamMemberActionScreenReaderText
    );

    return new TeamMemberView(
        teamMemberPerson,
        editAction,
        removeAction,
        roleViews
    );
  }

  public List<Team> getAllTeamsUserCanManage(AuthenticatedUserAccount user) {
    List<Team> teamList = new ArrayList<>();

    List<UserPrivilege> userPrivileges = teamService.getAllUserPrivilegesForPerson(user.getLinkedPerson());

    //If the logged in user is the team administrator for the regulator admin team then get the regulator team
    if (canManageRegulatorTeam(userPrivileges)) {
      Team regulatorTeam = teamService.getRegulatorTeam();
      teamList.add(regulatorTeam);
    }

    //If the logged in user is a regulator who can manage all organisation teams then get all organisations
    if (canManageAnyOrgTeam(userPrivileges)) {

      List<? extends Team> allOrgTeamList = teamService.getAllOrganisationTeams();
      allOrgTeamList.sort(Comparator.comparing(Team::getName));
      teamList.addAll(allOrgTeamList);

    } else {

      List<OrganisationTeam> orgTeamsPersonAdminOf = teamService.getOrganisationTeamListIfPersonInRole(
          user.getLinkedPerson(),
          EnumSet.of(OrganisationRole.TEAM_ADMINISTRATOR)
      );

      orgTeamsPersonAdminOf.sort(Comparator.comparing(Team::getName));

      teamList.addAll(orgTeamsPersonAdminOf);

    }
    return teamList;
  }

  public List<Team> getAllTeamsOfTypeUserCanManage(AuthenticatedUserAccount user, TeamType teamType) {
    return getAllTeamsUserCanManage(user)
        .stream()
        .filter(team -> team.getType().equals(teamType) || teamType == null)
        .collect(Collectors.toList());
  }

  /**
   * Retrieve a list of teams that user has access to view.
   * @param user The authenticated user to retrieve the teams for
   * @return A list of teams that user has access to view
   */
  public List<Team> getAllTeamsUserCanView(AuthenticatedUserAccount user) {

    List<Team> teams = new ArrayList<>();
    List<? extends Team> organisationTeams;

    teamService.getRegulatorTeamIfPersonInRole(
        user.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    ).ifPresent(teams::add);

    if (user.getUserPrivileges().contains(UserPrivilege.PATHFINDER_REG_ORG_MANAGER)) {
      organisationTeams = teamService.getAllOrganisationTeams();
    } else {
      organisationTeams = teamService.getOrganisationTeamsPersonIsMemberOf(user.getLinkedPerson());
    }

    teams.addAll(organisationTeams);

    return teams;
  }

  /**
   * Retrieve a list of teams of teamType that user has access to view.
   * @param user the authenticated user
   * @param teamType The type of teams to retrieve
   * @return A list of teams that the user can view
   */
  public List<Team> getAllTeamsOfTypeUserCanView(AuthenticatedUserAccount user, TeamType teamType) {
    return getAllTeamsUserCanView(user)
        .stream()
        .sorted(Comparator.comparing(team -> team.getName().toLowerCase()))
        .filter(team -> team.getType().equals(teamType) || teamType == null)
        .collect(Collectors.toList());
  }

  /**
   * Populate the existing roles a person has for a given team.
   */
  public void populateExistingRoles(Person person,
                                    Team team,
                                    UserRolesForm form,
                                    AuthenticatedUserAccount userAccount) {
    Optional<TeamMemberView> teamMember = getTeamMemberViewForTeamAndPerson(team, person, userAccount);
    if (teamMember.isPresent()) {
      List<TeamRoleView> personRoles = new ArrayList<>(teamMember.get().getRoleViews());
      List<String> personRoleNames = personRoles.stream()
          .sorted(Comparator.comparing(TeamRoleView::getDisplaySequence))
          .map(TeamRoleView::getRoleName)
          .collect(Collectors.toList());
      form.setUserRoles(personRoleNames);

    } else {
      form.setUserRoles(new ArrayList<>());
    }

  }

  /**
   * Update the roles the given Person has in the given Team with those set in the UserRolesForm.
   * If the Person is a new member of the team, this also sends out an email to notify the person of their new roles.
   */
  @Transactional
  public void updateUserRoles(Person person, Team team, UserRolesForm form, WebUserAccount actionPerformedBy) {

    List<Role> selectedRoles = getSelectedRolesForTeam(form, team);
    if (selectedRoles.isEmpty()) {
      throw new RuntimeException("Expected form with at least one selected role");
    }

    boolean isAlreadyTeamMember = teamService.isPersonMemberOfTeam(person, team);
    boolean settingAdminRole = selectedRoles.stream().anyMatch(Role::isTeamAdministratorRole);

    if (isAlreadyTeamMember && isPersonLastTeamAdmin(team, person) && !settingAdminRole) {
      throw new LastAdministratorException("Operation would result in 0 access managers");
    }

    if (isAlreadyTeamMember) {
      // Clear all roles so unselect roles are no longer applied
      teamService.removePersonFromTeam(team, person, actionPerformedBy);
    }

    List<String> roleNames = selectedRoles.stream()
        .map(Role::getName)
        .collect(Collectors.toList());

    teamService.addPersonToTeamInRoles(team, person, roleNames, actionPerformedBy);

    if (!isAlreadyTeamMember) {
      // Only send a notification email if the user was not already in the team
      notifyNewTeamUser(team, person, selectedRoles, actionPerformedBy);
    } else {
      notifyTeamRolesUpdated(team, person, selectedRoles, actionPerformedBy);
    }
  }

  /**
   * Remove the given Person from the given Team, as long as they are not the last administrator for that team.
   */
  public void removeTeamMember(Person personToRemove,
                               Team teamToRemoveFrom,
                               WebUserAccount actionPerformedBy) {

    if (isPersonMemberOfTeam(personToRemove, teamToRemoveFrom)) {
      if (
          isPersonLastTeamAdmin(teamToRemoveFrom, personToRemove)
          && !canLastTeamAdministratorBeRemovedFromTeam(teamToRemoveFrom, actionPerformedBy.getLinkedPerson())
      ) {
        throw new LastAdministratorException(String.format(
            "Person with ID %s cannot remove person with ID %s from %s with ID %d as this would result in 0 access managers",
            actionPerformedBy.getLinkedPerson().getId(),
            personToRemove.getId(),
            teamToRemoveFrom.getType().name(),
            teamToRemoveFrom.getId()
        ));
      } else {
        teamService.removePersonFromTeam(teamToRemoveFrom, personToRemove, actionPerformedBy);
        notifyTeamUserRemoved(teamToRemoveFrom, personToRemove, actionPerformedBy);
      }
    } else {
      throw new RuntimeException(String.format(
          "PersonId %s is not a member of resId %s",
          personToRemove.getId(),
          teamToRemoveFrom.getId())
      );
    }
  }

  private boolean canLastTeamAdministratorBeRemovedFromTeam(Team team,
                                                            Person personPerformingRemoval) {
    return team.getType().equals(TeamType.ORGANISATION)
        && canLastTeamAdministratorBeRemovedFromOrganisationTeam(personPerformingRemoval);
  }

  private boolean canLastTeamAdministratorBeRemovedFromOrganisationTeam(Person personPerformingRemoval) {
    return canManageAnyOrgTeam(personPerformingRemoval);
  }

  private boolean isPersonLastTeamAdmin(Team team, Person person) {
    List<TeamMember> teamAdministrators = getTeamAdministrators(team);

    boolean personIsAdmin = teamAdministrators.stream()
        .anyMatch(tm -> tm.getPerson().equals(person));

    return personIsAdmin && teamAdministrators.size() == 1;
  }

  private List<TeamMember> getTeamAdministrators(Team team) {
    return teamService.getTeamMembers(team).stream()
        .filter(TeamMember::isTeamAdministrator)
        .collect(Collectors.toList());
  }

  public List<Role> getSelectedRolesForTeam(UserRolesForm form, Team team) {
    Map<String, Role> selectableRolesForTeamMappedByName = teamService.getAllRolesForTeam(team).stream()
        .collect(Collectors.toMap((Role::getName), (r -> r)));

    List<Role> selectedRoles = new ArrayList<>();
    for (String roleName : form.getUserRoles()) {
      if (selectableRolesForTeamMappedByName.containsKey(roleName)) {
        selectedRoles.add(selectableRolesForTeamMappedByName.get(roleName));
      } else {
        if (LOGGER.isErrorEnabled()) {
          LOGGER.error(String.format("Form contains roleNames not applicable for team resId: %s", team.getId()));
        }
      }
    }

    return selectedRoles;
  }

  public List<TeamRoleView> getRolesForTeam(Team team) {
    return teamService.getAllRolesForTeam(team).stream()
        .map(TeamRoleView::createTeamRoleViewFrom)
        .sorted(Comparator.comparing(TeamRoleView::getDisplaySequence))
        .collect(Collectors.toList());
  }

  private String getStringTeamRoles(List<Role> selectedRoles) {
    return selectedRoles.stream()
        .map(Role::getTitle)
        .collect(Collectors.joining(", "));
  }

  public void notifyNewTeamUser(Team team, Person person, List<Role> selectedRoles, WebUserAccount addedByUser) {
    teamManagementEmailService.sendAddedToTeamEmail(team, person, getStringTeamRoles(selectedRoles), addedByUser);
  }

  public void notifyTeamRolesUpdated(Team team, Person person, List<Role> selectedRoles, WebUserAccount updatedByUser) {
    teamManagementEmailService.sendTeamRolesUpdatedEmail(team, person, getStringTeamRoles(selectedRoles), updatedByUser);
  }

  public void notifyTeamUserRemoved(Team team, Person person, WebUserAccount removedByUser) {
    teamManagementEmailService.sendRemovedFromTeamEmail(team, person, removedByUser);
  }

  /**
   * Checks if the given User has privileges to manage the given team.
   */
  public boolean canManageTeam(Team team, AuthenticatedUserAccount user) {
    // This does a full reload of privileges which is slow.
    // Could use the ones cached against the AuthenticatedUserAccount if performance is an issue.
    List<UserPrivilege> userPrivileges = teamService.getAllUserPrivilegesForPerson(user.getLinkedPerson());

    if (canManageAnyOrgTeam(userPrivileges) && team.getType().equals(TeamType.ORGANISATION)) {
      // If the logged in user is a regulator with the organisation manage privileges then they can manage any organisation team
      return true;
    } else {
      return teamService.getMembershipOfPersonInTeam(team, user.getLinkedPerson())
          .map(TeamMember::isTeamAdministrator)
          .orElse(false);
    }
  }

  /**
   * Checks if the given User has privileges to view the given team.
   */
  public boolean canViewTeam(Team team, AuthenticatedUserAccount user) {
    List<UserPrivilege> userPrivileges = teamService.getAllUserPrivilegesForPerson(user.getLinkedPerson());

    if (canManageAnyOrgTeam(userPrivileges) && team.getType().equals(TeamType.ORGANISATION)) {
      return true;
    } else {
      return teamService.getMembershipOfPersonInTeam(team, user.getLinkedPerson()).isPresent();
    }
  }

  /**
   * Finds the Person linked to the WebUserAccount with the given email or loginId.
   */
  public Optional<Person> getPersonByEmailAddressOrLoginId(String emailOrLoginId) {

    List<WebUserAccount> webUserAccounts =
        webUserAccountRepository.findAllByEmailAddressIgnoreCaseAndAccountStatusNot(emailOrLoginId,
            WebUserAccountStatus.CANCELLED);

    if (webUserAccounts.size() == 1) {
      return Optional.of(webUserAccounts.get(0).getLinkedPerson());
    }

    webUserAccounts.addAll(
        webUserAccountRepository.findAllByLoginIdIgnoreCaseAndAccountStatusNot(emailOrLoginId, WebUserAccountStatus.CANCELLED));

    if (webUserAccounts.size() == 1) {
      return Optional.of(webUserAccounts.get(0).getLinkedPerson());
    } else {

      Set<Person> distinctPeople = webUserAccounts.stream()
          .map(WebUserAccount::getLinkedPerson)
          .collect(Collectors.toSet());

      if (distinctPeople.size() > 1) {
        throw new RuntimeException(
            String.format("getPersonByEmailAddressOrLoginId returned %d different people with email/loginId '%s'",
                distinctPeople.size(), emailOrLoginId)
        );
      } else if (distinctPeople.isEmpty()) {
        return Optional.empty();
      } else {
        return Optional.of(webUserAccounts.get(0).getLinkedPerson());
      }

    }
  }

  @VisibleForTesting
  boolean canManageRegulatorTeam(List<UserPrivilege> userPrivileges) {
    return userPrivileges.stream()
        .anyMatch(p -> p.equals(UserPrivilege.PATHFINDER_REGULATOR_ADMIN));
  }

  public boolean canManageAnyOrgTeam(List<UserPrivilege> userPrivileges) {
    return userPrivileges.stream()
        .anyMatch(p -> p.equals(UserPrivilege.PATHFINDER_REG_ORG_MANAGER));
  }

  public boolean canManageAnyOrgTeam(AuthenticatedUserAccount userAccount) {
    return canManageAnyOrgTeam(userAccount.getLinkedPerson());
  }

  public boolean canManageAnyOrgTeam(Person person) {
    List<UserPrivilege> userPrivileges = teamService.getAllUserPrivilegesForPerson(person);
    return canManageAnyOrgTeam(userPrivileges);
  }

  public boolean isPersonMemberOfTeam(Person person, Team team) {
    return teamService.isPersonMemberOfTeam(person, team);
  }

  public UserAction constructAddMemberAction(Team team, AuthenticatedUserAccount userAccount) {
    return new LinkButton(
        ADD_USER_ACTION_PROMPT,
        ReverseRouter.route(on(PortalTeamManagementController.class)
            .renderAddUserToTeam(team.getId(), null, null)
        ),
        canManageTeam(team, userAccount),
        ButtonType.BLUE
    );
  }
}

