package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class RegulatorUpdateReminderEmailPropertiesService {

  private final EmailLinkService emailLinkService;

  @Autowired
  public RegulatorUpdateReminderEmailPropertiesService(EmailLinkService emailLinkService) {
    this.emailLinkService = emailLinkService;
  }

  public String getFormattedDeadlineDate(LocalDate deadlineDate) {
    return DateUtil.formatDate(deadlineDate);
  }

  public String getProjectManagementUrl(RegulatorUpdateRequest regulatorUpdateRequest) {
    return emailLinkService.generateProjectManagementUrl(
        regulatorUpdateRequest.getProjectDetail().getProject()
    );
  }
}
