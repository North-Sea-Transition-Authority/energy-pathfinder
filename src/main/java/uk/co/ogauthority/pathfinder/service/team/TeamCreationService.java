package uk.co.ogauthority.pathfinder.service.team;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.team.PortalTeamManagementController;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.AddOrganisationTeamForm;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.NewTeamForm;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserAction;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.StartProjectService;
import uk.co.ogauthority.pathfinder.service.teammanagement.TeamManagementService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class TeamCreationService {

  public static final String CREATE_ORGANISATION_TEAM_ACTION_PROMPT = "Create organisation team";

  private final ValidationService validationService;
  private final PortalTeamAccessor portalTeamAccessor;
  private final TeamManagementService teamManagementService;
  private final StartProjectService startProjectService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;

  @Autowired
  public TeamCreationService(ValidationService validationService,
                             PortalTeamAccessor portalTeamAccessor,
                             TeamManagementService teamManagementService,
                             StartProjectService startProjectService,
                             PortalOrganisationAccessor portalOrganisationAccessor) {
    this.validationService = validationService;
    this.portalTeamAccessor = portalTeamAccessor;
    this.teamManagementService = teamManagementService;
    this.startProjectService = startProjectService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
  }

  public BindingResult validate(NewTeamForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    return validationService.validate(form, bindingResult, validationType);
  }

  public Integer getOrCreateOrganisationGroupTeam(AddOrganisationTeamForm form,
                                                  AuthenticatedUserAccount user) {
    var organisationGroup = portalOrganisationAccessor.getOrganisationGroupOrError(
        Integer.parseInt(form.getOrganisationGroup())
    );

    var organisationTeam = getOrganisationGroupTeam(organisationGroup);

    return organisationTeam.map(PortalTeamDto::getResId).orElseGet(
        () -> {
          var teamId = createOrganisationGroupTeam(organisationGroup, user);

          startProjectService.createForwardWorkPlanProject(user, organisationGroup);

          return teamId;
        });
  }

  private Integer createOrganisationGroupTeam(PortalOrganisationGroup organisationGroup,
                                             AuthenticatedUserAccount user) {
    return portalTeamAccessor.createOrganisationGroupTeam(organisationGroup, user);
  }

  private Optional<PortalTeamDto> getOrganisationGroupTeam(PortalOrganisationGroup organisationGroup) {
    return portalTeamAccessor.findPortalTeamByOrganisationGroup(organisationGroup);
  }

  public UserAction constructAddOrganisationTeamAction(AuthenticatedUserAccount userAccount) {
    return new LinkButton(
        CREATE_ORGANISATION_TEAM_ACTION_PROMPT,
        ReverseRouter.route(on(PortalTeamManagementController.class).getNewOrganisationTeam(null)),
        teamManagementService.canManageAnyOrgTeam(userAccount),
        ButtonType.BLUE
    );
  }
}
