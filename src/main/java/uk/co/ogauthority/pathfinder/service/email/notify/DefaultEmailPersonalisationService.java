package uk.co.ogauthority.pathfinder.service.email.notify;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;

@Service
public class DefaultEmailPersonalisationService {

  public static final String DEFAULT_GREETING_TEXT = "Dear";
  public static final String DEFAULT_SIGN_OFF_TEXT = "Kind regards";

  public static final String DEFAULT_SERVICE_LOGIN_TEXT = "To see more details please log in to the service";

  private final ServiceProperties serviceProperties;

  public DefaultEmailPersonalisationService(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
  }

  protected Map<String, Object> getDefaultEmailPersonalisation() {

    final var serviceName = serviceProperties.getServiceName();
    final var customerMnemonic = serviceProperties.getCustomerMnemonic();

    final var emailPersonalisation = new HashMap<String, Object>();
    // TEST_EMAIL set to "no" by default. Services can override this map entry to modify email behaviour
    emailPersonalisation.put(CommonEmailMergeField.TEST_EMAIL, "no");
    emailPersonalisation.put(CommonEmailMergeField.SUBJECT_PREFIX, "");
    emailPersonalisation.put(CommonEmailMergeField.SERVICE_NAME, serviceName);
    emailPersonalisation.put(CommonEmailMergeField.CUSTOMER_MNEMONIC, customerMnemonic);
    emailPersonalisation.put(CommonEmailMergeField.SUPPLY_CHAIN_INTERFACE_URL, serviceProperties.getSupplyChainInterfaceUrl());
    emailPersonalisation.put(CommonEmailMergeField.SERVICE_LOGIN_TEXT, DEFAULT_SERVICE_LOGIN_TEXT);
    emailPersonalisation.put(CommonEmailMergeField.GREETING_TEXT, DEFAULT_GREETING_TEXT);
    emailPersonalisation.put(CommonEmailMergeField.SIGN_OFF_TEXT, DEFAULT_SIGN_OFF_TEXT);
    emailPersonalisation.put(CommonEmailMergeField.SIGN_OFF_IDENTIFIER, getDefaultSignOffIdentifier());
    emailPersonalisation.put(CommonEmailMergeField.RECIPIENT_IDENTIFIER, String.format("%s user", serviceName));
    return emailPersonalisation;
  }

  public String getDefaultSignOffIdentifier() {
    return String.format("%s %s team", serviceProperties.getCustomerMnemonic(), serviceProperties.getServiceName());
  }

  public String getServiceName() {
    return serviceProperties.getServiceName();
  }

  public String getCustomerMnemonic() {
    return serviceProperties.getCustomerMnemonic();
  }
}
