package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.weekbefore.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;

@RunWith(MockitoJUnitRunner.class)
public class WeekBeforeRegulatorDeadlineUpdateCommonEmailPropertyProviderTest {

  @Mock
  private ServiceProperties serviceProperties;

  private WeekBeforeRegulatorDeadlineUpdateCommonEmailPropertyProvider weekBeforeRegulatorDeadlineUpdateCommonEmailPropertyProvider;

  @Before
  public void setup() {
    weekBeforeRegulatorDeadlineUpdateCommonEmailPropertyProvider = new WeekBeforeRegulatorDeadlineUpdateCommonEmailPropertyProvider(
        serviceProperties
    );
  }
  
  @Test
  public void getDefaultIntroductionTextPrefix_verifyResultingText() {

    var customerMnemonic = "customer mnemonic";
    when(serviceProperties.getCustomerMnemonic()).thenReturn(customerMnemonic);

    var resultingText = weekBeforeRegulatorDeadlineUpdateCommonEmailPropertyProvider.getDefaultIntroductionTextPrefix();

    assertThat(resultingText).isEqualTo(
        String.format(
            "This is a reminder that the %s has requested an update",
            serviceProperties.getCustomerMnemonic()
        )
    );
  }

}