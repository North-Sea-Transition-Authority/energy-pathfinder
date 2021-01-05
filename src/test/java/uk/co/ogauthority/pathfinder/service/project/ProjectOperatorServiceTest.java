package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectOperatorServiceTest {

  @Mock
  private TeamService teamService;

  @Mock
  private ProjectOperatorRepository projectOperatorRepository;

  private ProjectOperatorService projectOperatorService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final Person person = UserTestingUtil.getPerson(authenticatedUser);

  private static final PortalOrganisationGroup organisationGroup = TeamTestingUtil.generateOrganisationGroup(
      1,
      "Org Grp",
      "Org Grp"
  );

  private static final OrganisationTeam organisationTeam = TeamTestingUtil.getOrganisationTeam(organisationGroup);

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final ProjectOperator projectOperator = ProjectOperatorTestUtil.getOperator(detail, organisationGroup);

  @Before
  public void setUp() {
    projectOperatorService = new ProjectOperatorService(
        teamService,
        projectOperatorRepository
    );

    when(projectOperatorRepository.save(any(ProjectOperator.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createOrUpdateProjectOperator_noExisting() {
    var projectOperator = projectOperatorService.createOrUpdateProjectOperator(detail, organisationGroup);
    assertThat(projectOperator.getOrganisationGroup()).isEqualTo(organisationGroup);
    assertThat(projectOperator.getProjectDetail()).isEqualTo(detail);
  }

  @Test
  public void createOrUpdateProjectOperator_withExisting() {
    when(projectOperatorRepository.findByProjectDetail(detail)).thenReturn(
        Optional.of(projectOperator)
    );
    var newOrgGroup = TeamTestingUtil.generateOrganisationGroup(
        2,
        "Org Grp2",
        "Org Grp2"
    );
    var projectOperator = projectOperatorService.createOrUpdateProjectOperator(detail, newOrgGroup);
    assertThat(projectOperator.getOrganisationGroup()).isEqualTo(newOrgGroup);
    assertThat(projectOperator.getProjectDetail()).isEqualTo(detail);
  }

  @Test
  public void isUserInProjectTeamOrRegulator_inProjectTeam() {
    when(teamService.isPersonMemberOfRegulatorTeam(person)).thenReturn(false);
    when(teamService.getOrganisationTeamsPersonIsMemberOf(person)).thenReturn(
        Collections.singletonList(organisationTeam));
    when(projectOperatorRepository.findByProjectDetail(detail)).thenReturn(Optional.of(projectOperator));
    assertThat(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).isTrue();

  }

  @Test
  public void isUserInProjectTeamOrRegulator_whenRegulator() {
    when(teamService.isPersonMemberOfRegulatorTeam(any())).thenReturn(true);
    assertThat(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).isTrue();
  }

  @Test
  public void isUserInProjectTeamOrRegulator_whenNotInTeam() {
    when(teamService.isPersonMemberOfRegulatorTeam(person)).thenReturn(false);
    when(teamService.getOrganisationTeamsPersonIsMemberOf(person)).thenReturn(Collections.singletonList(
        TeamTestingUtil.getOrganisationTeam(
            TeamTestingUtil.generateOrganisationGroup(
                2,
                "DifferentGrp",
                "DiffGrp"
            )
        )
    ));
    when(projectOperatorRepository.findByProjectDetail(detail)).thenReturn(Optional.of(projectOperator));
    assertThat(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).isFalse();
  }

  @Test
  public void canUserAccessOrgGroup_whenInTeam() {
    when(teamService.getOrganisationTeamsPersonIsMemberOf(person)).thenReturn(
        Collections.singletonList(organisationTeam));
    assertThat(projectOperatorService.canUserAccessOrgGroup(authenticatedUser, organisationGroup)).isTrue();
  }

  @Test
  public void canUserAccessOrgGroup_whenNotInTeam() {
    when(teamService.getOrganisationTeamsPersonIsMemberOf(person)).thenReturn(Collections.emptyList());
    assertThat(projectOperatorService.canUserAccessOrgGroup(authenticatedUser, organisationGroup)).isFalse();
  }

  @Test
  public void isUserInMultipleTeams_singleTeam() {
    when(teamService.getOrganisationTeamsPersonIsMemberOf(person)).thenReturn(
        Collections.singletonList(organisationTeam));
    assertThat(projectOperatorService.isUserInMultipleTeams(authenticatedUser)).isFalse();
  }

  @Test
  public void deleteProjectOperatorByProjectDetail() {
    projectOperatorService.deleteProjectOperatorByProjectDetail(detail);

    verify(projectOperatorRepository, times(1)).deleteByProjectDetail(detail);
  }

  @Test
  public void isUserInMultipleTeams_multipleTeam() {
    when(teamService.getOrganisationTeamsPersonIsMemberOf(person)).thenReturn(List.of(organisationTeam,
        TeamTestingUtil.getOrganisationTeam(
            TeamTestingUtil.generateOrganisationGroup(
                2,
                "Org Grp2",
                "Org Grp2"
            ))
        )
    );
    assertThat(projectOperatorService.isUserInMultipleTeams(authenticatedUser)).isTrue();
  }

  @Test
  public void getProjectOperatorByProjectAndVersion_whenFound_thenReturn() {
    when(projectOperatorRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        detail.getProject(),
        detail.getVersion()
    )).thenReturn(Optional.of(projectOperator));

    var resultingProjectOperator = projectOperatorService.getProjectOperatorByProjectAndVersion(
        detail.getProject(),
        detail.getVersion()
    );

    assertThat(resultingProjectOperator).contains(projectOperator);
  }

  @Test
  public void getProjectOperatorByProjectAndVersion_whenNotFound_thenReturnEmpty() {
    when(projectOperatorRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        detail.getProject(),
        detail.getVersion()
    )).thenReturn(Optional.empty());

    var resultingProjectOperator = projectOperatorService.getProjectOperatorByProjectAndVersion(
        detail.getProject(),
        detail.getVersion()
    );

    assertThat(resultingProjectOperator).isEmpty();
  }

  @Test
  public void getProjectOperatorByProjectDetailOrError_whenFound_thenReturn() {
    when(projectOperatorRepository.findByProjectDetail(detail)).thenReturn(Optional.of(projectOperator));
    var resultingProjectOperator = projectOperatorService.getProjectOperatorByProjectDetailOrError(detail);
    assertThat(resultingProjectOperator).isEqualTo(projectOperator);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getProjectOperatorByProjectDetailOrError_whenNotFound_thenException() {
    when(projectOperatorRepository.findByProjectDetail(detail)).thenReturn(Optional.empty());
    projectOperatorService.getProjectOperatorByProjectDetailOrError(detail);
  }
}
