package uk.co.ogauthority.pathfinder.controller.team;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.AddUserToTeamForm;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.UserRolesForm;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.model.team.TeamType;
import uk.co.ogauthority.pathfinder.model.teammanagement.TeamMemberView;
import uk.co.ogauthority.pathfinder.model.teammanagement.TeamRoleView;
import uk.co.ogauthority.pathfinder.model.teammanagement.TeamView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.FoxUrlService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.teammanagement.AddUserToTeamFormValidator;
import uk.co.ogauthority.pathfinder.service.teammanagement.LastAdministratorException;
import uk.co.ogauthority.pathfinder.service.teammanagement.TeamManagementService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Controller
@RequestMapping("/team-management")
public class PortalTeamManagementController {

  private final TeamManagementService teamManagementService;
  private final AddUserToTeamFormValidator addUserToTeamFormValidator;
  private final FoxUrlService foxUrlService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public PortalTeamManagementController(TeamManagementService teamManagementService,
                                        AddUserToTeamFormValidator addUserToTeamFormValidator,
                                        FoxUrlService foxUrlService,
                                        ControllerHelperService controllerHelperService) {
    this.teamManagementService = teamManagementService;
    this.addUserToTeamFormValidator = addUserToTeamFormValidator;
    this.foxUrlService = foxUrlService;
    this.controllerHelperService = controllerHelperService;
  }

  /**
   * Display all teams the user can manage.
   * If they can only manage a single team they are redirected to its management page.
   */
  @GetMapping("")
  public ModelAndView renderManageableTeams(AuthenticatedUserAccount currentUser, TeamType teamType) {

    var modelAndView = getManageableTeamsModelAndView(teamType);

    List<TeamView> teamViews = teamManagementService.getAllTeamsOfTypeUserCanView(currentUser, teamType)
        .stream()
        .map(team -> new TeamView(team,
            ReverseRouter.route(on(PortalTeamManagementController.class).renderTeamMembers(team.getId(), null)))
        )
        .collect(Collectors.toList());

    if (teamViews.size() > 1) {
      modelAndView.addObject("teamViewList", teamViews);
    } else if (teamViews.size() == 1) {
      // Dont show team list if there's only 1
      return new ModelAndView("redirect:" + teamViews.get(0).getSelectRoute());
    } else {
      throw new AccessDeniedException(String.format(
          "User with wuaId %s cannot manage any teams", currentUser.getWuaId()
      ));
    }
    return modelAndView;
  }

  private ModelAndView getManageableTeamsModelAndView(TeamType teamType) {
    var modelAndView = new ModelAndView("teamManagement/manageableTeams");

    var isManagingOrganisationTeam = (teamType != null && teamType.equals(TeamType.ORGANISATION));

    var pageTitle = isManagingOrganisationTeam ? "Select organisation" : "Select a team";
    modelAndView.addObject("pageTitle", pageTitle);

    var manageActionTitle = isManagingOrganisationTeam ? "Manage organisation" : "Manage team";
    modelAndView.addObject("manageActionTitle", manageActionTitle);

    return modelAndView;
  }

  @GetMapping("/teams/{resId}/member")
  public ModelAndView renderTeamMembers(@PathVariable Integer resId, AuthenticatedUserAccount currentUser) {
    return withViewableTeam(resId, currentUser, (this::getTeamUsersModelAndView));
  }

