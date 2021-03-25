package uk.co.ogauthority.pathfinder.service.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalRoleDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalSystemPrivilegeDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamMemberDto;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.PersonId;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.repository.PersonRepository;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.model.team.RegulatorTeam;
import uk.co.ogauthority.pathfinder.model.team.Role;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.model.team.TeamMember;
import uk.co.ogauthority.pathfinder.model.team.TeamType;

/**
 * Converts output from PortalTeams Service layer into Objects useful for the application.
 */
@Service
class TeamDtoFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(TeamDtoFactory.class);

  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final PersonRepository personRepository;

  @Autowired
  public TeamDtoFactory(PortalOrganisationAccessor portalOrganisationAccessor, PersonRepository personRepository) {
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.personRepository = personRepository;
  }

  /**
   * Given a PortalTeamDto, create the appropriate application team type.
   */
  Team createTeam(PortalTeamDto portalTeamDto) {
    TeamType teamType = TeamType.findByPortalTeamType(portalTeamDto.getType());
    switch (teamType) {
      case REGULATOR:
        return createRegulatorTeam(portalTeamDto);
      case ORGANISATION:
        return createOrganisationTeam(portalTeamDto);
      default:
        throw new TeamFactoryException("portalTeamType not supported by factory. Type: " + portalTeamDto.getType());
    }
  }

  /**
   * Given a portal team, convert to a regulator team if possible.
   */
  RegulatorTeam createRegulatorTeam(PortalTeamDto portalTeam) {
    checkPortalTeamsAllOfExpectedType(Collections.singleton(portalTeam), TeamType.REGULATOR);

    return new RegulatorTeam(
        portalTeam.getResId(),
        portalTeam.getName(),
        portalTeam.getDescription()
    );
  }

  /**
   * Create a OrganisationTeam from a single PortalTeamDto.
   */
  OrganisationTeam createOrganisationTeam(PortalTeamDto portalTeamDto) {
    List<OrganisationTeam> orgTeams = createOrganisationTeamList(Collections.singleton(portalTeamDto));
    if (orgTeams.isEmpty() || orgTeams.get(0) == null) {
      throw new TeamFactoryException("Organisation Team not created! " + portalTeamDto.toString());
    }
    return orgTeams.get(0);
  }

  /**
   * Given a collection of PortalTeamDtos, try to efficiently convert them to a list of OrganisationTeams.
   */
  public List<OrganisationTeam> createOrganisationTeamList(Collection<PortalTeamDto> portalTeams) {

    checkPortalTeamsAllOfExpectedType(portalTeams, TeamType.ORGANISATION);

    // get the organisation urefs from the team scopes
    List<String> organisationTeamPrimaryScopes = portalTeams.stream()
        .map(pt -> pt.getScope().getPrimaryScope())
        .distinct()
        .collect(Collectors.toList());

    // allow easy lookup of portal org grps based on the team scope
    Map<String, PortalOrganisationGroup> teamOrganisationMap = portalOrganisationAccessor.getAllOrganisationGroupsWithUrefIn(
        organisationTeamPrimaryScopes)
        .stream()
        .collect(Collectors.toMap((PortalOrganisationGroup::getUrefValue), (pog -> pog)));

    List<OrganisationTeam> organisationTeamList = new ArrayList<>();
    for (PortalTeamDto portalTeamDto : portalTeams) {

      // we've hit a data problem if we can't find an organisation group for our team.
      try {
        checkPortalOrganisationFoundForTeam(portalTeamDto, teamOrganisationMap);

        organisationTeamList.add(
            new OrganisationTeam(
                portalTeamDto.getResId(),
                teamOrganisationMap.get(portalTeamDto.getScope().getPrimaryScope()).getName(),
                portalTeamDto.getName(),
                teamOrganisationMap.get(portalTeamDto.getScope().getPrimaryScope())
            )
        );
      } catch (TeamFactoryException e) {
        // dont want to brick any calling code if there is some data issue. Log error and do not add the problem team to the result list.
        LOGGER.warn("Failed to convert PortalTeamDto with resId {} to a OrganisationTeam.", portalTeamDto.getResId(), e);
      }

    }

    return organisationTeamList;
  }

  List<TeamMember> createTeamMemberList(Collection<PortalTeamMemberDto> portalTeamMemberDtoList, Team team) {
    List<TeamMember> teamMembers = new ArrayList<>();

    Set<Integer> portalTeamMemberPersonIds = portalTeamMemberDtoList
        .stream()
        .map(PortalTeamMemberDto::getPersonId)
        .map(PersonId::asInt)
        .collect(Collectors.toSet());

    Map<PersonId, Person> teamMemberPeople = personRepository.findAllByIdIn(portalTeamMemberPersonIds)
        .stream()
        .collect(Collectors.toMap(Person::getId, Function.identity()));

    for (PortalTeamMemberDto portalTeamMemberDto : portalTeamMemberDtoList) {
      teamMembers.add(
          createTeamMember(portalTeamMemberDto, teamMemberPeople.get(portalTeamMemberDto.getPersonId()), team));
    }

    return teamMembers;
  }

  TeamMember createTeamMember(PortalTeamMemberDto portalTeamMemberDto, Person person, Team team) {
    Set<Role> roles = portalTeamMemberDto.getRoles()
        .stream()
        .map(this::createRole)
        .collect(Collectors.toSet());

    return new TeamMember(team, person, roles);
  }

  Role createRole(PortalRoleDto portalRoleDto) {
    return new Role(
        portalRoleDto.getName(),
        portalRoleDto.getTitle(),
        portalRoleDto.getDescription(),
        portalRoleDto.getDisplaySequence()
    );
  }

  /**
   * Make sure all portal teams are of expected type before trying to process them.
   */
  private void checkPortalTeamsAllOfExpectedType(Collection<PortalTeamDto> portalTeams,
                                                 TeamType expectedTeamType) {
    if (portalTeams.stream().anyMatch(pt -> !pt.getType().equals(expectedTeamType.getPortalTeamType()))) {
      throw new TeamFactoryException("Not all teams were of the expected team type");
    }
  }

  private void checkPortalOrganisationFoundForTeam(PortalTeamDto portalTeamDto,
                                                   Map<String, PortalOrganisationGroup> teamOrganisationMap) {
    if (!teamOrganisationMap.containsKey(portalTeamDto.getScope().getPrimaryScope())) {
      throw new TeamFactoryException(
          "Expected to find organisation matching team scope but did not! " + portalTeamDto.toString()
      );
    }
  }

  /**
   * Consume collection of PortalSystemPrivilegeDtos (which may contains duplicates through membership of multiple teams and roles)
   * and return a list.
   */
  Set<UserPrivilege> createUserPrivilegeSet(Collection<PortalSystemPrivilegeDto> portalSystemPrivilegeDtos) {

    Set<UserPrivilege> privileges = new HashSet<>();

    for (PortalSystemPrivilegeDto portalSystemPrivilegeDto: portalSystemPrivilegeDtos) {
      try {
        var privilege = UserPrivilege.valueOf(portalSystemPrivilegeDto.getGrantedPrivilege());
        privileges.add(privilege);
      } catch (IllegalArgumentException e) {
        LOGGER.debug(
            "Unknown privilege '{}' found when mapping portal privileges to the UserPrivilege enum. " +
                "This privilege has been ignored.",
            portalSystemPrivilegeDto.getGrantedPrivilege());
      }
    }

    return privileges;

  }

}
