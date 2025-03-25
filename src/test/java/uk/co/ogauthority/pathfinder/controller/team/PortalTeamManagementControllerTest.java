package uk.co.ogauthority.pathfinder.controller.team;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.TeamManagementContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.AddOrganisationTeamForm;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.UserRolesForm;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.model.teammanagement.TeamMemberView;
import uk.co.ogauthority.pathfinder.model.teammanagement.TeamRoleView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.team.TeamCreationService;
import uk.co.ogauthority.pathfinder.service.team.teammanagementcontext.TeamManagementContextService;
import uk.co.ogauthority.pathfinder.service.teammanagement.AddUserToTeamFormValidator;
import uk.co.ogauthority.pathfinder.service.teammanagement.LastAdministratorException;
import uk.co.ogauthority.pathfinder.service.teammanagement.TeamManagementService;
import uk.co.ogauthority.pathfinder.testutil.ControllerTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PortalTeamManagementController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = TeamManagementContextService.class))
public class PortalTeamManagementControllerTest extends TeamManagementContextAbstractControllerTest {

  private static final int UNKNOWN_PERSON_ID = 123456789;
  private static final int UNKNOWN_RES_ID = 99999;

  @MockitoBean
  private TeamManagementService teamManagementService;

  @MockitoBean
  protected AddUserToTeamFormValidator addUserToTeamFormValidator;

  @MockitoBean
  private TeamCreationService teamCreationService;

  protected TeamManagementContextService teamManagementContextService;

  private AuthenticatedUserAccount regulatorTeamAdmin;
  private Person regulatorTeamAdminPerson;
  private AuthenticatedUserAccount organisationTeamAdmin;
  private Person organisationTeamAdminPerson;
  private Team regulatorTeam;
  private Team organisationTeam;
  private TeamRoleView teamAdminRole;
  private TeamMemberView regTeamAdminTeamUserView;
  private AuthenticatedUserAccount organisationAccessManager;

  private AuthenticatedUserAccount unauthenticatedUser;

  @Before
  public void teamManagementTestSetup() {

    teamManagementContextService = new TeamManagementContextService(teamManagementService);

    regulatorTeamAdmin = new AuthenticatedUserAccount(new WebUserAccount(1), List.of(
        UserPrivilege.PATHFINDER_REGULATOR_ADMIN,
        UserPrivilege.PATHFINDER_TEAM_VIEWER
    ));
    organisationTeamAdmin = new AuthenticatedUserAccount(new WebUserAccount(2), List.of(
        UserPrivilege.PATHFINDER_ORG_ADMIN,
        UserPrivilege.PATHFINDER_TEAM_VIEWER
    ));

    organisationAccessManager = new AuthenticatedUserAccount(new WebUserAccount(3), List.of(
        UserPrivilege.PATHFINDER_REG_ORG_MANAGER,
        UserPrivilege.PATHFINDER_TEAM_VIEWER
    ));

    regulatorTeamAdminPerson = new Person(1, "Regulator", "Admin", "reg@admin.org", "0");
    organisationTeamAdminPerson = new Person(2, "Organisation", "Admin", "org@admin.org", "0");

    regulatorTeam = TeamTestingUtil.getRegulatorTeam();
    organisationTeam = TeamTestingUtil.getOrganisationTeam(TeamTestingUtil.createOrgUnit().getPortalOrganisationGroup());

    unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    when(teamManagementService.getTeamOrError(regulatorTeam.getId())).thenReturn(regulatorTeam);
    when(teamManagementService.getTeamOrError(UNKNOWN_RES_ID)).thenThrow(new PathfinderEntityNotFoundException(""));

    when(teamManagementService.canManageTeam(regulatorTeam, regulatorTeamAdmin)).thenReturn(true);
    when(teamManagementService.canManageTeam(regulatorTeam, organisationTeamAdmin)).thenReturn(false);

    when(teamManagementService.canViewTeam(regulatorTeam, regulatorTeamAdmin)).thenReturn(true);
    when(teamManagementService.canViewTeam(regulatorTeam, organisationTeamAdmin)).thenReturn(false);

    when(teamManagementService.getPerson(regulatorTeamAdminPerson.getId().asInt())).thenReturn(regulatorTeamAdminPerson);
    when(teamManagementService.getPerson(UNKNOWN_PERSON_ID)).thenThrow(new PathfinderEntityNotFoundException(""));

    teamAdminRole = TeamRoleView.createTeamRoleViewFrom(TeamTestingUtil.getTeamAdminRole());

    var userAction = new LinkButton("prompt", "some/url", true, ButtonType.BLUE);

    regTeamAdminTeamUserView = new TeamMemberView(
        regulatorTeamAdminPerson,
        userAction,
        userAction,
        Set.of(teamAdminRole)
    );

    when(teamManagementService.getTeamMemberViewForTeamAndPerson(
        regulatorTeam,
        regulatorTeamAdminPerson,
        regulatorTeamAdmin
    ))
        .thenReturn(Optional.of(regTeamAdminTeamUserView));

    when(teamCreationService.constructAddOrganisationTeamAction(any())).thenReturn(userAction);
    when(teamManagementService.constructAddMemberAction(any(), any())).thenReturn(userAction);

  }

