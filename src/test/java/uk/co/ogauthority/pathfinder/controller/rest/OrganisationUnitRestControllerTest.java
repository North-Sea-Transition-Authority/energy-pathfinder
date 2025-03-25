package uk.co.ogauthority.pathfinder.controller.rest;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = OrganisationUnitRestController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class OrganisationUnitRestControllerTest extends AbstractControllerTest {

  private static final String SEARCH_TERM = "search term";

  @MockitoBean
  protected SearchSelectorService searchSelectorService;

  @MockitoBean
  protected PortalOrganisationAccessor portalOrganisationAccessor;

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.WORK_AREA_PRIVILEGES
  );
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Test
  public void searchUserInvolvementOrganisationUnits_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OrganisationUnitRestController.class).searchUserInvolvementOrganisationUnits(SEARCH_TERM, unauthenticatedUser)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());

    verify(teamService, never()).getOrganisationGroupsPersonInTeamFor(unauthenticatedUser.getLinkedPerson());

    verify(portalOrganisationAccessor, never()).getActiveOrganisationUnitsByNameAndOrganisationGroupId(
        eq(SEARCH_TERM),
        anyList()
    );
  }

  @Test
  public void searchUserInvolvementOrganisationUnits_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OrganisationUnitRestController.class).searchUserInvolvementOrganisationUnits(SEARCH_TERM, authenticatedUser)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());

    verify(teamService, times(1)).getOrganisationGroupsPersonInTeamFor(authenticatedUser.getLinkedPerson());

    verify(portalOrganisationAccessor, times(1)).getActiveOrganisationUnitsByNameAndOrganisationGroupId(
        eq(SEARCH_TERM),
        anyList()
    );
  }

  @Test
  public void searchUserInvolvementOrganisationUnits_whenSearchTermIsNull_verifyInteractions() throws Exception {

    mockMvc.perform(get(ReverseRouter.route(
        on(OrganisationUnitRestController.class).searchUserInvolvementOrganisationUnits(null, authenticatedUser)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());

    verify(teamService, times(1)).getOrganisationGroupsPersonInTeamFor(authenticatedUser.getLinkedPerson());

    verify(portalOrganisationAccessor, times(1)).getActiveOrganisationUnitsForOrganisationGroupsIn(
        anyList()
    );

    verify(portalOrganisationAccessor, never()).getActiveOrganisationUnitsByNameAndOrganisationGroupId(
        anyString(),
        anyList()
    );
  }

  @Test
  public void searchUserInvolvementOrganisationUnits_whenSearchTermIsBlank_verifyInteractions() throws Exception {

    final String blankSearchTerm = "";

    mockMvc.perform(get(ReverseRouter.route(
        on(OrganisationUnitRestController.class).searchUserInvolvementOrganisationUnits(blankSearchTerm, authenticatedUser)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());

    verify(teamService, times(1)).getOrganisationGroupsPersonInTeamFor(authenticatedUser.getLinkedPerson());

    verify(portalOrganisationAccessor, times(1)).getActiveOrganisationUnitsForOrganisationGroupsIn(
        anyList()
    );

    verify(portalOrganisationAccessor, never()).getActiveOrganisationUnitsByNameAndOrganisationGroupId(
        anyString(),
        anyList()
    );
  }

  @Test
  public void searchUserInvolvementOrganisationUnits_whenSearchTermIsNotBlank_verifyInteractions() throws Exception {

    final String nonBlankSearchTerm = SEARCH_TERM;

    mockMvc.perform(get(ReverseRouter.route(
        on(OrganisationUnitRestController.class).searchUserInvolvementOrganisationUnits(nonBlankSearchTerm, authenticatedUser)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());

    verify(teamService, times(1)).getOrganisationGroupsPersonInTeamFor(authenticatedUser.getLinkedPerson());

    verify(portalOrganisationAccessor, times(1)).getActiveOrganisationUnitsByNameAndOrganisationGroupId(
        eq(nonBlankSearchTerm),
        anyList()
    );

    verify(portalOrganisationAccessor, never()).getActiveOrganisationUnitsForOrganisationGroupsIn(
        anyList()
    );
  }

  @Test
  public void searchOrganisationUnits_whenAuthenticated_thenAccess() throws Exception {

    mockMvc.perform(get(ReverseRouter.route(
        on(OrganisationUnitRestController.class).searchOrganisationUnits(SEARCH_TERM)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());

    verify(portalOrganisationAccessor, times(1)).findActiveOrganisationUnitsWhereNameContains(SEARCH_TERM);

    verify(searchSelectorService, times(1)).search(
        eq(SEARCH_TERM),
        anyList()
    );
  }

  @Test
  public void searchOrganisationUnits_whenUnauthenticated_thenNoAccess() throws Exception {

    mockMvc.perform(get(ReverseRouter.route(
        on(OrganisationUnitRestController.class).searchOrganisationUnits(SEARCH_TERM)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());

    verify(portalOrganisationAccessor, never()).findActiveOrganisationUnitsWhereNameContains(SEARCH_TERM);

    verify(searchSelectorService, never()).search(
        eq(SEARCH_TERM),
        anyList()
    );
  }

}