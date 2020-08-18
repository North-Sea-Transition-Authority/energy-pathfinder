package uk.co.ogauthority.pathfinder.controller.rest;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.model.team.OrganisationRole;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@RestController
@RequestMapping("/api/")
public class OrganisationGroupRestController {

  private final TeamService teamService;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public OrganisationGroupRestController(TeamService teamService,
                                         SearchSelectorService searchSelectorService) {
    this.teamService = teamService;
    this.searchSelectorService = searchSelectorService;
  }

  @GetMapping("/user-organisation")
  @ResponseBody
  public RestSearchResult searchFields(@RequestParam("term") String searchTerm,
                                       AuthenticatedUserAccount user) {
    var searchableList = teamService.getOrganisationGroupsByPersonRoleAndNameLike(
        user.getLinkedPerson(),
        EnumSet.allOf(OrganisationRole.class),
        searchTerm
    );

    List<RestSearchItem> results = searchSelectorService.search(searchTerm, searchableList)
        .stream()
        .sorted(Comparator.comparing(RestSearchItem::getText))
        .collect(Collectors.toList());

    return new RestSearchResult(results);
  }
}