  @Test
  public void renderManageableTeams_whenMultipleTeamsCanBeViewed() throws Exception {
    when(teamManagementService.getAllTeamsOfTypeUserCanView(regulatorTeamAdmin, null))
        .thenReturn(List.of(regulatorTeam, organisationTeam));

    mockMvc.perform(get("/team-management")
        .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(status().isOk());
  }

  @Test
  public void renderManageableTeams_whenSingleTeamCanBeViewed() throws Exception {
    when(teamManagementService.getAllTeamsOfTypeUserCanView(regulatorTeamAdmin, null))
        .thenReturn(List.of(regulatorTeam));

    mockMvc.perform(get("/team-management")
        .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  public void renderManageableTeams_whenZeroTeamsCanBeViewed() throws Exception {
    when(teamManagementService.getAllTeamsUserCanView(regulatorTeamAdmin))
        .thenReturn(List.of());

    mockMvc.perform(get("/team-management")
        .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void renderTeamMembers_whenTeamFound_andUserCanViewTeam() throws Exception {
    when(teamManagementService.getTeamMemberViewsForTeam(regulatorTeam, regulatorTeamAdmin))
        .thenReturn(List.of(regTeamAdminTeamUserView));

    mockMvc.perform(get("/team-management/teams/{resId}/member", regulatorTeam.getId())
        .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("teamManagement/teamMembers"));
  }

  @Test
  public void renderTeamMembers_whenTeamFound_andUserCannotViewTeam() throws Exception {
    mockMvc.perform(get("/team-management/teams/{resId}/member", regulatorTeam.getId())
        .with(authenticatedUserAndSession(organisationTeamAdmin)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void renderTeamMembers_whenTeamNotFound() throws Exception {
    mockMvc.perform(get("/team-management/teams/{resId}/member", UNKNOWN_RES_ID)
        .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  public void renderMemberRoles_whenTeamFound_andUserCanManage_andFormNotFilled() throws Exception {
    mockMvc.perform(
        get("/team-management/teams/{resId}/member/{personId}/roles",
            regulatorTeam.getId(),
            regulatorTeamAdminPerson.getId().asInt())
            .requestAttr("form", new UserRolesForm())
            .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(status().isOk())
        .andExpect(view().name("teamManagement/memberRoles"));

    verify(teamManagementService, times(1)).populateExistingRoles(
        eq(regulatorTeamAdminPerson),
        eq(regulatorTeam),
        any(),
        any()
    );
  }

  @Test
  public void renderMemberRoles_whenTeamFound_andUserCanManage_andFormFilledWithRoles() throws Exception {
    mockMvc.perform(
        get("/team-management/teams/{resId}/member/{personId}/roles",
            regulatorTeam.getId(),
            regulatorTeamAdminPerson.getId().asInt())
            .param("userRoles", teamAdminRole.getRoleName())
            .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(status().isOk())
        .andExpect(view().name("teamManagement/memberRoles"));

    verify(teamManagementService, times(0)).populateExistingRoles(
        eq(regulatorTeamAdminPerson),
        eq(regulatorTeam),
        any(),
        any()
    );
  }

  @Test
  public void renderMemberRoles_whenPersonNotFoundTest() throws Exception {
    mockMvc.perform(
        get("/team-management/teams/{resId}/member/{personId}/roles",
            regulatorTeam.getId(),
            UNKNOWN_PERSON_ID)
            .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void renderMemberRoles_whenTeamNotFoundTest() throws Exception {
    mockMvc.perform(
        get("/team-management/teams/{resId}/member/{personId}/roles",
            UNKNOWN_RES_ID,
            regulatorTeamAdminPerson.getId().asInt())
            .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void handleMemberRolesUpdate_whenUserCanManageTeam_andNoBindingErrors_andNewMemberAddedToTeam() throws Exception {
    mockMvc.perform(
        post("/team-management/teams/{resId}/member/{personId}/roles"
            , regulatorTeam.getId()
            , regulatorTeamAdminPerson.getId().asInt())
            .with(authenticatedUserAndSession(regulatorTeamAdmin))
            .param("userRoles", teamAdminRole.getRoleName())
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlTemplate("/team-management/teams/{resId}/member", regulatorTeam.getId()));

    verify(teamManagementService, times(1)).updateUserRoles(
        eq(regulatorTeamAdminPerson),
        eq(regulatorTeam),
        any(UserRolesForm.class),
        eq(regulatorTeamAdmin)
    );
  }

  @Test
  public void handleMemberRolesUpdate_whenUserCanManageTeam_andNoBindingErrors_andRemovingLastTeamAdmin() throws Exception {
    doThrow(new LastAdministratorException(""))
        .when(teamManagementService).updateUserRoles(
        eq(regulatorTeamAdminPerson),
        eq(regulatorTeam),
        any(UserRolesForm.class),
        eq(regulatorTeamAdmin)
    );

    mockMvc.perform(
        post("/team-management/teams/{resId}/member/{personId}/roles"
            , regulatorTeam.getId()
            , regulatorTeamAdminPerson.getId().asInt())
            .with(authenticatedUserAndSession(regulatorTeamAdmin))
            .with(csrf())
            .param("userRoles", teamAdminRole.getRoleName()))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasErrors("form"));

    verify(teamManagementService, times(1)).updateUserRoles(
        eq(regulatorTeamAdminPerson),
        eq(regulatorTeam),
        any(UserRolesForm.class),
        eq(regulatorTeamAdmin)
    );
  }

  @Test
  public void handleMemberRolesUpdate_whenUserCanManageTeam_andFromValidationProducesErrors() throws Exception {
    mockMvc.perform(
        post("/team-management/teams/{resId}/member/{personId}/roles"
            , regulatorTeam.getId()
            , regulatorTeamAdminPerson.getId().asInt())
            .with(authenticatedUserAndSession(regulatorTeamAdmin))
            .with(csrf())
            .param("userRoles", ""))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasErrors("form"));

    verify(teamManagementService, times(0)).updateUserRoles(any(), any(), any(), any());
    verify(teamManagementService, times(0)).notifyNewTeamUser(any(), any(), any(), any());
  }

  @Test
  public void handleMemberRolesUpdate_whenUserCannotManageTeam() throws Exception {
    mockMvc.perform(
        post("/team-management/teams/{resId}/member/{personId}/roles"
            , regulatorTeam.getId()
            , regulatorTeamAdminPerson.getId().asInt())
            .with(authenticatedUserAndSession(organisationTeamAdmin))
            .with(csrf())
            .param("userRoles", ""))
        .andExpect(status().isForbidden());

    verify(teamManagementService, times(0)).updateUserRoles(any(), any(), any(), any());
    verify(teamManagementService, times(0)).notifyNewTeamUser(any(), any(), any(), any());
  }


  @Test
  public void renderRemoveTeamMember_whenNotATeamMember() throws Exception {
    when(teamManagementService.getTeamMemberViewForTeamAndPerson(regulatorTeam, regulatorTeamAdminPerson, regulatorTeamAdmin))
        .thenReturn(Optional.empty());

    mockMvc.perform(
        get("/team-management/teams/{resId}/member/{personId}/remove",
            regulatorTeam.getId(),
            regulatorTeamAdminPerson.getId().asInt())
            .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(MockMvcResultMatchers.flash().attribute("bannerMessage",
            String.format("%s is no longer a member of %s", regulatorTeamAdminPerson.getFullName(), regulatorTeam.getName())))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(PortalTeamManagementController.class).renderTeamMembers(regulatorTeam.getId(), null))));
  }

  @Test
  public void renderRemoveTeamMember_whenATeamMember() throws Exception {
    mockMvc.perform(
        get("/team-management/teams/{resId}/member/{personId}/remove",
            regulatorTeam.getId(),
            regulatorTeamAdminPerson.getId().asInt())
            .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(status().isOk());
  }

  @Test
  public void handleRemoveTeamMemberSubmit_userCanManageTeam() throws Exception {

    mockMvc.perform(
        post("/team-management/teams/{resId}/member/{personId}/remove"
            , regulatorTeam.getId()
            , regulatorTeamAdminPerson.getId().asInt())
            .with(authenticatedUserAndSession((regulatorTeamAdmin)))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlTemplate("/team-management/teams/{resId}/member", regulatorTeam.getId()));

    verify(teamManagementService, times(1)).removeTeamMember(regulatorTeamAdminPerson,
        regulatorTeam, regulatorTeamAdmin);
  }

  @Test
  public void handleRemoveTeamMemberSubmit_userCannotManageTeam() throws Exception {

    mockMvc.perform(
        post("/team-management/teams/{resId}/member/{personId}/remove"
            , regulatorTeam.getId()
            , regulatorTeamAdminPerson.getId().asInt())
            .with(authenticatedUserAndSession(organisationTeamAdmin))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(teamManagementService, times(0)).removeTeamMember(any(), any(), any());
  }

  @Test
  public void handleRemoveTeamMemberSubmit_userCanManageTeam_andPersonIsLastAdministratorInTeam() throws Exception {

    doThrow(new LastAdministratorException(""))
        .when(teamManagementService).removeTeamMember(
        regulatorTeamAdminPerson,
        regulatorTeam,
        regulatorTeamAdmin
    );

    mockMvc.perform(
        post("/team-management/teams/{resId}/member/{personId}/remove"
            , regulatorTeam.getId()
            , regulatorTeamAdminPerson.getId().asInt())
            .with(authenticatedUserAndSession((regulatorTeamAdmin)))
            .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  public void renderAddUserToTeam() throws Exception {
    mockMvc.perform(get("/team-management/teams/{resId}/member/new", regulatorTeam.getId())
        .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(status().isOk());
  }

  @Test
  public void renderAddUserToTeam_whenTeamDoesNotExist() throws Exception {
    mockMvc.perform(get("/team-management/teams/{resId}/member/new", UNKNOWN_RES_ID)
        .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void renderAddUserToTeam_whenTeamExists_andUserCannotManageTeam() throws Exception {
    mockMvc.perform(get("/team-management/teams/{resId}/member/new", regulatorTeam.getId())
        .with(authenticatedUserAndSession(organisationTeamAdmin)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void renderAddUserToTeam_whenTeamExists_andUserCanManageTeam() throws Exception {
    mockMvc.perform(get("/team-management/teams/{resId}/member/new", regulatorTeam.getId())
        .with(authenticatedUserAndSession(regulatorTeamAdmin)))
        .andExpect(status().isOk());
  }

  @Test
  public void handleAddUserToTeamSubmit_whenTeamExists_andUserCanManageTeam_andFormIsValid() throws Exception {
    when(teamManagementService.getPersonByEmailAddressOrLoginId(organisationTeamAdminPerson.getEmailAddress()))
        .thenReturn(Optional.of(organisationTeamAdminPerson));

    mockMvc.perform(post("/team-management/teams/{resId}/member/new", regulatorTeam.getId())
        .with(authenticatedUserAndSession(regulatorTeamAdmin))
        .with(csrf())
        .param("userIdentifier", organisationTeamAdminPerson.getEmailAddress()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlTemplate("/team-management/teams/{resId}/member/{personId}/roles",
            regulatorTeam.getId(),
            organisationTeamAdminPerson.getId().asInt()));
  }

  @Test
  public void handleAddUserToTeamSubmit_whenTeamExists_andUserCanManageTeam_andFormIsValid_andFormEmailNotKnown() throws Exception {
    mockMvc.perform(post("/team-management/teams/{resId}/member/new", regulatorTeam.getId())
        .with(authenticatedUserAndSession(regulatorTeamAdmin))
        .with(csrf())
        .param("userIdentifier", "Some.Unknown@email.com"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void handleAddUserToTeamSubmit_whenTeamExists_andUserCanManageTeam_andFormIsInvalid() throws Exception {

    ControllerTestUtil.mockValidatorErrors(addUserToTeamFormValidator, List.of("userIdentifier"));

    mockMvc.perform(
        post("/team-management/teams/{resId}/member/new", regulatorTeam.getId())
            .with(authenticatedUserAndSession(regulatorTeamAdmin))
            .with(csrf())
            .param("userIdentifier", ""))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasErrors("form"));
  }

  @Test
  public void handleAddUserToTeamSubmit_whenTeamExists_andUserCannotManageTeam() throws Exception {
    mockMvc.perform(
        post("/team-management/teams/{resId}/member/new", regulatorTeam.getId())
            .with(authenticatedUserAndSession(organisationTeamAdmin))
            .with(csrf())
            .param("newUser", ""))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getNewOrganisationTeam_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PortalTeamManagementController.class).getNewOrganisationTeam(null)))
        .with(authenticatedUserAndSession(organisationAccessManager)))
        .andExpect(status().isOk());
  }

  @Test
  public void getNewOrganisationTeam_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PortalTeamManagementController.class).getNewOrganisationTeam(null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void createNewOrganisationTeam_whenFullSaveAndInvalidForm_thenNoCreate() throws Exception {

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new AddOrganisationTeamForm();
    form.setOrganisationGroup(null);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(teamCreationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(post(ReverseRouter.route(
        on(PortalTeamManagementController.class).createNewOrganisationTeam(form, bindingResult, ValidationType.FULL, null)))
        .with(authenticatedUserAndSession(organisationAccessManager))
        .with(csrf())
        .params(completeParams))
        .andExpect(status().isOk());

    verify(teamCreationService, times(0)).getOrCreateOrganisationGroupTeam(any(), any());
  }

  @Test
  public void createNewOrganisationTeam_whenAuthenticatedAndFullSaveAndValidForm_thenCreate() throws Exception {

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new AddOrganisationTeamForm("1");

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(teamCreationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(post(ReverseRouter.route(
        on(PortalTeamManagementController.class).createNewOrganisationTeam(form, bindingResult, ValidationType.FULL, null)))
        .with(authenticatedUserAndSession(organisationAccessManager))
        .with(csrf())
        .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(teamCreationService, times(1)).getOrCreateOrganisationGroupTeam(any(), any());
  }

  @Test
  public void createNewOrganisationTeam_whenUnauthenticated_thenNoAccess() throws Exception {

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new AddOrganisationTeamForm("1");

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(teamCreationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(post(ReverseRouter.route(
        on(PortalTeamManagementController.class).createNewOrganisationTeam(form, bindingResult, ValidationType.FULL, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser))
        .with(csrf())
        .params(completeParams))
        .andExpect(status().isForbidden());

    verify(teamCreationService, times(0)).getOrCreateOrganisationGroupTeam(any(), any());
  }
}