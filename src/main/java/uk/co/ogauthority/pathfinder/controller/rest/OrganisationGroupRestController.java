package uk.co.ogauthority.pathfinder.controller.rest;

import java.util.EnumSet;
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
import uk.co.ogauthority.pathfinder.model.team.OrganisationRole;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@RestController
@RequestMapping("/api/")
public class OrganisationGroupRestController {

  private final TeamService teamService;
  private final SearchSelectorService searchSelectorService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;

  @Autowired
  public OrganisationGroupRestController(TeamService teamService,
                                         SearchSelectorService searchSelectorService,
                                         PortalOrganisationAccessor portalOrganisationAccessor) {
    this.teamService = teamService;
    this.searchSelectorService = searchSelectorService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
  }

  @GetMapping("/user-organisation")
  @ResponseBody
  public RestSearchResult searchUserOrganisations(@Nullable @RequestParam("term") String searchTerm,
                                                  AuthenticatedUserAccount user) {
    var searchableList = teamService.getOrganisationGroupsByPersonRoleAndNameLike(
        user.getLinkedPerson(),
        EnumSet.allOf(OrganisationRole.class),
        searchTerm
    );

    return new RestSearchResult(searchSelectorService.search(searchTerm, searchableList));
  }

  @GetMapping("/organisations")
  @ResponseBody
  public RestSearchResult searchOrganisations(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(
        searchSelectorService.search(
            searchTerm,
            portalOrganisationAccessor.findOrganisationGroupsWhereNameContains(searchTerm)
        )
    );
  }
}
