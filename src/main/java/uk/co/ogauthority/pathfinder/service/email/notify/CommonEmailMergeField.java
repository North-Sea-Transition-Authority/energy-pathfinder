package uk.co.ogauthority.pathfinder.service.email.notify;

public class CommonEmailMergeField {

  public static final String TEST_EMAIL = "TEST_EMAIL";
  public static final String SERVICE_NAME = "SERVICE_NAME";
  public static final String CUSTOMER_MNEMONIC = "CUSTOMER_MNEMONIC";
  public static final String SUPPLY_CHAIN_INTERFACE_URL = "SUPPLY_CHAIN_INTERFACE_URL";
  public static final String SERVICE_LOGIN_TEXT = "SERVICE_LOGIN_TEXT";
  public static final String SERVICE_LOGIN_URL = "SERVICE_LOGIN_URL";
  public static final String GREETING_TEXT = "GREETING_TEXT";
  public static final String SIGN_OFF_TEXT = "SIGN_OFF_TEXT";
  public static final String SIGN_OFF_IDENTIFIER = "SIGN_OFF_IDENTIFIER";
  public static final String RECIPIENT_IDENTIFIER = "RECIPIENT_IDENTIFIER";
  public static final String SUBJECT_PREFIX = "SUBJECT_PREFIX";

  private CommonEmailMergeField() {
    throw new IllegalStateException("CommonEmailMergeField is a utility class and should not be instantiated");
  }
}