  private ModelAndView getTeamUsersModelAndView(Team team, AuthenticatedUserAccount user) {
    List<TeamMemberView> teamMemberViews = teamManagementService.getTeamMemberViewsForTeam(team).stream()
        .sorted(Comparator.comparing(TeamMemberView::getForename).thenComparing(TeamMemberView::getSurname))
        .collect(Collectors.toList());

    var teamRoles = teamManagementService.getRolesForTeam(team)
        .stream()
        .sorted(Comparator.comparing(TeamRoleView::getDisplaySequence))
        .collect(Collectors.toMap(TeamRoleView::getTitle, TeamRoleView::getDescription, (x,y) -> y, LinkedHashMap::new));

    return new ModelAndView("teamManagement/teamMembers")
        .addObject("teamId", team.getId())
        .addObject("teamName", team.getName())
        .addObject("teamMemberViews", teamMemberViews)
        .addObject("addUserUrl", ReverseRouter.route(
            on(PortalTeamManagementController.class).renderAddUserToTeam(team.getId(), null, null)
        ))
        .addObject("showBreadcrumbs", false)
        .addObject("userCanManageAccess", teamManagementService.canManageTeam(team, user))
        .addObject("showTopNav", true)
        .addObject("additionalGuidanceText", team.getType().getTeamManagementGuidance())
        .addObject("allRoles", teamRoles);
  }

  @GetMapping("/teams/{resId}/member/new")
  public ModelAndView renderAddUserToTeam(@PathVariable Integer resId,
                                          @ModelAttribute("form") AddUserToTeamForm userForm,
                                          AuthenticatedUserAccount currentUser) {
    return withManageableTeam(resId, currentUser, (this::getAddUserToTeamModelAndView));
  }

  private ModelAndView getAddUserToTeamModelAndView(Team team) {
    return new ModelAndView("teamManagement/addUserToTeam")
        .addObject("groupName", team.getName())
        .addObject("teamId", team.getId())
        .addObject("showTopNav", true)
        .addObject("cancelUrl", ReverseRouter.route(
            on(PortalTeamManagementController.class).renderTeamMembers(team.getId(), null))
        )
        .addObject("portalRegistrationUrl", foxUrlService.getFoxRegistrationUrl());
  }

