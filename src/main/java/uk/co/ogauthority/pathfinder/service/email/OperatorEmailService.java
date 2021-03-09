package uk.co.ogauthority.pathfinder.service.email;

import java.time.LocalDate;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.IncomingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.OutgoingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.ProjectUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class OperatorEmailService {

  private final EmailService emailService;
  private final EmailLinkService emailLinkService;
  private final ProjectInformationService projectInformationService;
  private final ProjectOperatorService projectOperatorService;
  private final PortalTeamAccessor portalTeamAccessor;

  @Autowired
  public OperatorEmailService(
      EmailService emailService,
      EmailLinkService emailLinkService,
      ProjectInformationService projectInformationService,
      ProjectOperatorService projectOperatorService,
      PortalTeamAccessor portalTeamAccessor
  ) {
    this.emailService = emailService;
    this.emailLinkService = emailLinkService;
    this.projectInformationService = projectInformationService;
    this.projectOperatorService = projectOperatorService;
    this.portalTeamAccessor = portalTeamAccessor;
  }

  public void sendUpdateRequestedEmail(ProjectDetail projectDetail, String updateReason, LocalDate deadlineDate) {
    var projectTitle = projectInformationService.getProjectTitle(projectDetail);
    var formattedDeadlineDate = DateUtil.formatDate(deadlineDate);
    var projectUrl = emailLinkService.generateProjectManagementUrl(projectDetail.getProject());

    var teamMemberPeople = getOrganisationGroupPeople(projectDetail);

    teamMemberPeople.forEach(person -> {
      var emailProperties = new ProjectUpdateRequestedEmailProperties(
          person.getForename(),
          projectTitle,
          updateReason,
          formattedDeadlineDate,
          projectUrl
      );
      emailService.sendEmail(emailProperties, person.getEmailAddress());
    });
  }

  public void sendProjectTransferEmails(ProjectDetail projectDetail,
                                        PortalOrganisationGroup fromOrganisationGroup,
                                        PortalOrganisationGroup toOrganisationGroup,
                                        String transferReason) {
    var projectTitle = projectInformationService.getProjectTitle(projectDetail);
    var projectUrl = emailLinkService.generateProjectManagementUrl(projectDetail.getProject());

    var fromOrganisationGroupName = fromOrganisationGroup.getName();
    var toOrganisationGroupName = toOrganisationGroup.getName();

    var emailTransferReason = StringUtils.removeEnd(transferReason, ".");

    var fromTeamMemberPeople = getOrganisationGroupPeople(fromOrganisationGroup);
    fromTeamMemberPeople.forEach(person -> {
      var transferredFromEmailProperties = new OutgoingOperatorProjectTransferEmailProperties(
          person.getForename(),
          projectTitle,
          emailTransferReason,
          toOrganisationGroupName
      );
      emailService.sendEmail(transferredFromEmailProperties, person.getEmailAddress());
    });

    var toTeamMemberPeople = getOrganisationGroupPeople(toOrganisationGroup);
    toTeamMemberPeople.forEach(person -> {
      var transferredToEmailProperties = new IncomingOperatorProjectTransferEmailProperties(
          person.getForename(),
          projectTitle,
          emailTransferReason,
          fromOrganisationGroupName,
          projectUrl
      );
      emailService.sendEmail(transferredToEmailProperties, person.getEmailAddress());
    });
  }

  private List<Person> getOrganisationGroupPeople(ProjectDetail projectDetail) {
    var projectOperator = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail);
    return getOrganisationGroupPeople(projectOperator.getOrganisationGroup());
  }

  private List<Person> getOrganisationGroupPeople(PortalOrganisationGroup organisationGroup) {
    var portalTeamDto = portalTeamAccessor.findPortalTeamByOrganisationGroup(organisationGroup)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Unable to find portal team for organisation group with org grp id %s", organisationGroup.getOrgGrpId())));
    return portalTeamAccessor.getPortalTeamMemberPeople(List.of(portalTeamDto.getResId()));
  }
}
