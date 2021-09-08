package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.OrganisationGroupMembership;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationGroupPersonMembershipService;
import uk.co.ogauthority.pathfinder.model.email.EmailAddress;
import uk.co.ogauthority.pathfinder.model.email.EmailRecipient;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;

@Service
class RegulatorUpdateReminderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegulatorUpdateReminderService.class);

  private final List<RegulatorUpdateReminder> regulatorUpdateReminders;

  private final RegulatorUpdateRequestService regulatorUpdateRequestService;

  private final PortalOrganisationGroupPersonMembershipService portalOrganisationGroupPersonMembershipService;

  private final EmailService emailService;

  @Autowired
  RegulatorUpdateReminderService(
      List<RegulatorUpdateReminder> regulatorUpdateReminders,
      RegulatorUpdateRequestService regulatorUpdateRequestService,
      PortalOrganisationGroupPersonMembershipService portalOrganisationGroupPersonMembershipService,
      EmailService emailService) {
    this.regulatorUpdateReminders = regulatorUpdateReminders;
    this.regulatorUpdateRequestService = regulatorUpdateRequestService;
    this.portalOrganisationGroupPersonMembershipService = portalOrganisationGroupPersonMembershipService;
    this.emailService = emailService;
  }

  void processDueReminders() {

    try {

      var projectsWithOutstandingUpdates = regulatorUpdateRequestService
          .getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines();

      var organisationGroupMemberships = getOrganisationGroupMemberships(projectsWithOutstandingUpdates);

      projectsWithOutstandingUpdates.forEach(regulatorUpdateRequestProjectDto -> {

        try {

          determineDueReminder(regulatorUpdateRequestProjectDto.getRegulatorUpdateRequest()).ifPresent(
              regulatorUpdateReminder -> {

                var projectOrganisationGroup = regulatorUpdateRequestProjectDto.getProjectOperator().getOrganisationGroup();

                var organisationGroupTeamMembership = organisationGroupMemberships
                    .stream()
                    .filter(organisationGroupMembership ->
                        organisationGroupMembership.getOrganisationGroup().equals(projectOrganisationGroup)
                    )
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Could not find OrganisationGroupMembership for organisation group with ID %d",
                        projectOrganisationGroup.getOrgGrpId()
                    )));

                processDueReminder(
                    regulatorUpdateReminder,
                    regulatorUpdateRequestProjectDto,
                    organisationGroupTeamMembership
                );
              });

        } catch (Exception ex) {
          LOGGER.error(
              "Failed to process regulator update reminder for project with ID {}",
              regulatorUpdateRequestProjectDto.getRegulatorUpdateRequest().getProjectDetail().getProject().getId(),
              ex
          );
        }
      });
    } catch (Exception ex) {
      LOGGER.error("Failed to process due regulator update request reminders", ex);
    }
  }

  private List<OrganisationGroupMembership> getOrganisationGroupMemberships(
      List<RegulatorUpdateRequestProjectDto> regulatorUpdateRequestProjectDtos
  ) {

    var operatorGroupsWithOutstandingUpdates = regulatorUpdateRequestProjectDtos
        .stream()
        .map(regulatorUpdateRequestProjectDto -> regulatorUpdateRequestProjectDto.getProjectOperator().getOrganisationGroup())
        .distinct()
        .collect(Collectors.toList());

    return portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIn(
        operatorGroupsWithOutstandingUpdates
    );
  }

  private Optional<RegulatorUpdateReminder> determineDueReminder(RegulatorUpdateRequest regulatorUpdateRequest) {
    return regulatorUpdateReminders
        .stream()
        .filter(regulatorUpdateReminder ->
            regulatorUpdateReminder.isReminderDue(regulatorUpdateRequest.getDeadlineDate())
        )
        .findFirst();
  }

  private void processDueReminder(RegulatorUpdateReminder regulatorUpdateReminder,
                                  RegulatorUpdateRequestProjectDto regulatorUpdateRequestProjectDto,
                                  OrganisationGroupMembership organisationGroupMembership) {

    var emailProperties = regulatorUpdateReminder.getEmailReminderProperties(regulatorUpdateRequestProjectDto);

    organisationGroupMembership.getTeamMembers().forEach(teamMember ->
        sendReminderEmailToTeamMember(
            emailProperties,
            teamMember
        )
    );

    sendReminderEmailToAdditionalRecipients(
        regulatorUpdateReminder,
        emailProperties,
        organisationGroupMembership.getOrganisationGroup()
    );
  }

  private void sendReminderEmailToTeamMember(EmailProperties emailProperties, Person teamMember) {
    sendEmail(
        emailProperties,
        new EmailRecipient(new EmailAddress(teamMember.getEmailAddress()), teamMember.getForename())
    );
  }

  private void sendReminderEmailToAdditionalRecipients(RegulatorUpdateReminder regulatorUpdateReminder,
                                                       EmailProperties emailProperties,
                                                       PortalOrganisationGroup portalOrganisationGroup) {
    regulatorUpdateReminder.getAdditionalReminderRecipients().forEach(emailRecipient -> {

      // if no recipient identifier, default to the name of the organisation group we are sending emails too.
      // Otherwise, use the recipient identifier provided
      var emailRecipientWithDefaultsApplied = StringUtils.isBlank(emailRecipient.getRecipientIdentifier())
          ? new EmailRecipient(emailRecipient.getEmailAddress(), portalOrganisationGroup.getName())
          : emailRecipient;

      sendEmail(
          emailProperties,
          emailRecipientWithDefaultsApplied
      );
    });
  }

  private void sendEmail(EmailProperties emailProperties, EmailRecipient emailRecipient) {
    emailService.sendEmail(
        emailProperties,
        emailRecipient.getEmailAddress().getEmailAddressValue(),
        emailRecipient.getRecipientIdentifier()
    );
  }

}
