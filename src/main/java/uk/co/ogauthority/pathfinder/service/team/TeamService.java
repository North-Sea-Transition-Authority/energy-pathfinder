package uk.co.ogauthority.pathfinder.service.team;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.team.OrganisationRole;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.model.team.RegulatorRole;
import uk.co.ogauthority.pathfinder.model.team.RegulatorTeam;
import uk.co.ogauthority.pathfinder.model.team.Role;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.model.team.TeamMember;
import uk.co.ogauthority.pathfinder.model.team.TeamType;

@Service
public class TeamService {

  private final PortalTeamAccessor portalTeamAccessor;
  private final TeamDtoFactory teamDtoFactory;

  @Autowired
  public TeamService(PortalTeamAccessor portalTeamAccessor,
                     TeamDtoFactory teamDtoFactory) {
    this.portalTeamAccessor = portalTeamAccessor;
    this.teamDtoFactory = teamDtoFactory;
  }

  /**
   * Return the regulator team.
   */
  public RegulatorTeam getRegulatorTeam() {
    List<PortalTeamDto> teamList = portalTeamAccessor.getPortalTeamsByPortalTeamType(
        TeamType.REGULATOR.getPortalTeamType()
    );
    return createRegulatorTeamOrError(teamList);
  }

  /**
   * Return all organisation teams.
   */
  public List<OrganisationTeam> getAllOrganisationTeams() {
    List<PortalTeamDto> orgTeams = portalTeamAccessor.getPortalTeamsByPortalTeamType(
        TeamType.ORGANISATION.getPortalTeamType()
    );
    return teamDtoFactory.createOrganisationTeamList(orgTeams);
  }

  /**
   * Return the Team with the given resId.
   */
  public Team getTeamByResId(int resId) {
    return portalTeamAccessor.findPortalTeamById(resId)
        .map(teamDtoFactory::createTeam)
        .orElseThrow(() -> new PathfinderEntityNotFoundException("Team not found for resId: " + resId));
  }

  /**
   * Return all members of the given team.
   */
  public List<TeamMember> getTeamMembers(Team team) {
    return teamDtoFactory.createTeamMemberList(portalTeamAccessor.getPortalTeamMembers(team.getId()), team);
  }

  /**
   * For a given person and team get the role membership of that person. If not a team member return empty optional.
   */
  public Optional<TeamMember> getMembershipOfPersonInTeam(Team team, Person person) {
    return portalTeamAccessor.getPersonTeamMembership(person, team.getId())
        .map(ptm -> teamDtoFactory.createTeamMember(ptm, person, team));
  }

  /**
   * Wrap portalTeams API so calling code has easy way to determine person involvement in the regulator team.
   */
  public Optional<RegulatorTeam> getRegulatorTeamIfPersonInRole(Person person, Collection<RegulatorRole> roles) {

    if (roles.isEmpty()) {
      throw new IllegalArgumentException("Cannot check membership when no roles specified");
    }

    List<String> portalRoleNames = roles.stream()
        .map(RegulatorRole::getPortalTeamRoleName)
        .collect(Collectors.toList());

    List<PortalTeamDto> regulatorTeamList = portalTeamAccessor.getTeamsWherePersonMemberOfTeamTypeAndHasRoleMatching(
        person,
        TeamType.REGULATOR.getPortalTeamType(),
        portalRoleNames
    );

    if (regulatorTeamList.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(createRegulatorTeamOrError(regulatorTeamList));

  }

  /**
   * Wrap portalTeams API so calling code has easy way of determining person involvement across all Organisation teams.
   * Returns the Organisation Teams where the person is a member and has any of the provided roles
   */
  public List<OrganisationTeam> getOrganisationTeamListIfPersonInRole(Person person, Collection<OrganisationRole> roles) {
    List<String> portalRoleNames = getPortalRoleNames(roles);

    List<PortalTeamDto> orgTeamList = portalTeamAccessor.getTeamsWherePersonMemberOfTeamTypeAndHasRoleMatching(
        person,
        TeamType.ORGANISATION.getPortalTeamType(),
        portalRoleNames
    );

    return teamDtoFactory.createOrganisationTeamList(orgTeamList);
  }

  public List<PortalOrganisationGroup> getOrganisationGroupsByPersonRoleAndNameLike(Person person,
                                                                                    Collection<OrganisationRole> roles,
                                                                                    String searchTerm) {
    List<String> portalRoleNames = getPortalRoleNames(roles);

    List<PortalTeamDto> orgTeamList = portalTeamAccessor.getTeamsWherePersonMemberOfTeamWithNameLikeAndAndHasRoleMatching(
        person,
        TeamType.ORGANISATION.getPortalTeamType(),
        portalRoleNames,
        searchTerm
    );

    return teamDtoFactory.createOrganisationTeamList(orgTeamList).stream().map(
        OrganisationTeam::getPortalOrganisationGroup).collect(Collectors.toList()
    );
  }

  private List<String> getPortalRoleNames(Collection<OrganisationRole> roles) {
    if (roles.isEmpty()) {
      throw new IllegalArgumentException("Cannot check membership when no roles specified");
    }

    return roles.stream()
        .map(OrganisationRole::getPortalTeamRoleName)
        .collect(Collectors.toList());
  }

  private RegulatorTeam createRegulatorTeamOrError(List<PortalTeamDto> teams) {
    if (teams.size() != 1) {
      throw new RuntimeException("Expected 1 REGULATOR type team but got " + teams.size());
    } else {
      return teamDtoFactory.createRegulatorTeam(teams.get(0));
    }
  }

  /**
   * Remove a given person from a team.
   */
  public void removePersonFromTeam(Team team, Person personToRemove, WebUserAccount actionPerformedBy) {
    portalTeamAccessor.removePersonFromTeam(team.getId(), personToRemove, actionPerformedBy);
  }


  /**
   * Add (or update) the roles a given person in a team has.
   */
  public void addPersonToTeamInRoles(Team team, Person personToAdd, Collection<String> roleNames, WebUserAccount actionPerformedBy) {
    portalTeamAccessor.addPersonToTeamWithRoles(team.getId(), personToAdd, roleNames, actionPerformedBy);
  }

  /**
   * Check if a given Person has some role within a given Team.
   */
  public boolean isPersonMemberOfTeam(Person person, Team team) {
    return portalTeamAccessor.personIsAMemberOfTeam(team.getId(), person);
  }

  /**
   * Get a list of all possible roles members of a given Team can have.
   */
  public List<Role> getAllRolesForTeam(Team team) {
    return portalTeamAccessor.getAllPortalRolesForTeam(team.getId()).stream()
        .map(teamDtoFactory::createRole)
        .collect(Collectors.toList());
  }

  /**
   * Get all organisation teams where user is a member.
   */
  public List<OrganisationTeam> getOrganisationTeamsPersonIsMemberOf(Person person) {
    return getOrganisationTeamListIfPersonInRole(
        person,
        EnumSet.allOf(OrganisationRole.class)
    );

  }

  public List<UserPrivilege> getAllUserPrivilegesForPerson(Person person) {
    // get privileges available to the user through res type role membership
    var portalPrivileges = teamDtoFactory.createUserPrivilegeSet(
        portalTeamAccessor.getAllPortalSystemPrivilegesForPerson(person)
    );

    return List.copyOf(portalPrivileges);
  }

  public boolean isPersonMemberOfRegulatorTeam(Person person) {
    return isPersonMemberOfTeam(person, getRegulatorTeam());
  }

}
