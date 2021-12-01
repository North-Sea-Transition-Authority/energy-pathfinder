package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;

@Service
class TestRegulatorUpdateReminderService implements RegulatorUpdateReminder {

  @Override
  public boolean isReminderDue(LocalDate updateDeadlineDate) {
    return false;
  }

  @Override
  public EmailProperties getEmailReminderProperties(RegulatorUpdateRequestProjectDto regulatorUpdateRequestProjectDto) {
    return null;
  }
}
