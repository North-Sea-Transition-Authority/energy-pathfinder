package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.TenderFunction;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

public class UpcomingTenderUtil {
  public static final TenderFunction TENDER_FUNCTION = TenderFunction.DRILLING;
  public static final String MANUAL_TENDER_FUNCTION = SearchSelectable.FREE_TEXT_PREFIX + "function";
  public static final String DESCRIPTION_OF_WORK = "work description";
  public static final LocalDate ESTIMATED_TENDER_DATE = LocalDate.now().plusMonths(1L);
  public static final ContractBand CONTRACT_BAND = ContractBand.GREATER_THAN_OR_EQUAL_TO_25M;
  public static final String CONTACT_NAME = ContactDetailsUtil.CONTACT_NAME;
  public static final String PHONE_NUMBER = ContactDetailsUtil.PHONE_NUMBER;
  public static final String JOB_TITLE = ContactDetailsUtil.JOB_TITLE;
  public static final String EMAIL = ContactDetailsUtil.EMAIL;


  public static UpcomingTenderForm getCompleteForm() {
    var form = new UpcomingTenderForm();
    form.setTenderFunction(TENDER_FUNCTION.name());
    setUpcomingTenderFields(form);
    return form;
  }

  public static UpcomingTenderForm getCompletedForm_manualEntry() {
    var form = new UpcomingTenderForm();
    form.setTenderFunction(MANUAL_TENDER_FUNCTION);
    setUpcomingTenderFields(form);
    return form;
  }

  private static void setUpcomingTenderFields(UpcomingTenderForm form) {
    form.setDescriptionOfWork(DESCRIPTION_OF_WORK);
    form.setEstimatedTenderDate(new ThreeFieldDateInput(ESTIMATED_TENDER_DATE));
    form.setContractBand(CONTRACT_BAND);
    form.setName(CONTACT_NAME);
    form.setPhoneNumber(PHONE_NUMBER);
    form.setJobTitle(JOB_TITLE);
    form.setEmailAddress(EMAIL);
  }


}
