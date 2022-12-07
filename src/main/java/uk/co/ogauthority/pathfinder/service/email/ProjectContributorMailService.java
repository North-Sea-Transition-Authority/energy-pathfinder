package uk.co.ogauthority.pathfinder.service.email;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.OrganisationGroupMembership;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationGroupPersonMembershipService;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.contributor.AddedProjectContributorEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.contributor.RemovedProjectContributorEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

@Service
public class ProjectContributorMailService {

  private final EmailService emailService;
  private final PortalOrganisationGroupPersonMembershipService portalOrganisationGroupPersonMembershipService;
  private final ProjectInformationService projectInformationService;
  private final ProjectOperatorService projectOperatorService;
  private final LinkService linkService;

  @Autowired
  public ProjectContributorMailService(EmailService emailService,
                                       PortalOrganisationGroupPersonMembershipService portalOrganisationGroupPersonMembershipService,
                                       ProjectInformationService projectInformationService,
                                       ProjectOperatorService projectOperatorService,
                                       LinkService linkService) {
    this.emailService = emailService;
    this.portalOrganisationGroupPersonMembershipService = portalOrganisationGroupPersonMembershipService;
    this.projectInformationService = projectInformationService;
    this.projectOperatorService = projectOperatorService;
    this.linkService = linkService;
  }

  public void sendContributorsRemovedEmail(List<ProjectContributor> projectContributors, ProjectDetail projectDetail) {
    var projectTitle = projectInformationService.getProjectTitle(projectDetail);
    var projectOperator = getProjectOperator(projectDetail);
    var personList = getListOfRecipientPersons(projectContributors);
    personList.forEach(person ->
        sendContributorRemovedEmail(
            person.getEmailAddress(),
            person.getFullName(),
            projectDetail,
            projectOperator,
            projectTitle
        )
    );
  }

  public void sendContributorsAddedEmail(List<ProjectContributor> projectContributors, ProjectDetail projectDetail) {
    var projectTitle = projectInformationService.getProjectTitle(projectDetail);
    var projectOperator = getProjectOperator(projectDetail);
    var personList = getListOfRecipientPersons(projectContributors);
    personList.forEach(person ->
        sendContributorAddedEmail(
            person.getEmailAddress(),
            person.getFullName(),
            projectDetail,
            projectOperator,
            projectTitle
        )
    );
  }

  private void sendContributorRemovedEmail(String emailAddress,
                                           String recipientName,
                                           ProjectDetail detail,
                                           ProjectOperator projectOperator,
                                           String projectTitle) {
    var emailProperties = new RemovedProjectContributorEmailProperties(
        recipientName,
        detail,
        projectOperator,
        projectTitle
    );
    emailService.sendEmail(emailProperties, emailAddress);
  }

  private void sendContributorAddedEmail(String emailAddress,
                                         String recipientName,
                                         ProjectDetail detail,
                                         ProjectOperator projectOperator,
                                         String projectTitle) {
    var emailProperties = new AddedProjectContributorEmailProperties(
        recipientName,
        detail,
        linkService.getWorkAreaUrl(),
        projectOperator,
        projectTitle
    );
    emailService.sendEmail(emailProperties, emailAddress);
  }

  private List<Person> getListOfRecipientPersons(List<ProjectContributor> projectContributors) {
    List<PortalOrganisationGroup> organisationGroups = projectContributors
        .stream()
        .map(ProjectContributor::getContributionOrganisationGroup)
        .distinct()
        .collect(Collectors.toList());
    return portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIn(organisationGroups)
        .stream()
        .map(OrganisationGroupMembership::getTeamMembers)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private ProjectOperator getProjectOperator(ProjectDetail projectDetail) {
    return projectOperatorService.getProjectOperatorByProjectDetail(projectDetail)
        .orElse(new ProjectOperator());
  }
}
