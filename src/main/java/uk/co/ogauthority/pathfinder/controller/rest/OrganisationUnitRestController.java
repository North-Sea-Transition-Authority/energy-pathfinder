package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@RestController
@RequestMapping("/api/organisation-units")
public class OrganisationUnitRestController {

  private final SearchSelectorService searchSelectorService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final TeamService teamService;

  @Autowired
  public OrganisationUnitRestController(SearchSelectorService searchSelectorService,
                                        PortalOrganisationAccessor portalOrganisationAccessor,
                                        TeamService teamService) {
    this.searchSelectorService = searchSelectorService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.teamService = teamService;
  }

  @GetMapping("/user-involvement-organisations")
  @ResponseBody
  public RestSearchResult searchUserInvolvementOrganisationUnits(@Nullable @RequestParam("term") String organisationUnitName,
                                                                 AuthenticatedUserAccount userAccount) {

    final var organisationGroups = teamService.getOrganisationGroupsPersonInTeamFor(userAccount.getLinkedPerson());

    // only filter by organisation unit name if a non-blank string has been provided. This allows the list of
    // organisations to be pre-populated even if a user hasn't entered a search term.
    final var matchedOrganisationUnits = (organisationUnitName != null && !organisationUnitName.isBlank())
        ? portalOrganisationAccessor.getActiveOrganisationUnitsByNameAndOrganisationGroupId(
            organisationUnitName,
            organisationGroups
          )
        : portalOrganisationAccessor.getActiveOrganisationUnitsForOrganisationGroupsIn(organisationGroups);

    return new RestSearchResult(
        searchSelectorService.search(
            organisationUnitName,
            matchedOrganisationUnits
        )
    );
  }

  @GetMapping
  @ResponseBody
  public RestSearchResult searchOrganisationUnits(@Nullable @RequestParam("term") String organisationUnitName) {

    final var matchedOrganisationUnits = portalOrganisationAccessor.findActiveOrganisationUnitsWhereNameContains(
        organisationUnitName
    );

    return new RestSearchResult(
        searchSelectorService.search(
            organisationUnitName,
            matchedOrganisationUnits
        )
    );
  }
}
