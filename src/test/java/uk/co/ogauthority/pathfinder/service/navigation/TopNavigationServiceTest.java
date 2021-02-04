package uk.co.ogauthority.pathfinder.service.navigation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.quarterlystatistics.QuarterlyStatisticsController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.navigation.TopNavigationItem;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationModelService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class TopNavigationServiceTest {

  @Mock
  private SystemAccessService systemAccessServiceMock;

  @Mock
  private TeamService teamServiceMock;

  private TopNavigationService topNavigationService;

  private AuthenticatedUserAccount unprivilegedUser;
  private AuthenticatedUserAccount workAreaOnlyUser;
  private AuthenticatedUserAccount teamAdministrationOnlyUser;
  private AuthenticatedUserAccount regulatorAdminUser;
  private AuthenticatedUserAccount organisationAdministratorUser;
  private AuthenticatedUserAccount quarterlyStatisticsViewer;
  private AuthenticatedUserAccount communicationsUser;

  @Before
  public void topNavigationServiceTestSetup() {
    topNavigationService = new TopNavigationService(systemAccessServiceMock, teamServiceMock);
    workAreaOnlyUser = UserTestingUtil.getAuthenticatedUserAccount(List.of(UserPrivilege.PATHFINDER_WORK_AREA));
    teamAdministrationOnlyUser = UserTestingUtil.getAuthenticatedUserAccount(List.of(UserPrivilege.PATHFINDER_ORG_ADMIN));
    regulatorAdminUser = UserTestingUtil.getAuthenticatedUserAccount(List.of(UserPrivilege.PATHFINDER_REGULATOR_ADMIN));
    unprivilegedUser = UserTestingUtil.getAuthenticatedUserAccount();
    organisationAdministratorUser = UserTestingUtil.getAuthenticatedUserAccount(List.of(
        UserPrivilege.PATHFINDER_ORG_ADMIN,
        UserPrivilege.PATHFINDER_WORK_AREA,
        UserPrivilege.PATHFINDER_PROJECT_CREATE
    ));
    quarterlyStatisticsViewer = UserTestingUtil.getAuthenticatedUserAccount(List.of(UserPrivilege.PATHFINDER_STATISTIC_VIEWER));
    communicationsUser = UserTestingUtil.getAuthenticatedUserAccount(List.of(UserPrivilege.PATHFINDER_COMMUNICATIONS));

    when(systemAccessServiceMock.canAccessWorkArea(any())).thenReturn(false);
    when(systemAccessServiceMock.canViewTeam(any())).thenReturn(false);
    when(systemAccessServiceMock.canAccessQuarterlyStatistics(any())).thenReturn(false);
  }

  @Test
  public void getTopNavigationItems_whenNoPrivileges_thenNoNavItems() {
    List<TopNavigationItem> navigationItems = topNavigationService.getTopNavigationItems(unprivilegedUser);
    assertThat(navigationItems).isEmpty();
  }

  @Test
  public void getTopNavigationItems_whenOnlyWorkAreaPrivilege_thenOnlyWorkAreaNavItem() {
    when(systemAccessServiceMock.canAccessWorkArea(workAreaOnlyUser)).thenReturn(true);

    List<TopNavigationItem> navigationItems = topNavigationService.getTopNavigationItems(workAreaOnlyUser);
    assertThat(navigationItems).hasSize(1);
    assertThat(navigationItems.get(0).getDisplayName()).isEqualTo(TopNavigationService.WORK_AREA_TITLE);
  }

  @Test
  public void getTopNavigationItems_whenOnlyTeamAdminPrivilege_thenOnlyTeamAdminNavItem() {
    when(systemAccessServiceMock.canViewTeam(teamAdministrationOnlyUser)).thenReturn(true);
    when(teamServiceMock.isPersonMemberOfRegulatorTeam(teamAdministrationOnlyUser.getLinkedPerson())).thenReturn(false);

    List<TopNavigationItem> navigationItems = topNavigationService.getTopNavigationItems(
        teamAdministrationOnlyUser);
    assertThat(navigationItems).hasSize(1);
    assertThat(navigationItems.get(0).getDisplayName()).isEqualTo(TopNavigationService.ORGANISATION_USERS_TITLE);
  }

  @Test
  public void getTopNavigationItems_whenOnlyQuarterlyStatistics_thenOnlyQuarterlyStatisticsNavItem() {
    when(systemAccessServiceMock.canAccessQuarterlyStatistics(any())).thenReturn(true);

    List<TopNavigationItem> navigationItems = topNavigationService.getTopNavigationItems(
        quarterlyStatisticsViewer
    );
    assertThat(navigationItems).hasSize(1);
    assertThat(navigationItems.get(0).getDisplayName()).isEqualTo(QuarterlyStatisticsController.QUARTERLY_STATISTICS_TITLE);
  }

  @Test
  public void getTopNavigationItems_whenOnlyCommunications_thenOnlyCommunicationsNavItem() {
    when(systemAccessServiceMock.canAccessCommunications(any())).thenReturn(true);

    List<TopNavigationItem> navigationItems = topNavigationService.getTopNavigationItems(
        communicationsUser
    );
    assertThat(navigationItems).hasSize(1);
    assertThat(navigationItems.get(0).getDisplayName()).isEqualTo(CommunicationModelService.COMMUNICATION_SUMMARY_PAGE_TITLE);
  }

  @Test
  public void getTopNavigationItems_whenRegulatorUserAndHaveAllPrivileges_thenAllNavItems() {
    when(systemAccessServiceMock.canAccessWorkArea(regulatorAdminUser)).thenReturn(true);
    when(systemAccessServiceMock.canViewTeam(regulatorAdminUser)).thenReturn(true);
    when(teamServiceMock.isPersonMemberOfRegulatorTeam(regulatorAdminUser.getLinkedPerson())).thenReturn(true);
    when(systemAccessServiceMock.canAccessQuarterlyStatistics(any())).thenReturn(true);
    when(systemAccessServiceMock.canAccessCommunications(any())).thenReturn(true);

    List<TopNavigationItem> navigationItems = topNavigationService.getTopNavigationItems(regulatorAdminUser);
    assertThat(navigationItems)
        .extracting(TopNavigationItem::getDisplayName)
        .containsExactly(
            TopNavigationService.WORK_AREA_TITLE,
            TopNavigationService.MANAGE_TEAM_TITLE,
            QuarterlyStatisticsController.QUARTERLY_STATISTICS_TITLE,
            CommunicationModelService.COMMUNICATION_SUMMARY_PAGE_TITLE
        );
  }

  @Test
  public void getTopNavigationItems_whenOrganisationUserAndHaveAllPrivileges_thenAllNavItems() {
    when(systemAccessServiceMock.canAccessWorkArea(organisationAdministratorUser)).thenReturn(true);
    when(systemAccessServiceMock.canViewTeam(organisationAdministratorUser)).thenReturn(true);
    when(teamServiceMock.isPersonMemberOfRegulatorTeam(organisationAdministratorUser.getLinkedPerson())).thenReturn(false);

    List<TopNavigationItem> navigationItems = topNavigationService.getTopNavigationItems(organisationAdministratorUser);
    assertThat(navigationItems)
        .extracting(TopNavigationItem::getDisplayName)
        .containsExactly(
            TopNavigationService.WORK_AREA_TITLE,
            TopNavigationService.ORGANISATION_USERS_TITLE
        );
  }

}