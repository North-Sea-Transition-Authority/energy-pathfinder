package uk.co.ogauthority.pathfinder.energyportal.service.organisation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamPersonMembershipDto;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;

@Service
public class PortalOrganisationGroupPersonMembershipService {

  private final PortalTeamAccessor portalTeamAccessor;
  private final PortalOrganisationAccessor portalOrganisationAccessor;

  @Autowired
  public PortalOrganisationGroupPersonMembershipService(PortalTeamAccessor portalTeamAccessor,
                                                        PortalOrganisationAccessor portalOrganisationAccessor) {
    this.portalTeamAccessor = portalTeamAccessor;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
  }

  public List<OrganisationGroupMembership> getOrganisationGroupMembershipForOrganisationGroupIn(
      List<PortalOrganisationGroup> organisationGroups
  ) {

    var organisationTeams = portalTeamAccessor.findPortalTeamByOrganisationGroupsIn(organisationGroups);

    var organisationTeamResIds = organisationTeams
        .stream()
        .map(PortalTeamDto::getResId)
        .collect(Collectors.toList());

    var peopleInOrganisationTeams = portalTeamAccessor.getPortalTeamPersonMembershipByResourceIdIn(organisationTeamResIds)
        .stream()
        .collect(Collectors.groupingBy(PortalTeamPersonMembershipDto::getResourceId));

    var organisationGroupMembershipList = new ArrayList<OrganisationGroupMembership>();

    organisationTeams.forEach(organisationTeam -> {

      var organisationGroupIdFromTeamScope = extractOrganisationGroupIdFromScope(organisationTeam.getScope().getPrimaryScope());

      var organisationGroup = organisationGroups
          .stream()
          .filter(portalOrganisationGroup ->
              portalOrganisationGroup.getOrgGrpId().equals(organisationGroupIdFromTeamScope)
          )
          .findFirst()
          .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
              "Could not find organisation group associated with portal team with id %d",
              organisationGroupIdFromTeamScope
          )));

      var organisationGroupMembership = convertToOrganisationGroupMembership(organisationGroup, organisationTeam);

      var resourcePeople = peopleInOrganisationTeams.get(organisationGroupMembership.getResourceId())
          .stream()
          .map(PortalTeamPersonMembershipDto::getPerson)
          .collect(Collectors.toList());

      organisationGroupMembership.setTeamMembers(resourcePeople);

      organisationGroupMembershipList.add(organisationGroupMembership);

    });

    return organisationGroupMembershipList;
  }

  public List<OrganisationGroupMembership> getOrganisationGroupMembershipForOrganisationGroupIdsIn(List<Integer> organisationGroupIds) {
    var organisationGroups = portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(organisationGroupIds);
    return getOrganisationGroupMembershipForOrganisationGroupIn(organisationGroups);
  }


  private OrganisationGroupMembership convertToOrganisationGroupMembership(PortalOrganisationGroup portalOrganisationGroup,
                                                                           PortalTeamDto portalTeamDto) {
    var organisationGroupMembership = new OrganisationGroupMembership();
    organisationGroupMembership.setOrganisationGroup(portalOrganisationGroup);
    organisationGroupMembership.setResourceId(portalTeamDto.getResId());
    organisationGroupMembership.setTeamMembers(Collections.emptyList());
    return organisationGroupMembership;
  }

  private int extractOrganisationGroupIdFromScope(String teamPrimaryScope) {
    var organisationGroupScopeSuffix = PortalOrganisationGroup.UREF_TYPE;
    return Integer.parseInt(teamPrimaryScope.replace(organisationGroupScopeSuffix, ""));
  }
}