package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.OrganisationGroupMembership;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationGroupPersonMembershipService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.quarterlystatistics.ReportableProjectService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.ReportableProjectTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@ExtendWith(SpringExtension.class)
class QuarterlyUpdateReminderServiceTest {

  @Mock
  private ReportableProjectService reportableProjectService;

  @Mock
  private PortalOrganisationGroupPersonMembershipService portalOrganisationGroupPersonMembershipService;

  @Mock
  private EmailService emailService;

  @Mock
  private TestQuarterlyUpdateReminderService testQuarterlyUpdateReminderService;

  @Mock
  private UpcomingTenderService upcomingTenderService;

  @InjectMocks
  private QuarterlyUpdateReminderService quarterlyUpdateReminderService;

  @Captor
  private ArgumentCaptor<List<String>> projectsWithPastUpcomingTendersCaptor;

  @Captor
  private ArgumentCaptor<List<String>> remindableProjectsCaptor;

  @Test
  void getAllRemindableProjects_whenNoRemindableProjectsFound_thenReturnEmptyList() {

    when(reportableProjectService.getReportableProjects()).thenReturn(Collections.emptyList());

    var resultingRemindableProjects = quarterlyUpdateReminderService.getAllRemindableProjects();

    assertThat(resultingRemindableProjects).isEmpty();
  }

  @Test
  void getAllRemindableProjects_whenRemindableProjectsFound_thenReturnPopulatedList() {
    var reportableProject = ReportableProjectTestUtil.createReportableProject(FieldStage.OIL_AND_GAS);
    when(reportableProjectService.getReportableProjects()).thenReturn(List.of(reportableProject));

    var resultingRemindableProjects = quarterlyUpdateReminderService.getAllRemindableProjects();

    assertThat(resultingRemindableProjects).containsExactly(
        new RemindableProject(
            reportableProject.getProjectDetailId(),
            reportableProject.getOperatorGroupId(),
            reportableProject.getProjectDisplayName()
        )
    );
  }

  @Test
  void sendQuarterlyProjectUpdateReminderToOperators_whenNoRemindableProjects_verifyNoEmailsSent() {
    quarterlyUpdateReminderService.sendQuarterlyProjectUpdateReminderToOperators(testQuarterlyUpdateReminderService);

    verify(emailService, never()).sendEmail(any(), anyString(), anyString());
  }

  @Test
  void sendQuarterlyProjectUpdateReminderToOperators_whenRemindableProjects_verifyEmailToEachTeamMember() {
    var organisationGroupA = TeamTestingUtil.generateOrganisationGroup(10, "organisation group A", "A short name");

    var firstMemberOfOrganisationA = createPerson(1000, "person1@organisationA.com");
    var secondMemberOfOrganisationA = createPerson(2000, "person2@organisationA.com");

    var organisationAMembership = new OrganisationGroupMembership(
        100,
        organisationGroupA,
        List.of(firstMemberOfOrganisationA, secondMemberOfOrganisationA)
    );

    var organisationGroupB = TeamTestingUtil.generateOrganisationGroup(20, "organisation group B", "B short name");

    var firstMemberOfOrganisationB = createPerson(3000, "person1@organisationB.com");

    var organisationBMembership = new OrganisationGroupMembership(
        200,
        organisationGroupB,
        List.of(firstMemberOfOrganisationB)
    );

    var organisationGroupMembershipList = List.of(
        organisationAMembership,
        organisationBMembership
    );

    when(portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIdsIn(any()))
        .thenReturn(organisationGroupMembershipList);

    var remindableProjectA = new RemindableProject(1, organisationGroupA.getOrgGrpId(), "A project");
    var remindableProjectB = new RemindableProject(2, organisationGroupB.getOrgGrpId(), "b project");
    var remindableProjectC = new RemindableProject(3, organisationGroupB.getOrgGrpId(), "C project");

    var remindableProjectList = List.of(remindableProjectC, remindableProjectB, remindableProjectA);

    when(testQuarterlyUpdateReminderService.getRemindableProjects()).thenReturn(remindableProjectList);

    var remindableProjectAUpcomingTender = mock(UpcomingTender.class);
    var projectDetailA = mock(ProjectDetail.class);
    when(remindableProjectAUpcomingTender.getProjectDetail()).thenReturn(projectDetailA);
    when(projectDetailA.getId()).thenReturn(1);

    var remindableProjectCUpcomingTenderFirst = mock(UpcomingTender.class);
    var remindableProjectCUpcomingTenderSecond = mock(UpcomingTender.class);
    var projectDetailC = mock(ProjectDetail.class);
    when(projectDetailC.getId()).thenReturn(3);
    when(remindableProjectCUpcomingTenderFirst.getProjectDetail()).thenReturn(projectDetailC);
    when(remindableProjectCUpcomingTenderSecond.getProjectDetail()).thenReturn(projectDetailC);

    var pastUpcomingTenders = List.of(remindableProjectCUpcomingTenderFirst, remindableProjectCUpcomingTenderSecond, remindableProjectAUpcomingTender);
    when(upcomingTenderService.getPastUpcomingTendersForRemindableProjects(remindableProjectList))
        .thenReturn(pastUpcomingTenders);

    quarterlyUpdateReminderService.sendQuarterlyProjectUpdateReminderToOperators(testQuarterlyUpdateReminderService);

    organisationGroupMembershipList.forEach(organisationGroupMembership -> {

      var organisationGroup = organisationGroupMembership.getOrganisationGroup();

      var expectedRemindableProjects = Map.of(
          organisationGroupA.getOrgGrpId(), List.of("A project"),
          organisationGroupB.getOrgGrpId(), List.of("b project", "C project")
      );

      var expectedProjectsWithPastUpcomingTenders = Map.of(
          organisationGroupA.getOrgGrpId(), List.of("A project"),
          organisationGroupB.getOrgGrpId(), List.of("C project")
      );

      organisationGroupMembership.getTeamMembers().forEach(teamMember -> {

        verify(testQuarterlyUpdateReminderService, times(1)).getReminderEmailProperties(
            eq(teamMember.getForename()),
            eq(organisationGroup.getName()),
            remindableProjectsCaptor.capture(),
            projectsWithPastUpcomingTendersCaptor.capture()
        );

        verify(emailService, times(1)).sendEmail(
            any(),
            eq(teamMember.getEmailAddress()),
            eq(teamMember.getForename())
        );

        assertThat(remindableProjectsCaptor.getValue()).isEqualTo(expectedRemindableProjects.get(organisationGroup.getOrgGrpId()));
        assertThat(projectsWithPastUpcomingTendersCaptor.getValue())
            .isEqualTo(expectedProjectsWithPastUpcomingTenders.get(organisationGroup.getOrgGrpId()));
      });
    });
  }

