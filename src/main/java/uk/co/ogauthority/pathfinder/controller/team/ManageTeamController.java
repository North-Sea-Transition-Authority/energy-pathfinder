package uk.co.ogauthority.pathfinder.controller.team;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.enums.team.ViewableTeamType;
import uk.co.ogauthority.pathfinder.service.navigation.TopNavigationService;
import uk.co.ogauthority.pathfinder.service.team.ManageTeamService;

@Controller
public class ManageTeamController {

  private final ManageTeamService manageTeamService;

  @Autowired
  public ManageTeamController(ManageTeamService manageTeamService) {
    this.manageTeamService = manageTeamService;
  }

  @GetMapping("/manage-teams")
  public ModelAndView renderTeamTypes(AuthenticatedUserAccount user) {

    var teamTypeUrlMap = manageTeamService.getViewableTeamTypesAndUrlsForUser(user);

    if (teamTypeUrlMap.size() == 1) {
      return new ModelAndView("redirect:" + teamTypeUrlMap.entrySet().iterator().next().getValue());
    }

    if (teamTypeUrlMap.isEmpty()) {
      throw new AccessDeniedException(String.format("User with WUA ID [%s] can't access any team types",
          user.getWuaId()));
    }

    return getRenderTeamTypesModelAndView(user, teamTypeUrlMap);

  }

  private ModelAndView getRenderTeamTypesModelAndView(AuthenticatedUserAccount user, Map<ViewableTeamType, String> teamTypeUrlMap) {
    var isRegulatorUser = manageTeamService.isPersonMemberOfRegulatorTeam(user);
    var pageTitle = (isRegulatorUser ? TopNavigationService.MANAGE_TEAM_TITLE : TopNavigationService.ORGANISATION_USERS_TITLE);
    return new ModelAndView("teamManagement/teamTypes")
        .addObject("teamTypes", teamTypeUrlMap)
        .addObject("pageTitle", pageTitle);
  }

}
