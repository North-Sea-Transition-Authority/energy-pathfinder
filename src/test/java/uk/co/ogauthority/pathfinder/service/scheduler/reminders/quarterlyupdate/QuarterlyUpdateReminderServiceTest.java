package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.OrganisationGroupMembership;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationGroupPersonMembershipService;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.quarterlystatistics.ReportableProjectService;
import uk.co.ogauthority.pathfinder.testutil.ReportableProjectTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class QuarterlyUpdateReminderServiceTest {

  @Mock
  private ReportableProjectService reportableProjectService;

  @Mock
  private PortalOrganisationGroupPersonMembershipService portalOrganisationGroupPersonMembershipService;

  @Mock
  private EmailService emailService;

  @Mock
  private TestQuarterlyUpdateReminderService testQuarterlyUpdateReminderService;

  private QuarterlyUpdateReminderService quarterlyUpdateReminderService;

  @Before
  public void setup() {
    quarterlyUpdateReminderService = new QuarterlyUpdateReminderService(
        reportableProjectService,
        portalOrganisationGroupPersonMembershipService,
        emailService
    );
  }

  @Test
  public void getAllRemindableProjects_whenNoRemindableProjectsFound_thenReturnEmptyList() {

    when(reportableProjectService.getReportableProjects()).thenReturn(Collections.emptyList());

    var resultingRemindableProjects = quarterlyUpdateReminderService.getAllRemindableProjects();

    assertThat(resultingRemindableProjects).isEmpty();
  }

  @Test
  public void getAllRemindableProjects_whenRemindableProjectsFound_thenReturnPopulatedList() {

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
  public void sendQuarterlyProjectUpdateReminderToOperators_whenNoRemindableProjects_verifyNoEmailsSent() {

    quarterlyUpdateReminderService.sendQuarterlyProjectUpdateReminderToOperators(testQuarterlyUpdateReminderService);

    verify(emailService, never()).sendEmail(any(), anyString(), anyString());
  }

  @Test
  public void sendQuarterlyProjectUpdateReminderToOperators_whenRemindableProjects_verifyEmailToEachTeamMember() {

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
    var remindableProjectB = new RemindableProject(2, organisationGroupB.getOrgGrpId(), "B project");
    var remindableProjectC = new RemindableProject(3, organisationGroupB.getOrgGrpId(), "C project");

    var remindableProjectList = List.of(remindableProjectC, remindableProjectB, remindableProjectA);

    when(testQuarterlyUpdateReminderService.getRemindableProjects()).thenReturn(remindableProjectList);

    quarterlyUpdateReminderService.sendQuarterlyProjectUpdateReminderToOperators(testQuarterlyUpdateReminderService);

    var remindableProjectListByOperator = remindableProjectList
        .stream()
        .collect(Collectors.groupingBy(RemindableProject::getOperatorGroupId, Collectors.toList()));

    organisationGroupMembershipList.forEach(organisationGroupMembership -> {

      var organisationGroup = organisationGroupMembership.getOrganisationGroup();

      var sortedProjectsDisplayNames = remindableProjectListByOperator.get(organisationGroup.getOrgGrpId())
          .stream()
          .sorted(Comparator.comparing(remindableProject -> remindableProject.getProjectDisplayName().toLowerCase()))
          .map(RemindableProject::getProjectDisplayName)
          .collect(Collectors.toList());

      organisationGroupMembership.getTeamMembers().forEach(teamMember -> {

        verify(testQuarterlyUpdateReminderService, times(1)).getReminderEmailProperties(
            teamMember.getForename(),
            organisationGroup.getName(),
            sortedProjectsDisplayNames
        );

        verify(emailService, times(1)).sendEmail(
            any(),
            eq(teamMember.getEmailAddress()),
            eq(teamMember.getForename())
        );

      });
    });
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
