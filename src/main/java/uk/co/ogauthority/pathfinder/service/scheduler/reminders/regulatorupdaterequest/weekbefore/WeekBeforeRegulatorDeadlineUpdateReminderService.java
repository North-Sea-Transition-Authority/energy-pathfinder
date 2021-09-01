package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.weekbefore;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.weekbefore.WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminder;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminderEmailPropertiesService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateRequestProjectDto;

@Service
class WeekBeforeRegulatorDeadlineUpdateReminderService implements RegulatorUpdateReminder {

  private final List<WeekBeforeRegulatorDeadlineUpdatedEmailPropertyProvider> weekBeforeRegulatorDeadlineUpdatedEmailPropertyProviders;

  private final RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService;

  @Autowired
  WeekBeforeRegulatorDeadlineUpdateReminderService(
      List<WeekBeforeRegulatorDeadlineUpdatedEmailPropertyProvider> weekBeforeRegulatorDeadlineUpdatedEmailPropertyProviders,
      RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService
  ) {
    this.weekBeforeRegulatorDeadlineUpdatedEmailPropertyProviders = weekBeforeRegulatorDeadlineUpdatedEmailPropertyProviders;
    this.regulatorUpdateReminderEmailPropertiesService = regulatorUpdateReminderEmailPropertiesService;
  }

  @Override
  public boolean isReminderDue(LocalDate updateDeadlineDate) {
    return updateDeadlineDate.minus(7, ChronoUnit.DAYS).equals(LocalDate.now());
  }

  @Override
  public EmailProperties getEmailReminderProperties(RegulatorUpdateRequestProjectDto regulatorUpdateRequestProjectDto) {

    var regulatorUpdateRequest = regulatorUpdateRequestProjectDto.getRegulatorUpdateRequest();

    var emailPropertyProvider = weekBeforeRegulatorDeadlineUpdatedEmailPropertyProviders
        .stream()
        .filter(provider -> provider.getSupportedProjectType().equals(regulatorUpdateRequest.getProjectDetail().getProjectType()))
        .findFirst();

    // if a project type specific provider is found, then use specific email properties
    if (emailPropertyProvider.isPresent()) {
      return emailPropertyProvider.get().getEmailProperties(regulatorUpdateRequest);
    } else {
      // otherwise, fall back to the default email properties
      return new WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties(
          regulatorUpdateReminderEmailPropertiesService.getFormattedDeadlineDate(regulatorUpdateRequest.getDeadlineDate()),
          regulatorUpdateRequest.getUpdateReason(),
          regulatorUpdateReminderEmailPropertiesService.getProjectManagementUrl(regulatorUpdateRequest)
      );
    }
  }
}
