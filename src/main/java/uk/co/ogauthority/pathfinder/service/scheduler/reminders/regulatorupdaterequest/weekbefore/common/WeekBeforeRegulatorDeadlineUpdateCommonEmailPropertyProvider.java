package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.weekbefore.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;

@Service
public class WeekBeforeRegulatorDeadlineUpdateCommonEmailPropertyProvider {

  private final ServiceProperties serviceProperties;

  @Autowired
  public WeekBeforeRegulatorDeadlineUpdateCommonEmailPropertyProvider(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
  }

  public String getDefaultIntroductionTextPrefix() {
    return String.format(
        "This is a reminder that the %s has requested an update",
        serviceProperties.getCustomerMnemonic()
    );
  }
}
