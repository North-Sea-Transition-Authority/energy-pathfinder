package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderConversionForm;

public class UpcomingTenderConversionUtil {

  public static final String CONTRACTOR_NAME = "My first contractor";
  public static final LocalDate DATE_AWARDED = LocalDate.now();
  public static final String CONTACT_NAME = "John Smith";
  public static final String PHONE_NUMBER = "020 1234 1234";
  public static final String JOB_TITLE = "Developer";
  public static final String EMAIL = "john.smith@email.com";

  private UpcomingTenderConversionUtil() {
    throw new IllegalStateException("UpcomingTenderConversionUtil is a utility class and should not be instantiated");
  }

  public static UpcomingTenderConversionForm createUpcomingTenderConversionForm() {
    var form = new UpcomingTenderConversionForm();
    form.setDateAwarded(new ThreeFieldDateInput(DATE_AWARDED));
    form.setContractorName(CONTRACTOR_NAME);

    var contactDetailForm = new ContactDetailForm();
    contactDetailForm.setName(CONTACT_NAME);
    contactDetailForm.setPhoneNumber(PHONE_NUMBER);
    contactDetailForm.setJobTitle(JOB_TITLE);
    contactDetailForm.setEmailAddress(EMAIL);
    form.setContactDetail(contactDetailForm);

    return form;
  }
}
