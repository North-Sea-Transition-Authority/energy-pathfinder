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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.CollectionUtils;
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

  @Test
  void getAllRemindableProjects_whenNoRemindableProjectsFound_thenReturnEmptyList() {

    when(reportableProjectService.getReportableProjects()).thenReturn(Collections.emptyList());

    var resultingRemindableProjects = quarterlyUpdateReminderService.getAllRemindableProjects();

    assertThat(resultingRemindableProjects).isEmpty();
  }

  @Test
  void getAllRemindableProjects_whenRemindableProjectsFound_thenReturnPopulatedList() {
    var reportableProject = ReportableProjectTestUtil.createReportableProject(FieldStage.DEVELOPMENT);
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

    var remindableProjectCUpcomingTender = mock(UpcomingTender.class);
    var projectDetailC = mock(ProjectDetail.class);
    when(remindableProjectCUpcomingTender.getProjectDetail()).thenReturn(projectDetailC);
    when(projectDetailC.getId()).thenReturn(3);

    var pastUpcomingTenders = List.of(remindableProjectCUpcomingTender, remindableProjectAUpcomingTender);
    var projectsWithPastUpcomingTenders = List.of(remindableProjectA, remindableProjectC);
    when(upcomingTenderService.getPastUpcomingTendersForRemindableProjects(remindableProjectList))
        .thenReturn(pastUpcomingTenders);

    quarterlyUpdateReminderService.sendQuarterlyProjectUpdateReminderToOperators(testQuarterlyUpdateReminderService);

    var remindableProjectListByOperator = remindableProjectList
        .stream()
        .collect(Collectors.groupingBy(RemindableProject::getOperatorGroupId, Collectors.toList()));

    var projectsWithPastUpcomingTendersByOperator = projectsWithPastUpcomingTenders
        .stream()
        .collect(Collectors.groupingBy(RemindableProject::getOperatorGroupId, Collectors.toList()));

    organisationGroupMembershipList.forEach(organisationGroupMembership -> {

      var organisationGroup = organisationGroupMembership.getOrganisationGroup();

      var sortedProjectsDisplayNames = remindableProjectListByOperator.get(organisationGroup.getOrgGrpId())
          .stream()
          .sorted(Comparator.comparing(remindableProject -> remindableProject.getProjectDisplayName().toLowerCase()))
          .map(RemindableProject::getProjectDisplayName)
          .collect(Collectors.toList());

      List<String> sortedProjectsWithPastUpcomingTendersDisplayNames;
      if (CollectionUtils.isEmpty(projectsWithPastUpcomingTendersByOperator)) {
        sortedProjectsWithPastUpcomingTendersDisplayNames = Collections.emptyList();
      } else {
        sortedProjectsWithPastUpcomingTendersDisplayNames = projectsWithPastUpcomingTendersByOperator.get(organisationGroup.getOrgGrpId())
            .stream()
            .sorted(Comparator.comparing(remindableProject -> remindableProject.getProjectDisplayName().toLowerCase()))
            .map(RemindableProject::getProjectDisplayName)
            .collect(Collectors.toList());
      }

      organisationGroupMembership.getTeamMembers().forEach(teamMember -> {

        verify(testQuarterlyUpdateReminderService, times(1)).getReminderEmailProperties(
            teamMember.getForename(),
            organisationGroup.getName(),
            sortedProjectsDisplayNames,
            sortedProjectsWithPastUpcomingTendersDisplayNames
        );

        verify(emailService, times(1)).sendEmail(
            any(),
            eq(teamMember.getEmailAddress()),
            eq(teamMember.getForename())
        );

      });
    });
  }

  @Test
  void getRemindableProjectsNotUpdatedInCurrentQuarter_whenNoRemindableProjectsFound_thenReturnEmptyList() {
    when(reportableProjectService.getReportableProjectsNotUpdatedBetween(any(), any())).thenReturn(Collections.emptyList());

    var resultingRemindableProjects = quarterlyUpdateReminderService.getRemindableProjectsNotUpdatedInCurrentQuarter();

    assertThat(resultingRemindableProjects).isEmpty();
  }

  @Test
  void getRemindableProjectsNotUpdatedInCurrentQuarter_whenRemindableProjectsFound_thenReturnPopulatedList() {
    var reportableProject = ReportableProjectTestUtil.createReportableProject(FieldStage.DEVELOPMENT);
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
