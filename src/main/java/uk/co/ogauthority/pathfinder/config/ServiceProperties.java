package uk.co.ogauthority.pathfinder.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceProperties {

  private final String serviceName;
  private final String customerMnemonic;
  private final String customerName;
  private final boolean isStackTraceEnabled;

  @Autowired
  public ServiceProperties(@Value("${service.name}") String serviceName,
                           @Value("${service.customer.mnemonic}") String customerMnemonic,
                           @Value("${service.customer.name}") String customerName,
                           @Value("${service.is-stack-trace-enabled}") boolean isStackTraceEnabled) {
    this.serviceName = serviceName;
    this.customerMnemonic = customerMnemonic;
    this.customerName = customerName;
    this.isStackTraceEnabled = isStackTraceEnabled;
  }

  public String getServiceName() {
    return serviceName;
  }

  public String getCustomerMnemonic() {
    return customerMnemonic;
  }

  public String getCustomerName() {
    return customerName;
  }

  public boolean getStackTraceEnabled() {
    return isStackTraceEnabled;
  }
}
