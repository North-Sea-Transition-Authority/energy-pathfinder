package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.model.view.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.model.view.contactdetail.ContactDetailView;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class UpcomingTenderUtil {
  public static final Function TENDER_FUNCTION = Function.DRILLING;
  public static final String MANUAL_TENDER_FUNCTION = SearchSelectablePrefix.FREE_TEXT_PREFIX + "function";
  public static final String DESCRIPTION_OF_WORK = "work description";
  public static final LocalDate ESTIMATED_TENDER_DATE = LocalDate.now().plusMonths(1L);
  public static final ContractBand CONTRACT_BAND = ContractBand.GREATER_THAN_OR_EQUAL_TO_25M;
  public static final String CONTACT_NAME = ContactDetailsTestUtil.CONTACT_NAME;
  public static final String PHONE_NUMBER = ContactDetailsTestUtil.PHONE_NUMBER;
  public static final String JOB_TITLE = ContactDetailsTestUtil.JOB_TITLE;
  public static final String EMAIL = ContactDetailsTestUtil.EMAIL;

  public static final Integer DISPLAY_ORDER = 1;
  public static final Integer ID = 1;
  public static final Integer PROJECT_ID = 1;


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


  public static UpcomingTender getUpcomingTender(ProjectDetail detail) {
    var tender = new UpcomingTender(detail);
    tender.setTenderFunction(TENDER_FUNCTION);
    setUpcomingTenderFields(tender);
    return tender;
  }

  public static UpcomingTender getUpcomingTender_manualEntry(ProjectDetail detail) {
    var tender = new UpcomingTender(detail);
    tender.setManualTenderFunction(MANUAL_TENDER_FUNCTION);
    setUpcomingTenderFields(tender);
    return tender;
  }

  public static UpcomingTenderView getView(Integer displayOrder, boolean isValid) {
    var view = new UpcomingTenderView(
        displayOrder,
        ID,
        PROJECT_ID
    );
    view.setIsValid(isValid);
    view.setTenderFunction(TENDER_FUNCTION.getDisplayName());
    view.setEstimatedTenderDate(DateUtil.formatDate(ESTIMATED_TENDER_DATE));
    view.setContractBand(CONTRACT_BAND.getDisplayName());

    ContactDetailView contactDetailView = new ContactDetailView();
    contactDetailView.setName(CONTACT_NAME);
    contactDetailView.setPhoneNumber(PHONE_NUMBER);
    contactDetailView.setJobTitle(JOB_TITLE);
    contactDetailView.setEmailAddress(EMAIL);
    view.setContactDetailView(contactDetailView);

    return view;
  }

  private static void setUpcomingTenderFields(UpcomingTender tender) {
    tender.setDescriptionOfWork(DESCRIPTION_OF_WORK);
    tender.setEstimatedTenderDate(ESTIMATED_TENDER_DATE);
    tender.setContractBand(CONTRACT_BAND);
    tender.setContactName(CONTACT_NAME);
    tender.setPhoneNumber(PHONE_NUMBER);
    tender.setJobTitle(JOB_TITLE);
    tender.setEmailAddress(EMAIL);
  }

  private static void setUpcomingTenderFields(UpcomingTenderForm form) {
    form.setDescriptionOfWork(DESCRIPTION_OF_WORK);
    form.setEstimatedTenderDate(new ThreeFieldDateInput(ESTIMATED_TENDER_DATE));
    form.setContractBand(CONTRACT_BAND);

    var contactDetailForm = ContactDetailsTestUtil.createContactDetailForm(
        CONTACT_NAME,
        PHONE_NUMBER,
        JOB_TITLE,
        EMAIL
    );
    form.setContactDetail(contactDetailForm);
  }


}
