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
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.navigation.TopNavigationItem;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class TopNavigationServiceTest {

  @Mock
  private SystemAccessService systemAccessServiceMock;

  private TopNavigationService topNavigationService;

  private AuthenticatedUserAccount unprivilegedUser;
  private AuthenticatedUserAccount workAreaOnlyUser;
  private AuthenticatedUserAccount teamAdministrationOnlyUser;
  private AuthenticatedUserAccount regulatorAdminUser;

  @Before
  public void topNavigationServiceTestSetup() {
    topNavigationService = new TopNavigationService(systemAccessServiceMock);
    workAreaOnlyUser = UserTestingUtil.getAuthenticatedUserAccount(List.of(UserPrivilege.PATHFINDER_WORK_AREA));
    teamAdministrationOnlyUser = UserTestingUtil.getAuthenticatedUserAccount(List.of(UserPrivilege.PATHFINDER_ORG_ADMIN));
    regulatorAdminUser = UserTestingUtil.getAuthenticatedUserAccount(List.of(UserPrivilege.PATHFINDER_REGULATOR_ADMIN));
    unprivilegedUser = UserTestingUtil.getAuthenticatedUserAccount();

    when(systemAccessServiceMock.canAccessWorkArea(any())).thenReturn(false);
    when(systemAccessServiceMock.canAccessTeamAdministration(any())).thenReturn(false);
  }

  @Test
  public void getTopNavigationItems_noPrivileges() {
    List<TopNavigationItem> navigationItems = topNavigationService.getTopNavigationItems(unprivilegedUser);
    assertThat(navigationItems.isEmpty());
  }

  @Test
  public void getTopNavigationItems_onlyWorkArea() {
    when(systemAccessServiceMock.canAccessWorkArea(workAreaOnlyUser)).thenReturn(true);

    List<TopNavigationItem> navigationItems = topNavigationService.getTopNavigationItems(workAreaOnlyUser);
    assertThat(navigationItems).hasSize(1);
    assertThat(navigationItems.get(0).getDisplayName()).isEqualTo(TopNavigationService.WORK_AREA_TITLE);
  }

  @Test
  public void getTopNavigationItems_onlyTeamAdministration() {
    when(systemAccessServiceMock.canAccessTeamAdministration(teamAdministrationOnlyUser)).thenReturn(true);

    List<TopNavigationItem> navigationItems = topNavigationService.getTopNavigationItems(teamAdministrationOnlyUser);
    assertThat(navigationItems).hasSize(1);
    assertThat(navigationItems.get(0).getDisplayName()).isEqualTo(TopNavigationService.MANAGE_TEAM_TITLE);
  }

  @Test
  public void getTopNavigationItems_allNavigationItems() {
    when(systemAccessServiceMock.canAccessWorkArea(regulatorAdminUser)).thenReturn(true);
    when(systemAccessServiceMock.canAccessTeamAdministration(regulatorAdminUser)).thenReturn(true);

    List<TopNavigationItem> navigationItems = topNavigationService.getTopNavigationItems(regulatorAdminUser);
    assertThat(navigationItems)
        .extracting(TopNavigationItem::getDisplayName)
        .containsExactly(
            TopNavigationService.WORK_AREA_TITLE,
            TopNavigationService.MANAGE_TEAM_TITLE
        );
  }

}