  @PostMapping("/teams/{resId}/member/new")
  public ModelAndView handleAddUserToTeamSubmit(@PathVariable Integer resId,
                                                @ModelAttribute("form") AddUserToTeamForm userForm,
                                                BindingResult result,
                                                AuthenticatedUserAccount currentUser) {
    return withManageableTeam(resId, currentUser, team -> {
      userForm.setResId(resId);
      addUserToTeamFormValidator.validate(userForm, result);

      return controllerHelperService.checkErrorsAndRedirect(
        result,
        getAddUserToTeamModelAndView(team),
        userForm,
        () -> {
          var person = teamManagementService.getPersonByEmailAddressOrLoginId(userForm.getUserIdentifier())
              .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
                  "No person found with email/loginId %s. This should have been caught by form validation.", userForm.getUserIdentifier())
              ));
          return ReverseRouter.redirect(on(PortalTeamManagementController.class)
              .renderMemberRoles(team.getId(), person.getId().asInt(), null, null));
        });
    });
  }

  @GetMapping("/teams/{resId}/member/{personId}/remove")
  public ModelAndView renderRemoveTeamMember(@PathVariable Integer resId,
                                             @PathVariable Integer personId,
                                             AuthenticatedUserAccount currentUser) {
    return withManageableTeam(resId, currentUser, team -> {
      var person = teamManagementService.getPerson(personId);
      return getRemoveTeamMemberModelAndView(team, person, null);
    });
  }

  @PostMapping("/teams/{resId}/member/{personId}/remove")
  public ModelAndView handleRemoveTeamMemberSubmit(@PathVariable Integer resId,
                                                   @PathVariable Integer personId,
                                                   AuthenticatedUserAccount currentUser) {
    return withManageableTeam(resId, currentUser, team -> {
      var person = teamManagementService.getPerson(personId);
      try {
        teamManagementService.removeTeamMember(person, team, currentUser);
      } catch (LastAdministratorException e) {
        return getRemoveTeamMemberModelAndView(team, person,
            "This person cannot be removed from the team as they are currently the only person in the access manager role.");
      }
      return ReverseRouter.redirect(on(PortalTeamManagementController.class).renderTeamMembers(resId, null));
    });
  }

  private ModelAndView getRemoveTeamMemberModelAndView(Team team, Person person, String error) {
    var teamMemberView = teamManagementService.getTeamMemberViewForTeamAndPerson(team, person)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("personId: %s is not a member of resId: %s", person.getId(), team.getId())
        ));

    return new ModelAndView("teamManagement/removeMember")
        .addObject("cancelUrl", ReverseRouter.route(on(PortalTeamManagementController.class).renderTeamMembers(team.getId(), null)))
        .addObject("showTopNav", true)
        .addObject("teamName", team.getName())
        .addObject("teamMember", teamMemberView)
        .addObject("error", error);
  }

  @GetMapping("/teams/{resId}/member/{personId}/roles")
  public ModelAndView renderMemberRoles(@PathVariable Integer resId,
                                        @PathVariable Integer personId,
                                        @ModelAttribute("form") UserRolesForm form,
                                        AuthenticatedUserAccount currentUser) {
    return withManageableTeam(resId, currentUser, team -> {
      var person = teamManagementService.getPerson(personId);
      if (form.getUserRoles() == null) {
        teamManagementService.populateExistingRoles(person, team, form);
      }
      return getMemberRolesModelAndView(team, person, form);
    });
  }

  @PostMapping("/teams/{resId}/member/{personId}/roles")
  public ModelAndView handleMemberRolesUpdate(@PathVariable Integer resId,
                                              @PathVariable Integer personId,
                                              @Valid @ModelAttribute("form") UserRolesForm form,
                                              BindingResult result,
                                              AuthenticatedUserAccount currentUser) {
    return withManageableTeam(resId, currentUser, team -> {
      var person = teamManagementService.getPerson(personId);

      return controllerHelperService.checkErrorsAndRedirect(
        result,
        getMemberRolesModelAndView(team, person, form),
        form,
        () -> {
          try {
            teamManagementService.updateUserRoles(person, team, form, currentUser);
            return ReverseRouter.redirect(on(PortalTeamManagementController.class).renderTeamMembers(resId, null));
          } catch (LastAdministratorException e) {
            // TODO PAT-160 to ensure this happens in a validator so error summary is populated correctly
            result.rejectValue(
                "userRoles",
                "userRoles.invalid",
                "You cannot remove the last access manager from a team"
            );
            return getMemberRolesModelAndView(team, person, form);
          }
        });
    });
  }

  private ModelAndView getMemberRolesModelAndView(Team team, Person person, UserRolesForm form) {
    List<TeamRoleView> roles = teamManagementService.getRolesForTeam(team);
    return new ModelAndView("teamManagement/memberRoles")
        .addObject("teamId", team.getId())
        .addObject("form", form)
        .addObject("roles", ControllerUtils.asCheckboxMap(roles))
        .addObject("teamName", team.getName())
        .addObject("userName", person.getFullName())
        .addObject("showTopNav", true)
        .addObject("cancelUrl", ReverseRouter.route(
            on(PortalTeamManagementController.class).renderTeamMembers(team.getId(), null))
        );
  }

  private ModelAndView withManageableTeam(Integer resId, AuthenticatedUserAccount currentUser, Function<Team, ModelAndView> function) {
    var team = teamManagementService.getTeamOrError(resId);
    if (teamManagementService.canManageTeam(team, currentUser)) {
      return function.apply(team);
    } else {
      throw new AccessDeniedException(String.format(
          "User with wua id %s attempted to manage resId %s but does not have the correct privileges", currentUser.getWuaId(), team.getId()
      ));
    }
  }

  private ModelAndView withViewableTeam(Integer resId,
                                        AuthenticatedUserAccount currentUser,
                                        BiFunction<Team, AuthenticatedUserAccount, ModelAndView> function) {
    var team = teamManagementService.getTeamOrError(resId);
    if (teamManagementService.canViewTeam(team, currentUser)) {
      return function.apply(team, currentUser);
    } else {
      throw new AccessDeniedException(String.format(
          "User with wua id %s attempted to view resId %s but does not have the correct privileges", currentUser.getWuaId(), team.getId()
      ));
    }
  }
}
