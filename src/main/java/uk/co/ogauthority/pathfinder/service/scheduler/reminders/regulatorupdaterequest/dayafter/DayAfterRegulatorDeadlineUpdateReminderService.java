package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.dayafter.DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminder;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminderEmailPropertiesService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateRequestProjectDto;

@Service
class DayAfterRegulatorDeadlineUpdateReminderService implements RegulatorUpdateReminder {

  private final List<DayAfterRegulatorDeadlineUpdateEmailPropertyProvider> dayAfterRegulatorDeadlineUpdateEmailPropertyProviders;

  private final RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService;

  @Autowired
  DayAfterRegulatorDeadlineUpdateReminderService(
      List<DayAfterRegulatorDeadlineUpdateEmailPropertyProvider> dayAfterRegulatorDeadlineUpdateEmailPropertyProviders,
      RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService
  ) {
    this.dayAfterRegulatorDeadlineUpdateEmailPropertyProviders = dayAfterRegulatorDeadlineUpdateEmailPropertyProviders;
    this.regulatorUpdateReminderEmailPropertiesService = regulatorUpdateReminderEmailPropertiesService;
  }

  @Override
  public boolean isReminderDue(LocalDate updateDeadlineDate) {
    return LocalDate.now().minusDays(1).equals(updateDeadlineDate);
  }

  @Override
  public EmailProperties getEmailReminderProperties(RegulatorUpdateRequestProjectDto regulatorUpdateRequestProjectDto) {

    var regulatorUpdateRequest = regulatorUpdateRequestProjectDto.getRegulatorUpdateRequest();

    var emailPropertyProvider = dayAfterRegulatorDeadlineUpdateEmailPropertyProviders
        .stream()
        .filter(provider -> provider.getSupportedProjectType().equals(regulatorUpdateRequest.getProjectDetail().getProjectType()))
        .findFirst();

    // if a project type specific provider is found, then use specific email properties
    if (emailPropertyProvider.isPresent()) {
      return emailPropertyProvider.get().getEmailProperties(regulatorUpdateRequest);
    } else {
      // otherwise, fall back to the default email properties
      return new DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties(
          regulatorUpdateRequest.getUpdateReason(),
          regulatorUpdateReminderEmailPropertiesService.getProjectManagementUrl(regulatorUpdateRequest)
      );
    }
  }
}
