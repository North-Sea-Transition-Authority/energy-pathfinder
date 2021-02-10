package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;

public class WellboreTestUtil {

  private static final Integer ID = 1;
  private static final String REGISTRATION_NO = "16/02b-A1";

  private WellboreTestUtil() {
    throw new IllegalStateException("WellboreTestUtil is a utility class and should not be instantiated");
  }

  public static Wellbore createWellbore() {
    return createWellbore(REGISTRATION_NO);
  }

  public static Wellbore createWellbore(String registrationNo) {
    return new Wellbore(ID, registrationNo);
  }
}
