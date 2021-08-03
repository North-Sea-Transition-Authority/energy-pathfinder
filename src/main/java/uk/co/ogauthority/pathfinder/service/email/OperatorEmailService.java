package uk.co.ogauthority.pathfinder.service.email;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.email.projecttransfer.ProjectTransferEmailPropertyService;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested.UpdateRequestedEmailPropertyService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class OperatorEmailService {

  private final EmailService emailService;
  private final ProjectOperatorService projectOperatorService;
  private final PortalTeamAccessor portalTeamAccessor;
  private final UpdateRequestedEmailPropertyService updateRequestedEmailPropertyService;
  private final ProjectTransferEmailPropertyService projectTransferEmailPropertyService;

  @Autowired
  public OperatorEmailService(
      EmailService emailService,
      ProjectOperatorService projectOperatorService,
      PortalTeamAccessor portalTeamAccessor,
      UpdateRequestedEmailPropertyService updateRequestedEmailPropertyService,
      ProjectTransferEmailPropertyService projectTransferEmailPropertyService
  ) {
    this.emailService = emailService;
    this.projectOperatorService = projectOperatorService;
    this.portalTeamAccessor = portalTeamAccessor;
    this.updateRequestedEmailPropertyService = updateRequestedEmailPropertyService;
    this.projectTransferEmailPropertyService = projectTransferEmailPropertyService;
  }

  public void sendUpdateRequestedEmail(ProjectDetail projectDetail, String updateReason, LocalDate deadlineDate) {

    var formattedDeadlineDate = DateUtil.formatDate(deadlineDate);

    final var updateRequestedEmailProperties = updateRequestedEmailPropertyService.getUpdateRequestedEmailProperties(
        projectDetail,
        updateReason,
        formattedDeadlineDate
    );

    var teamMemberPeople = getOrganisationGroupPeople(projectDetail);

    teamMemberPeople.forEach(person ->
        emailService.sendEmail(updateRequestedEmailProperties, person.getEmailAddress(), person.getForename())
    );
  }

  public void sendProjectTransferEmails(ProjectDetail projectDetail,
                                        PortalOrganisationGroup fromOrganisationGroup,
                                        PortalOrganisationGroup toOrganisationGroup,
                                        String transferReason) {

    final var fromOrganisationGroupName = fromOrganisationGroup.getName();
    final var toOrganisationGroupName = toOrganisationGroup.getName();

    final var outgoingOperatorTransferEmailProperties = projectTransferEmailPropertyService
        .getOutgoingOperatorProjectTransferEmailProperties(
            projectDetail,
            transferReason,
            toOrganisationGroupName
        );

    final var fromTeamMemberPeople = getOrganisationGroupPeople(fromOrganisationGroup);

    fromTeamMemberPeople.forEach(person ->
        emailService.sendEmail(outgoingOperatorTransferEmailProperties, person.getEmailAddress(), person.getForename())
    );

    final var incomingOperatorTransferEmailProperties = projectTransferEmailPropertyService
        .getIncomingOperatorProjectTransferEmailProperties(
            projectDetail,
            transferReason,
            fromOrganisationGroupName
        );

    final var toTeamMemberPeople = getOrganisationGroupPeople(toOrganisationGroup);

    toTeamMemberPeople.forEach(person ->
        emailService.sendEmail(incomingOperatorTransferEmailProperties, person.getEmailAddress(), person.getForename())
    );
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
