package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;

@Service
public class DayAfterRegulatorUpdateCommonEmailPropertyProvider {

  private final ServiceProperties serviceProperties;

  @Autowired
  public DayAfterRegulatorUpdateCommonEmailPropertyProvider(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
  }

  public String getDefaultIntroductionTextPrefix() {
    return String.format(
        "This is a final reminder that the %s requested update",
        serviceProperties.getCustomerMnemonic()
    );
  }
}
