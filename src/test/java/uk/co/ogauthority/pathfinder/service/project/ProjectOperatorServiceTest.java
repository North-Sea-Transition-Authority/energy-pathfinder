package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetails;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.team.OrganisationRole;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorsRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.teammanagement.TeamManagementService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectOperatorServiceTest {

  @Mock
  private TeamService teamService;
  @Mock
  private TeamManagementService teamManagementService;
  @Mock
  private ProjectOperatorsRepository projectOperatorsRepository;

  private ProjectOperatorService projectOperatorService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final Person person = UserTestingUtil.getPerson(authenticatedUser);

  private static final OrganisationTeam organisationTeam = TeamTestingUtil.getOrganisationTeam(TeamTestingUtil.generateOrganisationGroup(
      1,
      "Org Grp",
      "Org Grp"
  ));

  private final ProjectDetails projectDetails = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() throws Exception {
    projectOperatorService = new ProjectOperatorService(
        teamService,
        teamManagementService,
        projectOperatorsRepository
    );

    when(projectOperatorsRepository.save(any(ProjectOperator.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createProjectOperator() {
    when(teamManagementService.getPerson(authenticatedUser.getLinkedPerson().getId().asInt())).thenReturn(person);
    when(teamService.getOrganisationTeamListIfPersonInRole(
        person,
        Collections.singletonList(OrganisationRole.PROJECT_SUBMITTER)
    )).thenReturn(Collections.singletonList(organisationTeam));
    var projectOperator = projectOperatorService.createProjectOperator(projectDetails, authenticatedUser);
    assertThat(projectOperator.getOrganisationGroup()).isEqualTo(organisationTeam.getPortalOrganisationGroup());
    assertThat(projectOperator.getProjectDetail()).isEqualTo(projectDetails);
  }
}
