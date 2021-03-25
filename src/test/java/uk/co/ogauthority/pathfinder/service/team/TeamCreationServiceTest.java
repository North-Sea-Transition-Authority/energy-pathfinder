package uk.co.ogauthority.pathfinder.service.team;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.team.PortalTeamManagementController;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.AddOrganisationTeamForm;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.NewTeamForm;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.teammanagement.TeamManagementService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class TeamCreationServiceTest {

  @Mock
  private ValidationService validationService;

  @Mock
  private PortalTeamAccessor portalTeamAccessor;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private TeamManagementService teamManagementService;

  private TeamCreationService teamCreationService;

  private final AuthenticatedUserAccount authenticatedUserAccount = UserTestingUtil.getAuthenticatedUserAccount(
      Set.of(UserPrivilege.PATHFINDER_REG_ORG_MANAGER)
  );

  @Before
  public void setup() {
    teamCreationService = new TeamCreationService(
        validationService,
        portalTeamAccessor,
        portalOrganisationAccessor,
        teamManagementService
    );
  }

  static class NewTeamTestForm implements NewTeamForm {}

  @Test
  public void validate() {

    final var form = new NewTeamTestForm();
    final var bindingResult = new BeanPropertyBindingResult(form, "form");
    final var validationType = ValidationType.FULL;

    teamCreationService.validate(form, bindingResult, validationType);
    verify(validationService, times(1)).validate(form, bindingResult, validationType);
  }

  @Test
  public void getOrCreateOrganisationGroupTeam_whenTeamExists_thenReturnExistingTeam() {

    final var organisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "name", "short name");
    final var organisationTeam = TeamTestingUtil.getOrganisationTeam(organisationGroup);
    final var portalTeamDto = TeamTestingUtil.portalTeamDtoFrom(organisationTeam);

    when(portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroup.getOrgGrpId())).thenReturn(organisationGroup);
    when(portalTeamAccessor.findPortalTeamByOrganisationGroup(organisationGroup)).thenReturn(Optional.of(portalTeamDto));

    final var form = new AddOrganisationTeamForm(organisationGroup.getSelectionId());

    var result = teamCreationService.getOrCreateOrganisationGroupTeam(form, authenticatedUserAccount);

    assertThat(result).isEqualTo(portalTeamDto.getResId());
    verify(portalTeamAccessor, times(0)).createOrganisationGroupTeam(organisationGroup, authenticatedUserAccount);
  }

  @Test
  public void getOrCreateOrganisationGroupTeam_whenTeamDoesntExists_thenCreateNewTeam() {

    final var organisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "name", "short name");

    when(portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroup.getOrgGrpId())).thenReturn(organisationGroup);
    when(portalTeamAccessor.findPortalTeamByOrganisationGroup(organisationGroup)).thenReturn(Optional.empty());

    final var form = new AddOrganisationTeamForm(organisationGroup.getSelectionId());

    var result = teamCreationService.getOrCreateOrganisationGroupTeam(form, authenticatedUserAccount);

    assertThat(result).isNotNull();
    verify(portalTeamAccessor, times(1)).createOrganisationGroupTeam(organisationGroup, authenticatedUserAccount);

  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getOrCreateOrganisationGroupTeam_whenOrganisationGroupDoesntExist_thenException() {

    final var organisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "name", "short name");

    doThrow(new PathfinderEntityNotFoundException("test"))
        .when(portalOrganisationAccessor).getOrganisationGroupOrError(organisationGroup.getOrgGrpId());

    final var form = new AddOrganisationTeamForm(organisationGroup.getSelectionId());

    teamCreationService.getOrCreateOrganisationGroupTeam(form, authenticatedUserAccount);

    verify(portalTeamAccessor, times(0)).createOrganisationGroupTeam(organisationGroup, authenticatedUserAccount);

  }

  @Test
  public void constructAddOrganisationTeamAction_whenOrganisationAccessManager_thenEnabled() {

    final var user = UserTestingUtil.getAuthenticatedUserAccount();
    when(teamManagementService.canManageAnyOrgTeam(user)).thenReturn(true);

    var result = teamCreationService.constructAddOrganisationTeamAction(user);

    checkCommonAddOrganisationTeamActionValues((LinkButton) result);
    assertThat(result.getEnabled()).isTrue();
  }

  @Test
  public void constructAddOrganisationTeamAction_whenNotOrganisationAccessManager_thenDisabled() {

    final var user = UserTestingUtil.getAuthenticatedUserAccount();
    when(teamManagementService.canManageAnyOrgTeam(user)).thenReturn(false);

    var result = teamCreationService.constructAddOrganisationTeamAction(user);

    checkCommonAddOrganisationTeamActionValues((LinkButton) result);
    assertThat(result.getEnabled()).isFalse();
  }

  private void checkCommonAddOrganisationTeamActionValues(LinkButton linkButton) {
    assertThat(linkButton.getPrompt()).isEqualTo(TeamCreationService.CREATE_ORGANISATION_TEAM_ACTION_PROMPT);
    assertThat(linkButton.getType()).isEqualTo(UserActionType.LINK_BUTTON);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(PortalTeamManagementController.class).getNewOrganisationTeam(null))
    );
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.BLUE);
  }

}