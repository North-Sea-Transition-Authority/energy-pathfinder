package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderConversionForm;

public class UpcomingTenderConversionUtil {

  public static final String CONTRACTOR_NAME = "My first contractor";
  public static final LocalDate DATE_AWARDED = LocalDate.now();

  private UpcomingTenderConversionUtil() {
    throw new IllegalStateException("UpcomingTenderConversionUtil is a utility class and should not be instantiated");
  }

  public static UpcomingTenderConversionForm createUpcomingTenderConversionForm() {
    var form = new UpcomingTenderConversionForm();
    form.setDateAwarded(new ThreeFieldDateInput(DATE_AWARDED));
    form.setContractorName(CONTRACTOR_NAME);
    return form;
  }
}