  @Test
  void sendQuarterlyProjectUpdateReminderToOperators_whenRemindableProjectsAndNoUpcomingTenders_verifyEmailToEachTeamMember() {
    var organisationGroup = TeamTestingUtil.generateOrganisationGroup(10, "organisation group", "short name");

    var memberOfOrganisation = createPerson(1000, "person1@organisation.com");

    var organisationMembership = new OrganisationGroupMembership(
        100,
        organisationGroup,
        List.of(memberOfOrganisation)
    );

    when(portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIdsIn(any()))
        .thenReturn(List.of(organisationMembership));

    var remindableProject = new RemindableProject(1, organisationGroup.getOrgGrpId(), "project display name");
    var remindableProjectList = List.of(remindableProject);
    when(testQuarterlyUpdateReminderService.getRemindableProjects()).thenReturn(remindableProjectList);

    when(upcomingTenderService.getPastUpcomingTendersForRemindableProjects(remindableProjectList))
        .thenReturn(Collections.emptyList());

    quarterlyUpdateReminderService.sendQuarterlyProjectUpdateReminderToOperators(testQuarterlyUpdateReminderService);

    verify(testQuarterlyUpdateReminderService, times(1)).getReminderEmailProperties(
        eq(memberOfOrganisation.getForename()),
        eq(organisationGroup.getName()),
        remindableProjectsCaptor.capture(),
        projectsWithPastUpcomingTendersCaptor.capture()
    );

    verify(emailService, times(1)).sendEmail(
        any(),
        eq(memberOfOrganisation.getEmailAddress()),
        eq(memberOfOrganisation.getForename())
    );

    assertThat(remindableProjectsCaptor.getValue()).isEqualTo(List.of("project display name"));
    assertThat(projectsWithPastUpcomingTendersCaptor.getValue()).isEqualTo(Collections.emptyList());
  }

  @Test
  void getRemindableProjectsNotUpdatedInCurrentQuarter_whenNoRemindableProjectsFound_thenReturnEmptyList() {
    when(reportableProjectService.getReportableProjectsNotUpdatedBetween(any(), any())).thenReturn(Collections.emptyList());

    var resultingRemindableProjects = quarterlyUpdateReminderService.getRemindableProjectsNotUpdatedInCurrentQuarter();

    assertThat(resultingRemindableProjects).isEmpty();
  }

  @Test
  void getRemindableProjectsNotUpdatedInCurrentQuarter_whenRemindableProjectsFound_thenReturnPopulatedList() {
    var reportableProject = ReportableProjectTestUtil.createReportableProject(FieldStage.OIL_AND_GAS);
    var projectDetailId = reportableProject.getProjectDetailId();
    var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setId(projectDetailId);

    when(reportableProjectService.getReportableProjectsNotUpdatedBetween(any(), any())).thenReturn(List.of(reportableProject));

    var resultingRemindableProjects = quarterlyUpdateReminderService.getRemindableProjectsNotUpdatedInCurrentQuarter();

    assertThat(resultingRemindableProjects).containsExactly(
        new RemindableProject(
            reportableProject.getProjectDetailId(),
            reportableProject.getOperatorGroupId(),
            reportableProject.getProjectDisplayName()
        )
    );
  }

  private Person createPerson(int personId, String emailAddress) {
    return UserTestingUtil.getPerson(
        personId,
        String.format("%s forename", personId),
        String.format("%s surname", personId),
        emailAddress,
        "123"
    );
  }

}
