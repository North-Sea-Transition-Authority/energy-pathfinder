package uk.co.ogauthority.pathfinder.service.email.notify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEmailPersonalisationServiceTest {

  @Mock
  private ServiceProperties serviceProperties;

  private static final String SERVICE_NAME = "service name";
  private static final String CUSTOMER_MNEMONIC = "customer mnemonic";
  private static final String SUPPLY_CHAIN_URL = "https://url.com";

  private DefaultEmailPersonalisationService defaultEmailPersonalisationService;

  @Before
  public void setup() {
    defaultEmailPersonalisationService = new DefaultEmailPersonalisationService(serviceProperties);

    when(serviceProperties.getServiceName()).thenReturn(SERVICE_NAME);

    when(serviceProperties.getCustomerMnemonic()).thenReturn(CUSTOMER_MNEMONIC);

    when(serviceProperties.getSupplyChainInterfaceUrl()).thenReturn(SUPPLY_CHAIN_URL);
  }

  @Test
  public void getDefaultEmailPersonalisation_assertPropertiesReturned() {

    final var defaultPersonalisation = defaultEmailPersonalisationService.getDefaultEmailPersonalisation();

    final var serviceName = SERVICE_NAME;
    final var customerMnemonic = CUSTOMER_MNEMONIC;

    assertThat(defaultPersonalisation).containsExactlyInAnyOrderEntriesOf(
        Map.of(
            CommonEmailMergeField.TEST_EMAIL, "no",
            CommonEmailMergeField.SUBJECT_PREFIX, "",
            CommonEmailMergeField.SERVICE_NAME, serviceName,
            CommonEmailMergeField.CUSTOMER_MNEMONIC, customerMnemonic,
            CommonEmailMergeField.SUPPLY_CHAIN_INTERFACE_URL, SUPPLY_CHAIN_URL,
            CommonEmailMergeField.SERVICE_LOGIN_TEXT, DefaultEmailPersonalisationService.DEFAULT_SERVICE_LOGIN_TEXT,
            CommonEmailMergeField.GREETING_TEXT, DefaultEmailPersonalisationService.DEFAULT_GREETING_TEXT,
            CommonEmailMergeField.SIGN_OFF_TEXT, DefaultEmailPersonalisationService.DEFAULT_SIGN_OFF_TEXT,
            CommonEmailMergeField.SIGN_OFF_IDENTIFIER, String.format("%s %s team", customerMnemonic, serviceName),
            CommonEmailMergeField.RECIPIENT_IDENTIFIER, String.format("%s user", serviceName)
        )
    );
  }

  @Test
  public void getDefaultSignOffIdentifier_assertCorrectReturnFormat() {
    final var expectedSignOffIdentifier = String.format("%s %s team", CUSTOMER_MNEMONIC, SERVICE_NAME);
    final var returnedSignOffIdentifier = defaultEmailPersonalisationService.getDefaultSignOffIdentifier();
    assertThat(returnedSignOffIdentifier).isEqualTo(expectedSignOffIdentifier);
  }

  @Test
  public void getServiceName_assertExpectedReturnValue() {
    final var returnedServiceName = defaultEmailPersonalisationService.getServiceName();
    assertThat(returnedServiceName).isEqualTo(SERVICE_NAME);
  }

  @Test
  public void getCustomerMnemonic_assertExpectedReturnValue() {
    final var returnedCustomerMnemonic = defaultEmailPersonalisationService.getCustomerMnemonic();
    assertThat(returnedCustomerMnemonic).isEqualTo(CUSTOMER_MNEMONIC);
  }
}