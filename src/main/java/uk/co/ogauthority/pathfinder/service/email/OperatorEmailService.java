package uk.co.ogauthority.pathfinder.service.email;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
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
    var teamMemberPeople = getOrganisationGroupPeople(projectDetail);

    teamMemberPeople.forEach(person -> {
      var emailProperties = new ProjectUpdateRequestedEmailProperties(
          person.getForename(),
          projectInformationService.getProjectTitle(projectDetail),
          updateReason,
          DateUtil.formatDate(deadlineDate),
          emailLinkService.generateProjectManagementUrl(projectDetail.getProject())
      );
      emailService.sendEmail(emailProperties, person.getEmailAddress());
    });
  }

  private List<Person> getOrganisationGroupPeople(ProjectDetail projectDetail) {
    var projectOperator = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail);
    var organisationGroup = projectOperator.getOrganisationGroup();
    var portalTeamDto = portalTeamAccessor.findPortalTeamByOrganisationGroup(organisationGroup)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Unable to find portal team for organisation group with org grp id %s", organisationGroup.getOrgGrpId())));
    return portalTeamAccessor.getPortalTeamMemberPeople(List.of(portalTeamDto.getResId()));
  }
}
