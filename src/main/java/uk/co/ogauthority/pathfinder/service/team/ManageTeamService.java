package uk.co.ogauthority.pathfinder.service.team;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.team.PortalTeamManagementController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.enums.team.ViewableTeamType;
import uk.co.ogauthority.pathfinder.model.team.RegulatorRole;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@Service
public class ManageTeamService {

  private final TeamService teamService;

  @Autowired
  public ManageTeamService(TeamService teamService) {
    this.teamService = teamService;
  }

  public Map<ViewableTeamType, String> getViewableTeamTypesAndUrlsForUser(WebUserAccount user) {
    var isUserInRegulatorTeam = teamService.isPersonMemberOfRegulatorTeam(user.getLinkedPerson());
    return
        (isUserInRegulatorTeam ? getViewableTeamTypesAndUrlsForRegulatorUser(user) : getViewableTeamTypesAndUrlsForOrganisationUser(user));
  }

  /**
   * Get the viewable regulator team types for the given user.
   * @param user The authenticated user
   * @return The team types user has access to view
   */
  private Map<ViewableTeamType, String> getViewableTeamTypesAndUrlsForRegulatorUser(WebUserAccount user) {

    EnumMap<ViewableTeamType, String> teamTypeUrls = new EnumMap<>(ViewableTeamType.class);

    Set<RegulatorRole> userRegRoles = teamService
        .getMembershipOfPersonInTeam(teamService.getRegulatorTeam(), user.getLinkedPerson())
        .map(member -> member.getRoleSet().stream()
            .map(role -> RegulatorRole.getValueByPortalTeamRoleName(role.getName()))
            .collect(Collectors.toSet()))
        .orElse(Set.of());

    if (!userRegRoles.isEmpty()) {

      // If you are in the regulator team you can access it
      teamTypeUrls.put(ViewableTeamType.REGULATOR_TEAM,
          ReverseRouter.route(on(PortalTeamManagementController.class)
              .renderTeamMembers(teamService.getRegulatorTeam().getId(), null)));

      // if you are the organisation access manager then get all organisation teams
      if (userRegRoles.contains(RegulatorRole.ORGANISATION_MANAGER)) {
        var organisationEntry = getOrganisationTeamEntry();
        teamTypeUrls.put(organisationEntry.getKey(), organisationEntry.getValue());
      }

    }

    return sortedMap(teamTypeUrls);

  }

  /**
   * Get the viewable organisation team type for the given user.
   * @param user The authenticated user
   * @return The organisation team type that the user has access to view
   */
  private Map<ViewableTeamType, String> getViewableTeamTypesAndUrlsForOrganisationUser(WebUserAccount user) {

    EnumMap<ViewableTeamType, String> teamTypeUrls = new EnumMap<>(ViewableTeamType.class);

    var organisationTeams = teamService.getOrganisationTeamsPersonIsMemberOf(
        user.getLinkedPerson()
    );

    if (!organisationTeams.isEmpty()) {
      var organisationEntry = getOrganisationTeamEntry();
      teamTypeUrls.put(organisationEntry.getKey(), organisationEntry.getValue());
    }

    return sortedMap(teamTypeUrls);
  }

  public boolean isPersonMemberOfRegulatorTeam(AuthenticatedUserAccount user) {
    return teamService.isPersonMemberOfRegulatorTeam(user.getLinkedPerson());
  }

  private Map<ViewableTeamType, String> sortedMap(Map<ViewableTeamType, String> map) {
    return map.entrySet().stream()
        .sorted(Comparator.comparing(entry -> entry.getKey().getDisplayOrder()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
  }

  private Map.Entry<ViewableTeamType, String> getOrganisationTeamEntry() {
    return Map.entry(ViewableTeamType.ORGANISATION_TEAMS, ViewableTeamType.ORGANISATION_TEAMS.getLinkUrl());
  }

}
