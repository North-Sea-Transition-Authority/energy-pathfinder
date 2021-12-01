package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;

@RunWith(MockitoJUnitRunner.class)
public class DayAfterRegulatorUpdateCommonEmailPropertyProviderTest {

  @Mock
  private ServiceProperties serviceProperties;

  private DayAfterRegulatorUpdateCommonEmailPropertyProvider dayAfterRegulatorUpdateCommonEmailPropertyProvider;

  @Before
  public void setup() {
    dayAfterRegulatorUpdateCommonEmailPropertyProvider = new DayAfterRegulatorUpdateCommonEmailPropertyProvider(
        serviceProperties
    );
  }

  @Test
  public void getDefaultIntroductionTextPrefix_assertResult() {

    var customerMnemonic = "customer mnemonic";
    when(serviceProperties.getCustomerMnemonic()).thenReturn(customerMnemonic);

    var resultingText = dayAfterRegulatorUpdateCommonEmailPropertyProvider.getDefaultIntroductionTextPrefix();
    assertThat(resultingText).isEqualTo(
        String.format(
            "This is a final reminder that the %s requested update",
            serviceProperties.getCustomerMnemonic()
        )
    );
  }

}