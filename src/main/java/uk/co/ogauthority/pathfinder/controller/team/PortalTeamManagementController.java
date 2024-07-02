package uk.co.ogauthority.pathfinder.controller.team;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.controller.team.annotation.TeamManagementPermissionCheck;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.team.ViewableTeamType;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.AddOrganisationTeamForm;
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
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamCreationService;
import uk.co.ogauthority.pathfinder.service.team.teammanagementcontext.TeamManagementContext;
import uk.co.ogauthority.pathfinder.service.team.teammanagementcontext.TeamManagementPermission;
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
  private final TeamCreationService teamCreationService;

  @Autowired
  public PortalTeamManagementController(TeamManagementService teamManagementService,
                                        AddUserToTeamFormValidator addUserToTeamFormValidator,
                                        FoxUrlService foxUrlService,
                                        ControllerHelperService controllerHelperService,
                                        TeamCreationService teamCreationService) {
    this.teamManagementService = teamManagementService;
    this.addUserToTeamFormValidator = addUserToTeamFormValidator;
    this.foxUrlService = foxUrlService;
    this.controllerHelperService = controllerHelperService;
    this.teamCreationService = teamCreationService;
  }

  /**
   * Display all teams the user can manage.
   * If they can only manage a single team they are redirected to its management page.
   */
  @GetMapping("")
  public ModelAndView renderManageableTeams(AuthenticatedUserAccount currentUser, TeamType teamType) {

    var modelAndView = getManageableTeamsModelAndView(teamType, currentUser);

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

  private ModelAndView getManageableTeamsModelAndView(TeamType teamType, AuthenticatedUserAccount userAccount) {
    var modelAndView = new ModelAndView("teamManagement/manageableTeams");

    var isManagingOrganisationTeam = (teamType != null && teamType.equals(TeamType.ORGANISATION));

    var pageTitle = isManagingOrganisationTeam ? "Select organisation" : "Select a team";
    modelAndView.addObject("pageTitle", pageTitle);

    var manageActionTitle = isManagingOrganisationTeam ? "Manage organisation" : "Manage team";
    modelAndView.addObject("manageActionTitle", manageActionTitle);

    modelAndView.addObject("addNewOrganisationTeamAction",
        teamCreationService.constructAddOrganisationTeamAction(userAccount)
    );

    return modelAndView;
  }

  @GetMapping("/teams/{resId}/member")
  @TeamManagementPermissionCheck(permissions = TeamManagementPermission.VIEW)
  public ModelAndView renderTeamMembers(@PathVariable Integer resId,
                                        TeamManagementContext teamManagementContext) {
    return getTeamUsersModelAndView(teamManagementContext);
  }

  private ModelAndView getTeamUsersModelAndView(TeamManagementContext teamManagementContext) {

    var team = teamManagementContext.getTeam();

    List<TeamMemberView> teamMemberViews = teamManagementService.getTeamMemberViewsForTeam(
        team,
        teamManagementContext.getUserAccount()
    )
        .stream()
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
        .addObject("addUserAction", teamManagementService.constructAddMemberAction(
            team,
            teamManagementContext.getUserAccount()
        ))
        .addObject("showBreadcrumbs", false)
        .addObject("showTeamMemberActions", teamManagementContext.canManageTeam())
        .addObject("showTopNav", true)
        .addObject("additionalGuidanceText", team.getType().getTeamManagementGuidance())
        .addObject("allRoles", teamRoles);
  }

  @GetMapping("/teams/{resId}/member/new")
  @TeamManagementPermissionCheck(permissions = TeamManagementPermission.MANAGE)
  public ModelAndView renderAddUserToTeam(@PathVariable Integer resId,
                                          @ModelAttribute("form") AddUserToTeamForm userForm,
                                          TeamManagementContext teamManagementContext) {
    return getAddUserToTeamModelAndView(teamManagementContext.getTeam());
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
  @TeamManagementPermissionCheck(permissions = TeamManagementPermission.MANAGE)
  public ModelAndView handleAddUserToTeamSubmit(@PathVariable Integer resId,
                                                @ModelAttribute("form") AddUserToTeamForm userForm,
                                                BindingResult result,
                                                TeamManagementContext teamManagementContext) {
    var team = teamManagementContext.getTeam();
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
      }
    );
  }

  @GetMapping("/teams/{resId}/member/{personId}/remove")
  @TeamManagementPermissionCheck(permissions = TeamManagementPermission.MANAGE)
  public ModelAndView renderRemoveTeamMember(@PathVariable Integer resId,
                                             @PathVariable Integer personId,
                                             TeamManagementContext teamManagementContext,
                                             RedirectAttributes redirectAttributes) {
    var person = teamManagementService.getPerson(personId);
    return getRemoveTeamMemberModelAndView(teamManagementContext, person, null, redirectAttributes);
  }

  @PostMapping("/teams/{resId}/member/{personId}/remove")
  @TeamManagementPermissionCheck(permissions = TeamManagementPermission.MANAGE)
  public ModelAndView handleRemoveTeamMemberSubmit(@PathVariable Integer resId,
                                                   @PathVariable Integer personId,
                                                   TeamManagementContext teamManagementContext) {
    var team = teamManagementContext.getTeam();
    var person = teamManagementService.getPerson(personId);
    try {
      teamManagementService.removeTeamMember(person, team, teamManagementContext.getUserAccount());
    } catch (LastAdministratorException e) {
      return getRemoveTeamMemberModelAndView(
          teamManagementContext,
          person,
          "This person cannot be removed from the team as they are currently the only person in the access manager role.",
          null);
    }
    return ReverseRouter.redirect(on(PortalTeamManagementController.class).renderTeamMembers(resId, null));
  }

  private ModelAndView getRemoveTeamMemberModelAndView(TeamManagementContext teamManagementContext, Person person, String error,
                                                       RedirectAttributes redirectAttributes) {

    final var team = teamManagementContext.getTeam();

    var teamMemberView = teamManagementService.getTeamMemberViewForTeamAndPerson(
        team,
        person,
        teamManagementContext.getUserAccount()
    );

    if (teamMemberView.isPresent()) {
      return new ModelAndView("teamManagement/removeMember")
          .addObject("cancelUrl", ReverseRouter.route(on(PortalTeamManagementController.class).renderTeamMembers(team.getId(), null)))
          .addObject("showTopNav", true)
          .addObject("teamName", team.getName())
          .addObject("teamMember", teamMemberView.get())
          .addObject("error", error);
    } else {
      redirectAttributes.addFlashAttribute(
          "bannerMessage",
          String.format("%s is no longer a member of %s", person.getFullName(), teamManagementContext.getTeam().getName())
      );
      return ReverseRouter.redirect(on(PortalTeamManagementController.class).renderTeamMembers(team.getId(), null));
    }
  }

  @GetMapping("/teams/{resId}/member/{personId}/roles")
  @TeamManagementPermissionCheck(permissions = TeamManagementPermission.MANAGE)
  public ModelAndView renderMemberRoles(@PathVariable Integer resId,
                                        @PathVariable Integer personId,
                                        @ModelAttribute("form") UserRolesForm form,
                                        TeamManagementContext teamManagementContext) {
    var person = teamManagementService.getPerson(personId);
    final var team = teamManagementContext.getTeam();

    if (form.getUserRoles() == null) {
      teamManagementService.populateExistingRoles(person, team, form, teamManagementContext.getUserAccount());
    }
    return getMemberRolesModelAndView(team, person, form);
  }

  @PostMapping("/teams/{resId}/member/{personId}/roles")
  @TeamManagementPermissionCheck(permissions = TeamManagementPermission.MANAGE)
  public ModelAndView handleMemberRolesUpdate(@PathVariable Integer resId,
                                              @PathVariable Integer personId,
                                              @Valid @ModelAttribute("form") UserRolesForm form,
                                              BindingResult result,
                                              TeamManagementContext teamManagementContext) {

    var team = teamManagementContext.getTeam();
    var person = teamManagementService.getPerson(personId);

    return controllerHelperService.checkErrorsAndRedirect(
      result,
      getMemberRolesModelAndView(team, person, form),
      form,
      () -> {
        try {
          teamManagementService.updateUserRoles(person, team, form, teamManagementContext.getUserAccount());
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
      }
    );
  }

  @GetMapping("/create-organisation-team")
  @TeamManagementPermissionCheck(permissions = TeamManagementPermission.CREATE)
  public ModelAndView getNewOrganisationTeam(TeamManagementContext teamManagementContext) {
    return getNewOrganisationTeamModelAndView(new AddOrganisationTeamForm());
  }

  @PostMapping("/create-organisation-team")
  @TeamManagementPermissionCheck(permissions = TeamManagementPermission.CREATE)
  public ModelAndView createNewOrganisationTeam(@Valid @ModelAttribute("form") AddOrganisationTeamForm form,
                                                BindingResult bindingResult,
                                                ValidationType validationType,
                                                TeamManagementContext teamManagementContext) {
    bindingResult = teamCreationService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getNewOrganisationTeamModelAndView(form),
        form,
        () -> {
          var resourceId = teamCreationService.getOrCreateOrganisationGroupTeam(form, teamManagementContext.getUserAccount());
          return ReverseRouter.redirect(on(PortalTeamManagementController.class).renderTeamMembers(resourceId, null));
        }
    );
  }

  private ModelAndView getNewOrganisationTeamModelAndView(AddOrganisationTeamForm form) {
    return new ModelAndView("teamManagement/addNewOrganisationTeam")
        .addObject("form", form)
        .addObject("primaryButtonText", ValidationType.FULL.getButtonText())
        .addObject("secondaryLinkText", "Cancel")
        .addObject("teamTypeDisplayName", TeamType.ORGANISATION.name().toLowerCase())
        .addObject("organisationRestUrl",
            SearchSelectorService.route(on(OrganisationGroupRestController.class).searchOrganisations(null))
        )
        .addObject("linkSecondaryActionUrl", ViewableTeamType.ORGANISATION_TEAMS.getLinkUrl());
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
}
