package uk.co.ogauthority.pathfinder.testutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;
import uk.co.ogauthority.pathfinder.model.view.wellbore.WellboreView;

public class WellboreTestUtil {

  private static final Integer ID = 1;
  private static final String REGISTRATION_NO = "16/02b-A1";
  public static final String QUADRANT_NUMBER = "16";
  public static final String BLOCK_NUMBER = "02";
  public static final String BLOCK_SUFFIX = "b";
  public static final String PLATFORM_LETTER = "A";
  public static final String DRILLING_SEQ_NUMBER = "1";
  public static final String WELL_SUFFIX = "";

  private WellboreTestUtil() {
    throw new IllegalStateException("WellboreTestUtil is a utility class and should not be instantiated");
  }

  public static List<Wellbore> getUnorderedWellbores() {
    var unorderedWellbores = new ArrayList<>(getOrderedWellbores());
    Collections.shuffle(unorderedWellbores);
    return unorderedWellbores;
  }

  public static List<Wellbore> getOrderedWellbores() {
    return List.of(
        createWellbore("20/01- 1", "20", "01", "", "", "1", ""),
        createWellbore("22/01- 1", "22", "01", "", "", "1", ""),

        createWellbore("24/01- 1", "24", "01", "", "", "1", ""),
        createWellbore("24/02- 1", "24", "02", "", "", "1", ""),

        createWellbore("26/01a- 1", "26", "01", "a", "", "1", ""),
        createWellbore("26/01b- 1", "26", "01", "b", "", "1", ""),

        createWellbore("28/01-L1", "28", "01", "", "L", "1", ""),
        createWellbore("28/01-M1", "28", "01", "", "M", "1", ""),

        createWellbore("30/01- 1", "30", "01", "", "", "1", ""),
        createWellbore("30/01- 2", "30", "01", "", "", "2", ""),

        createWellbore("32/01- 1W", "32", "01", "", "", "1", "W"),
        createWellbore("32/01- 1Y", "32", "01", "", "", "1", "Y")
    );
  }

  public static Wellbore createWellbore() {
    return createWellbore(
        REGISTRATION_NO,
        QUADRANT_NUMBER,
        BLOCK_NUMBER,
        BLOCK_SUFFIX,
        PLATFORM_LETTER,
        DRILLING_SEQ_NUMBER,
        WELL_SUFFIX
    );
  }

  public static Wellbore createWellbore(String registrationNo,
                                        String quadrantNumber,
                                        String blockNumber,
                                        String blockSuffix,
                                        String platformLetter,
                                        String drillingSeqNumber,
                                        String wellSuffix) {
    return new Wellbore(
        ID,
        registrationNo,
        quadrantNumber,
        blockNumber,
        blockSuffix,
        platformLetter,
        drillingSeqNumber,
        wellSuffix
    );
  }

  public static WellboreView createWellboreView() {
    return new WellboreView(createWellbore(), true);
  }
}
