package uk.co.ogauthority.pathfinder.energyportal.usercontext;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import uk.co.fivium.energyportal.starter.usercontext.EnergyPortalUserServicesContextProvider;
import uk.co.fivium.energyportal.starter.usercontext.UserContextV1;
import uk.co.fivium.energyportal.starter.usercontext.VersionedUserContext;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.team.OrganisationRole;
import uk.co.ogauthority.pathfinder.model.team.RegulatorRole;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.QuarterlyUpdateReminderService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Component
class PathfinderEnergyPortalUserServicesContextProvider implements EnergyPortalUserServicesContextProvider {

  private final WebUserAccountService webUserAccountService;
  private final TeamService teamService;
  private final QuarterlyUpdateReminderService quarterlyUpdateReminderService;
  private final UpcomingTenderService upcomingTenderService;
  private final RegulatorUpdateRequestService regulatorUpdateRequestService;
  private final ProjectService projectService;
  private final ProjectAssessmentService projectAssessmentService;
  private final Clock clock;

  PathfinderEnergyPortalUserServicesContextProvider(
      WebUserAccountService webUserAccountService,
      TeamService teamService,
      QuarterlyUpdateReminderService quarterlyUpdateReminderService,
      UpcomingTenderService upcomingTenderService,
      RegulatorUpdateRequestService regulatorUpdateRequestService,
      ProjectService projectService,
      ProjectAssessmentService projectAssessmentService,
      Clock clock
  ) {
    this.webUserAccountService = webUserAccountService;
    this.teamService = teamService;
    this.quarterlyUpdateReminderService = quarterlyUpdateReminderService;
    this.upcomingTenderService = upcomingTenderService;
    this.regulatorUpdateRequestService = regulatorUpdateRequestService;
    this.projectService = projectService;
    this.projectAssessmentService = projectAssessmentService;
    this.clock = clock;
  }

  @Override
  public VersionedUserContext getUserContext(long wuaId) {
    var webUserAccount = webUserAccountService.getWebUserAccountOrError(Math.toIntExact(wuaId));
    var person = webUserAccount.getLinkedPerson();
    var organisationTeams =
        teamService.getOrganisationTeamListIfPersonInRole(person, List.of(OrganisationRole.PROJECT_SUBMITTER));
    var userContextBuilder = VersionedUserContext.newBuilder().v1();
    var now = LocalDate.now(clock);

    if (!organisationTeams.isEmpty()) {
      var portalOrganisationGroupIds = organisationTeams.stream()
          .map(organisationTeam -> organisationTeam.getPortalOrganisationGroup().getOrgGrpId())
          .toList();

      var numQuarterlyUpdates = quarterlyUpdateReminderService.getRemindableProjectsNotUpdatedInCurrentQuarter().stream()
          .filter(remindableProject -> portalOrganisationGroupIds.contains(remindableProject.getOperatorGroupId()))
          .count();

      addQuarterlyUpdatesRequired(numQuarterlyUpdates, userContextBuilder);

      var numPastUpcomingTenders =
          upcomingTenderService.getPastUpcomingTendersForOrganisationGroupsIn(portalOrganisationGroupIds).size();

      addPastUpcomingTenders(numPastUpcomingTenders, userContextBuilder);

      var numRegulatorUpdatesByOverdue =
          regulatorUpdateRequestService.getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines()
              .stream()
              .filter(regulatorUpdateRequestProjectDto -> portalOrganisationGroupIds.contains(
                  regulatorUpdateRequestProjectDto.getProjectOperator().getOrganisationGroup().getOrgGrpId()
              )).collect(Collectors.partitioningBy(
                  regulatorUpdateRequestProjectDto -> regulatorUpdateRequestProjectDto
                      .getRegulatorUpdateRequest()
                      .getDeadlineDate()
                      .isBefore(now),
                  Collectors.counting()
              ));

      addRegulatorUpdatesRequestedAndOverdue(
          numRegulatorUpdatesByOverdue.get(false),
          numRegulatorUpdatesByOverdue.get(true),
          userContextBuilder
      );
    }

    var regulatorTeam = teamService.getRegulatorTeamIfPersonInRole(person, List.of(RegulatorRole.PROJECT_ADMINISTRATOR));

    if (regulatorTeam.isPresent()) {
      var projectsInQA = projectService.findAllLatestDetailByStatus(ProjectStatus.QA);
      var numAwaitingAssessment = projectsInQA.size() - projectAssessmentService.countByProjectDetailIn(projectsInQA);

      addProjectsAwaitingAssessment(numAwaitingAssessment, userContextBuilder);
    }

    return userContextBuilder.build();
  }

  private void addQuarterlyUpdatesRequired(long numQuarterlyUpdates, UserContextV1.Builder userContextBuilder) {
    if (numQuarterlyUpdates > 0) {
      userContextBuilder.low(
          Math.toIntExact(numQuarterlyUpdates),
          "quarterly update%s required".formatted(numQuarterlyUpdates == 1 ? "" : "s")
      );
    }
  }

  private void addPastUpcomingTenders(long numPastUpcomingTenders, UserContextV1.Builder userContextBuilder) {
    if (numPastUpcomingTenders > 0) {
      userContextBuilder.high(
          Math.toIntExact(numPastUpcomingTenders),
          "upcoming tender date%s in the past".formatted(numPastUpcomingTenders == 1 ? "" : "s")
      );
    }
  }

  private void addRegulatorUpdatesRequestedAndOverdue(
      long numRegulatorUpdatesRequested,
      long numUpdatesOverdue,
      UserContextV1.Builder userContextBuilder
  ) {
    if (numRegulatorUpdatesRequested > 0) {
      userContextBuilder.low(
          Math.toIntExact(numRegulatorUpdatesRequested),
          "update%s requested".formatted(numRegulatorUpdatesRequested == 1 ? "" : "s")
      );
    }
    if (numUpdatesOverdue > 0) {
      userContextBuilder.high(
          Math.toIntExact(numUpdatesOverdue),
          "update%s overdue".formatted(numUpdatesOverdue == 1 ? "" : "s")
      );
    }
  }

  private void addProjectsAwaitingAssessment(long numAwaitingAssessment, UserContextV1.Builder userContextBuilder) {
    if (numAwaitingAssessment > 0) {
      userContextBuilder.low(
          Math.toIntExact(numAwaitingAssessment),
          "awaiting assessment"
      );
    }
  }
}