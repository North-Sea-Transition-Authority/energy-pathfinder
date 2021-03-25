package uk.co.ogauthority.pathfinder.testutil;

import java.util.HashMap;
import java.util.Map;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;

public class EmailPropertyTestUtil {

  public static Map<String, String> getDefaultEmailPersonalisation(String recipientIdentifier,
                                                                   String signOffIdentifier) {
    var emailPersonalisation = new HashMap<String, String>();
    emailPersonalisation.put("TEST_EMAIL", "no");
    emailPersonalisation.put("GREETING_TEXT", EmailProperties.DEFAULT_GREETING_TEXT);
    emailPersonalisation.put("RECIPIENT_IDENTIFIER", recipientIdentifier);
    emailPersonalisation.put("SIGN_OFF_TEXT", EmailProperties.DEFAULT_SIGN_OFF_TEXT);
    emailPersonalisation.put("SIGN_OFF_IDENTIFIER", signOffIdentifier);
    return emailPersonalisation;
  }
}
