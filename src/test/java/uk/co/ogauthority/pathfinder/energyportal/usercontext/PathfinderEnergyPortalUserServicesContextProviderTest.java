package uk.co.ogauthority.pathfinder.energyportal.usercontext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.fivium.energyportal.starter.usercontext.VersionedUserContext;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.team.OrganisationRole;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.model.team.RegulatorRole;
import uk.co.ogauthority.pathfinder.model.team.RegulatorTeam;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.QuarterlyUpdateReminderService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.RemindableProject;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateRequestProjectDto;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUpdateTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class PathfinderEnergyPortalUserServicesContextProviderTest {

  private static final Clock CLOCK = Clock.fixed(Instant.now(), ZoneId.of("UTC"));
  private static final long WUA_ID = 1L;
  private static final WebUserAccount WEB_USER_ACCOUNT = UserTestingUtil.getWebUserAccount();
  private static final int PORTAL_ORG_GROUP_ID = 50;
  private static final PortalOrganisationGroup PORTAL_ORGANISATION_GROUP = TeamTestingUtil.generateOrganisationGroup(
      PORTAL_ORG_GROUP_ID,
      "Test Group",
      "TEST"
  );
  private static final PortalOrganisationGroup OTHER_PORTAL_ORGANISATION_GROUP = TeamTestingUtil.generateOrganisationGroup(
      999,
      "Test Group",
      "TEST"
  );
  private static final OrganisationTeam ORGANISATION_TEAM = TeamTestingUtil.getOrganisationTeam(PORTAL_ORGANISATION_GROUP);
  private static final ProjectDetail PROJECT_DETAIL = ProjectUtil.getProjectDetails(ProjectStatus.QA);
  private static final RegulatorTeam REGULATOR_TEAM = TeamTestingUtil.getRegulatorTeam();

  @Mock
  private WebUserAccountService webUserAccountService;

  @Mock
  private TeamService teamService;

  @Mock
  private QuarterlyUpdateReminderService quarterlyUpdateReminderService;

  @Mock
  private UpcomingTenderService upcomingTenderService;

  @Mock
  private RegulatorUpdateRequestService regulatorUpdateRequestService;

  @Mock
  private ProjectService projectService;

  @Mock
  private ProjectAssessmentService projectAssessmentService;

  private PathfinderEnergyPortalUserServicesContextProvider contextProvider;

  @Before
  public void setUp() {
    contextProvider = new PathfinderEnergyPortalUserServicesContextProvider(
        webUserAccountService,
        teamService,
        quarterlyUpdateReminderService,
        upcomingTenderService,
        regulatorUpdateRequestService,
        projectService,
        projectAssessmentService,
        CLOCK
    );

    when(webUserAccountService.getWebUserAccountOrError(Math.toIntExact(WUA_ID))).thenReturn(WEB_USER_ACCOUNT);
  }

  @Test
  public void addQuarterlyUpdatesRequired_noneRequired() {
    var otherOrgRemindableProject = new RemindableProject(1, 2, "project name");

    when(teamService.getOrganisationTeamListIfPersonInRole(
        WEB_USER_ACCOUNT.getLinkedPerson(),
        List.of(OrganisationRole.PROJECT_SUBMITTER)
    )).thenReturn(List.of(ORGANISATION_TEAM));

    when(quarterlyUpdateReminderService.getRemindableProjectsNotUpdatedInCurrentQuarter())
        .thenReturn(List.of(otherOrgRemindableProject));

    var expectedUserContext = VersionedUserContext.newBuilder().v1().build();

    assertThat(contextProvider.getUserContext(WUA_ID)).isEqualTo(expectedUserContext);
  }

  @Test
  public void addQuarterlyUpdatesRequired() {
    var remindableProject = new RemindableProject(1, PORTAL_ORG_GROUP_ID, "project name");

    when(teamService.getOrganisationTeamListIfPersonInRole(
        WEB_USER_ACCOUNT.getLinkedPerson(),
        List.of(OrganisationRole.PROJECT_SUBMITTER)
    )).thenReturn(List.of(ORGANISATION_TEAM));

    when(quarterlyUpdateReminderService.getRemindableProjectsNotUpdatedInCurrentQuarter())
        .thenReturn(List.of(remindableProject));

    var expectedUserContext = VersionedUserContext.newBuilder().v1()
        .low(1, "quarterly update required")
        .build();

    assertThat(contextProvider.getUserContext(WUA_ID)).isEqualTo(expectedUserContext);
  }

  @Test
  public void addPastUpcomingTenders_noneInPast() {
    when(teamService.getOrganisationTeamListIfPersonInRole(
        WEB_USER_ACCOUNT.getLinkedPerson(),
        List.of(OrganisationRole.PROJECT_SUBMITTER)
    )).thenReturn(List.of(ORGANISATION_TEAM));

    when(upcomingTenderService.getPastUpcomingTendersForOrganisationGroupsIn(List.of(PORTAL_ORG_GROUP_ID)))
        .thenReturn(List.of());

    var expectedUserContext = VersionedUserContext.newBuilder().v1().build();

    assertThat(contextProvider.getUserContext(WUA_ID)).isEqualTo(expectedUserContext);
  }

  @Test
  public void addPastUpcomingTenders() {
    var upcomingTender1 = UpcomingTenderUtil.getUpcomingTender(PROJECT_DETAIL);
    var upcomingTender2 = UpcomingTenderUtil.getUpcomingTender(PROJECT_DETAIL);

    when(teamService.getOrganisationTeamListIfPersonInRole(
        WEB_USER_ACCOUNT.getLinkedPerson(),
        List.of(OrganisationRole.PROJECT_SUBMITTER)
    )).thenReturn(List.of(ORGANISATION_TEAM));

    when(upcomingTenderService.getPastUpcomingTendersForOrganisationGroupsIn(List.of(PORTAL_ORG_GROUP_ID)))
        .thenReturn(List.of(upcomingTender1, upcomingTender2));

    var expectedUserContext = VersionedUserContext.newBuilder().v1()
        .high(2, "upcoming tender dates in the past")
        .build();

    assertThat(contextProvider.getUserContext(WUA_ID)).isEqualTo(expectedUserContext);
  }

  @Test
  public void addRegulatorUpdatesRequestedAndOverdue_noneRequestedAndOverdue() {
    var otherRegulatorUpdateRequestProjectDto = new RegulatorUpdateRequestProjectDto(
        ProjectUpdateTestUtil.createRegulatorUpdateRequest(),
        ProjectOperatorTestUtil.getOperator(OTHER_PORTAL_ORGANISATION_GROUP)
    );

    when(teamService.getOrganisationTeamListIfPersonInRole(
        WEB_USER_ACCOUNT.getLinkedPerson(),
        List.of(OrganisationRole.PROJECT_SUBMITTER)
    )).thenReturn(List.of(ORGANISATION_TEAM));

    when(regulatorUpdateRequestService.getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines())
        .thenReturn(List.of(otherRegulatorUpdateRequestProjectDto));

    var expectedUserContext = VersionedUserContext.newBuilder().v1().build();

    assertThat(contextProvider.getUserContext(WUA_ID)).isEqualTo(expectedUserContext);
  }

  @Test
  public void addRegulatorUpdatesRequestedAndOverdue() {
    var regulatorUpdateRequestProjectDto = new RegulatorUpdateRequestProjectDto(
        ProjectUpdateTestUtil.createRegulatorUpdateRequest(),
        ProjectOperatorTestUtil.getOperator(PORTAL_ORGANISATION_GROUP)
    );

    var overDueRegulatorUpdateRequestProjectDto = new RegulatorUpdateRequestProjectDto(
        ProjectUpdateTestUtil.createOverDueRegulatorUpdateRequest(),
        ProjectOperatorTestUtil.getOperator(PORTAL_ORGANISATION_GROUP)
    );

    when(teamService.getOrganisationTeamListIfPersonInRole(
        WEB_USER_ACCOUNT.getLinkedPerson(),
        List.of(OrganisationRole.PROJECT_SUBMITTER)
    )).thenReturn(List.of(ORGANISATION_TEAM));

    when(regulatorUpdateRequestService.getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines())
        .thenReturn(List.of(regulatorUpdateRequestProjectDto, overDueRegulatorUpdateRequestProjectDto));

    var expectedUserContext = VersionedUserContext.newBuilder().v1()
        .low(1, "update requested")
        .high(1, "update overdue")
        .build();

    assertThat(contextProvider.getUserContext(WUA_ID)).isEqualTo(expectedUserContext);
  }

  @Test
  public void addProjectsAwaitingAssessment_noneAwaitingAssessment() {
    when(teamService.getRegulatorTeamIfPersonInRole(
        WEB_USER_ACCOUNT.getLinkedPerson(),
        List.of(RegulatorRole.PROJECT_ADMINISTRATOR)
    )).thenReturn(Optional.of(REGULATOR_TEAM));

    when(projectService.findAllLatestDetailByStatus(ProjectStatus.QA)).thenReturn(List.of());
    when(projectAssessmentService.countByProjectDetailIn(Collections.emptyList())).thenReturn(0);

    var expectedUserContext = VersionedUserContext.newBuilder().v1().build();

    assertThat(contextProvider.getUserContext(WUA_ID)).isEqualTo(expectedUserContext);
  }

  @Test
  public void addProjectsAwaitingAssessment() {
    var projectsInQA = List.of(
        ProjectUtil.getProjectDetails(),
        ProjectUtil.getProjectDetails(),
        ProjectUtil.getProjectDetails(),
        ProjectUtil.getProjectDetails()
    );

    when(teamService.getRegulatorTeamIfPersonInRole(
        WEB_USER_ACCOUNT.getLinkedPerson(),
        List.of(RegulatorRole.PROJECT_ADMINISTRATOR)
    )).thenReturn(Optional.of(REGULATOR_TEAM));

    when(projectService.findAllLatestDetailByStatus(ProjectStatus.QA)).thenReturn(projectsInQA);
    when(projectAssessmentService.countByProjectDetailIn(projectsInQA)).thenReturn(1);

    var expectedUserContext = VersionedUserContext.newBuilder().v1()
        .low(3, "awaiting assessment")
        .build();

    assertThat(contextProvider.getUserContext(WUA_ID)).isEqualTo(expectedUserContext);
  }
}