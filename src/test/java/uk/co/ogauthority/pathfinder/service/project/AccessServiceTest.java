package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.SecurityHelperUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class AccessServiceTest {

  private final AuthenticatedUserAccount authenticatedUserAccount = UserTestingUtil.getAuthenticatedUserAccount();

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private TeamService teamService;

  private AccessService accessService;

  @Before
  public void setup() {
    SecurityHelperUtil.setAuthentication(authenticatedUserAccount);

    accessService = new AccessService(
        projectOperatorService,
        teamService
    );
  }

  @Test
  public void canCurrentUserAccessProjectSectionInfo_whenUserIsOperator_thenTrue() {
    when(projectOperatorService.isUserInProjectTeam(detail, authenticatedUserAccount)).thenReturn(true);

    assertThat(accessService.canCurrentUserAccessProjectSectionInfo(detail, new OrganisationGroupIdWrapper(1))).isTrue();
  }

  @Test
  public void canCurrentUserAccessProjectSectionInfo_whenUserNotOperatorAndCreatedSectionInfo_thenTrue() {
    var portalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "org", "org");
    var organisationGroupIdWrapper = new OrganisationGroupIdWrapper(portalOrganisationGroup.getOrgGrpId());

    when(projectOperatorService.isUserInProjectTeam(detail, authenticatedUserAccount)).thenReturn(false);
    when(teamService.getOrganisationTeamsPersonIsMemberOf(authenticatedUserAccount.getLinkedPerson()))
        .thenReturn(List.of(TeamTestingUtil.getOrganisationTeam(portalOrganisationGroup)));

    assertThat(accessService.canCurrentUserAccessProjectSectionInfo(detail, organisationGroupIdWrapper)).isTrue();
  }

  @Test
  public void canCurrentUserAccessProjectSectionInfo_whenUserNotOperatorAndNotCreatedSectionInfo_thenFalse() {
    var portalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(42, "org", "org");
    var organisationGroupIdWrapper = new OrganisationGroupIdWrapper(portalOrganisationGroup.getOrgGrpId());
    var differentOrganisationTeam = TeamTestingUtil.getOrganisationTeam(1, "other org");

    when(projectOperatorService.isUserInProjectTeam(detail, authenticatedUserAccount)).thenReturn(false);
    when(teamService.getOrganisationTeamsPersonIsMemberOf(authenticatedUserAccount.getLinkedPerson()))
        .thenReturn(List.of(differentOrganisationTeam));

    assertThat(accessService.canCurrentUserAccessProjectSectionInfo(detail, organisationGroupIdWrapper)).isFalse();
  }
}