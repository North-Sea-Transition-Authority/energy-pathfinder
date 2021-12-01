package uk.co.ogauthority.pathfinder.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification.NoUpdateNotificationEmailPropertyService;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted.UpdateSubmittedEmailPropertyService;

@Service
public class RegulatorEmailService {

  private final EmailService emailService;
  private final String regulatorSharedEmail;
  private final NoUpdateNotificationEmailPropertyService noUpdateNotificationEmailPropertyService;
  private final UpdateSubmittedEmailPropertyService updateSubmittedEmailPropertyService;

  @Autowired
  public RegulatorEmailService(EmailService emailService,
                               @Value("${regulator.shared.email}") String regulatorSharedEmail,
                               NoUpdateNotificationEmailPropertyService noUpdateNotificationEmailPropertyService,
                               UpdateSubmittedEmailPropertyService updateSubmittedEmailPropertyService) {
    this.emailService = emailService;
    this.regulatorSharedEmail = regulatorSharedEmail;
    this.noUpdateNotificationEmailPropertyService = noUpdateNotificationEmailPropertyService;
    this.updateSubmittedEmailPropertyService = updateSubmittedEmailPropertyService;
  }


  public String getRegulatorSharedMailboxAddress() {
    return regulatorSharedEmail;
  }

  public void sendUpdateSubmitConfirmationEmail(ProjectDetail detail) {
    final var emailProperties = updateSubmittedEmailPropertyService.getUpdateSubmittedEmailProperties(detail);
    sendEmailToRegulatorSharedMailbox(emailProperties);
  }

  public void sendNoUpdateNotificationEmail(ProjectDetail projectDetail, String noUpdateReason) {
    final var emailProperties = noUpdateNotificationEmailPropertyService.getNoUpdateNotificationEmailProperties(
        projectDetail,
        noUpdateReason
    );
    sendEmailToRegulatorSharedMailbox(emailProperties);
  }

  private void sendEmailToRegulatorSharedMailbox(EmailProperties emailProperties) {
    emailService.sendEmail(emailProperties, regulatorSharedEmail);
  }

}
