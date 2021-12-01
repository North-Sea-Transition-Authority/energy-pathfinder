package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.OrganisationGroupMembership;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationGroupPersonMembershipService;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.quarterlystatistics.ReportableProjectService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class QuarterlyUpdateReminderService {

  private final ReportableProjectService reportableProjectService;
  private final PortalOrganisationGroupPersonMembershipService portalOrganisationGroupPersonMembershipService;
  private final EmailService emailService;

  @Autowired
  public QuarterlyUpdateReminderService(
      ReportableProjectService reportableProjectService,
      PortalOrganisationGroupPersonMembershipService portalOrganisationGroupPersonMembershipService,
      EmailService emailService
  ) {
    this.reportableProjectService = reportableProjectService;
    this.portalOrganisationGroupPersonMembershipService = portalOrganisationGroupPersonMembershipService;
    this.emailService = emailService;
  }

  public List<RemindableProject> getAllRemindableProjects() {
    return reportableProjectService.getReportableProjects()
        .stream()
        .map(this::convertToRemindableProject)
        .collect(Collectors.toList());
  }

  public List<RemindableProject> getRemindableProjectsNotUpdatedInCurrentQuarter() {

    var currentQuarter = DateUtil.getCurrentQuarter();

    return reportableProjectService.getReportableProjectsNotUpdatedBetween(
            currentQuarter.getStartDateAsInstant(),
            currentQuarter.getEndDateAsInstant()
        )
        .stream()
        .map(this::convertToRemindableProject)
        .collect(Collectors.toList());
  }

  public void sendQuarterlyProjectUpdateReminderToOperators(QuarterlyUpdateReminder quarterlyUpdateReminder) {

    var remindableProjects = quarterlyUpdateReminder.getRemindableProjects();

    var organisationGroupMemberships = getOrganisationGroupMemberships(remindableProjects);

    var remindableProjectsByOrganisationGroup = remindableProjects
        .stream()
        .collect(Collectors.groupingBy(RemindableProject::getOperatorGroupId, Collectors.toList()));

    organisationGroupMemberships.forEach(organisationGroupMembership -> {

      var organisationGroup = organisationGroupMembership.getOrganisationGroup();

      var organisationGroupRemindableProjects = remindableProjectsByOrganisationGroup.get(organisationGroup.getOrgGrpId());

      sendQuarterlyReminderToTeamMembers(
          quarterlyUpdateReminder,
          organisationGroup,
          organisationGroupRemindableProjects,
          organisationGroupMembership.getTeamMembers()
      );
    });
  }

  private List<OrganisationGroupMembership> getOrganisationGroupMemberships(List<RemindableProject> remindableProjects) {

    var operatorGroupIdsWithRemindableProjects = remindableProjects
        .stream()
        .map(RemindableProject::getOperatorGroupId)
        .distinct()
        .collect(Collectors.toList());

    return portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIdsIn(
        operatorGroupIdsWithRemindableProjects
    );
  }

  private void sendQuarterlyReminderToTeamMembers(QuarterlyUpdateReminder quarterlyUpdateReminder,
                                                  PortalOrganisationGroup organisationGroup,
                                                  List<RemindableProject> operatorRemindableProjects,
                                                  List<Person> teamMembers) {

    var projectsDisplayNames =  operatorRemindableProjects
        .stream()
        .sorted(Comparator.comparing(remindableProject -> remindableProject.getProjectDisplayName().toLowerCase()))
        .map(RemindableProject::getProjectDisplayName)
        .collect(Collectors.toList());

    teamMembers.forEach(teamMember -> {

      var emailProperties = quarterlyUpdateReminder.getReminderEmailProperties(
          teamMember.getForename(),
          organisationGroup.getName(),
          projectsDisplayNames
      );

      emailService.sendEmail(
          emailProperties,
          teamMember.getEmailAddress(),
          teamMember.getForename()
      );
    });
  }

  private RemindableProject convertToRemindableProject(ReportableProject reportableProject) {
    return new RemindableProject(
        reportableProject.getProjectDetailId(),
        reportableProject.getOperatorGroupId(),
        reportableProject.getProjectDisplayName()
    );
  }
}
