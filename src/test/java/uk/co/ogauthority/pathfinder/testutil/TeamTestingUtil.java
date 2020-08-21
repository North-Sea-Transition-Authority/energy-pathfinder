package uk.co.ogauthority.pathfinder.testutil;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalRoleDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamMemberDto;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnit;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.model.team.RegulatorRole;
import uk.co.ogauthority.pathfinder.model.team.RegulatorTeam;
import uk.co.ogauthority.pathfinder.model.team.Role;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.model.team.TeamMember;

/**
 * Util class to ease testing of Teams service and associated services which use teams package objects.
 */
public class TeamTestingUtil {

  public static RegulatorTeam getRegulatorTeam() {
    return new RegulatorTeam(100, "REGULATOR_TEAM_NAME", "REGULATOR_TEAM_DESCRIPTION");
  }

  public static OrganisationTeam getOrganisationTeam(PortalOrganisationGroup organisationGroup) {
    return new OrganisationTeam(200, "ORG_TEAM_NAME", "ORG_TEAM_DESCRIPTION", organisationGroup);
  }

  public static OrganisationTeam getOrganisationTeam(int id, String organisationTeamName) {
    var organisationGroup = generateOrganisationGroup(
        1,
        "ORG_GRP_NAME",
        "ORG_GRP_SHORT_NAME"
    );
    return new OrganisationTeam(id, organisationTeamName, "ORG_TEAM_DESCRIPTION", organisationGroup);
  }

  public static Role getTeamAdminRole() {
    return new Role(
        Role.TEAM_ADMINISTRATOR_ROLE_NAME,
        "ADMIN ROLE_TITLE",
        "ADMIN_ROLE_DESCRIPTION",
        10
    );
  }

  public static Role generateRole(String roleName, int displaySequence) {
    return new Role(
        roleName,
        roleName,
        roleName,
        displaySequence
    );
  }

  public static PortalTeamDto portalTeamDtoFrom(RegulatorTeam team) {
    return new PortalTeamDto(
        team.getId(),
        team.getName(),
        team.getDescription(),
        team.getType().getPortalTeamType(),
        null
    );
  }

  public static PortalTeamDto portalTeamDtoFrom(OrganisationTeam team) {
    return new PortalTeamDto(
        team.getId(),
        team.getName(),
        team.getDescription(),
        team.getType().getPortalTeamType(),
        team.getPortalOrganisationGroup().getUrefValue()
    );
  }

  public static PortalTeamMemberDto createPortalTeamMember(Person person, Set<PortalRoleDto> roles) {
    return new PortalTeamMemberDto(person.getId(), roles);
  }

  public static PortalTeamMemberDto createPortalTeamMember(Person person, Team team) {
    Set<PortalRoleDto> personRoles = new HashSet<>();
    personRoles.add(
        getTeamAdminRoleDto(team)
    );

    return createPortalTeamMember(person, personRoles);
  }

  public static PortalRoleDto getTeamAdminRoleDto(Team team) {
    return getTeamAdminRoleDto(team.getId());
  }

  public static PortalRoleDto getTeamAdminRoleDto(int resId) {
    return new PortalRoleDto(resId, Role.TEAM_ADMINISTRATOR_ROLE_NAME, "Team admin", "Team Admin Desc", 10);
  }

  public static PortalOrganisationUnit createOrgUnit() {
    var portalOrganisationGroup = generateOrganisationGroup(100, "ORGANISATION_GROUP", "ORG_GRP");
    return generateOrganisationUnit(1000, "ORGANISATION_UNIT", portalOrganisationGroup);
  }

  private static PortalOrganisationUnit generateOrganisationUnit(int ouId, String name,
                                                                 PortalOrganisationGroup portalOrganisationGroup) {
    PortalOrganisationUnit organisationUnit = new PortalOrganisationUnit();
    try {
      FieldUtils.writeField(organisationUnit, "ouId", ouId, true);
      FieldUtils.writeField(organisationUnit, "name", name, true);
      FieldUtils.writeField(organisationUnit, "portalOrganisationGroup", portalOrganisationGroup, true);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return organisationUnit;
  }

  public static PortalOrganisationGroup generateOrganisationGroup(int orgGrpId, String name, String shortName) {

    PortalOrganisationGroup portalOrganisationGroup = new PortalOrganisationGroup();
    try {
      FieldUtils.writeField(portalOrganisationGroup, "orgGrpId", orgGrpId, true);
      FieldUtils.writeField(portalOrganisationGroup, "name", name, true);
      FieldUtils.writeField(portalOrganisationGroup, "shortName", shortName, true);
      FieldUtils.writeField(portalOrganisationGroup, "urefValue", orgGrpId + "++REGORGGRP", true);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return portalOrganisationGroup;
  }

  public static TeamMember createRegulatorTeamMember(Team regulatorTeam, Person person, Set<RegulatorRole> regulatorRoles) {

    var roles = regulatorRoles.stream()
        .map(role -> new Role(role.getPortalTeamRoleName(), "title", "desc", 10))
        .collect(Collectors.toSet());

    return new TeamMember(regulatorTeam, person, roles);

  }

}

