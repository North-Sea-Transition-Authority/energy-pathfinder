package uk.co.ogauthority.pathfinder.service.team;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.team.PortalTeamManagementController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.enums.team.ManageableTeamType;
import uk.co.ogauthority.pathfinder.model.team.RegulatorRole;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@Service
public class ManageTeamService {

  private final TeamService teamService;

  @Autowired
  public ManageTeamService(TeamService teamService) {

    this.teamService = teamService;
  }

  public Map<ManageableTeamType, String> getManageTeamTypesAndUrlsForUser(WebUserAccount user) {

    var teamTypeUrls = new HashMap<ManageableTeamType, String>();

    Set<RegulatorRole> userRegRoles = teamService
        .getMembershipOfPersonInTeam(teamService.getRegulatorTeam(), user.getLinkedPerson())
        .map(member -> member.getRoleSet().stream()
            .map(role -> RegulatorRole.getValueByPortalTeamRoleName(role.getName()))
            .collect(Collectors.toSet()))
        .orElse(Set.of());

    if (userRegRoles.contains(RegulatorRole.ORGANISATION_MANAGER)) {
      teamTypeUrls.put(ManageableTeamType.ORGANISATION_TEAMS, ManageableTeamType.ORGANISATION_TEAMS.getLinkUrl());
    }

    if (userRegRoles.contains(RegulatorRole.TEAM_ADMINISTRATOR)) {

      teamTypeUrls.put(ManageableTeamType.REGULATOR_TEAM,
          ReverseRouter.route(on(PortalTeamManagementController.class)
              .renderTeamMembers(teamService.getRegulatorTeam().getId(), null)));

      return sortedMap(teamTypeUrls);

    }

    return sortedMap(teamTypeUrls);

  }

  private Map<ManageableTeamType, String> sortedMap(Map<ManageableTeamType, String> map) {
    return map.entrySet().stream()
        .sorted(Comparator.comparing(entry -> entry.getKey().getDisplayOrder()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
  }

}
