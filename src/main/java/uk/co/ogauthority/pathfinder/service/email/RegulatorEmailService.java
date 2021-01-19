package uk.co.ogauthority.pathfinder.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.NoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

@Service
public class RegulatorEmailService {

  private final EmailService emailService;
  private final EmailLinkService emailLinkService;
  private final ProjectInformationService projectInformationService;
  private final String regulatorSharedEmail;

  @Autowired
  public RegulatorEmailService(EmailService emailService,
                               EmailLinkService emailLinkService,
                               ProjectInformationService projectInformationService,
                               @Value("${regulator.shared.email}") String regulatorSharedEmail) {
    this.emailService = emailService;
    this.emailLinkService = emailLinkService;
    this.projectInformationService = projectInformationService;
    this.regulatorSharedEmail = regulatorSharedEmail;
  }


  public String getRegulatorSharedMailboxAddress() {
    return regulatorSharedEmail;
  }

  public void sendUpdateSubmitConfirmationEmail(ProjectDetail detail) {
    var emailProps = new ProjectUpdateEmailProperties(
        projectInformationService.getProjectTitle(detail),
        emailLinkService.getWorkAreaUrl()
    );
    sendEmailToRegulatorSharedMailbox(emailProps);
  }

  public void sendNoUpdateNotificationEmail(ProjectDetail detail, String noUpdateReason) {
    var emailProps = new NoUpdateNotificationEmailProperties(
        projectInformationService.getProjectTitle(detail),
        emailLinkService.getWorkAreaUrl(),
        noUpdateReason
    );
    sendEmailToRegulatorSharedMailbox(emailProps);
  }


  private void sendEmailToRegulatorSharedMailbox(EmailProperties emailProperties) {
    emailService.sendEmail(emailProperties, regulatorSharedEmail);
  }

}
