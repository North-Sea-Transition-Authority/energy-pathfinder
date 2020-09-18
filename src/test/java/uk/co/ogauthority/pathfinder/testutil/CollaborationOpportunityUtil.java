package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;

public class CollaborationOpportunityUtil {
  public static final Function FUNCTION = Function.DRILLING;
  public static final String MANUAL_FUNCTION = SearchSelectablePrefix.FREE_TEXT_PREFIX + "function";
  public static final String DESCRIPTION_OF_WORK = "work description";
  public static final LocalDate ESTIMATED_TENDER_DATE = LocalDate.now().plusMonths(1L);
  public static final String CONTACT_NAME = ContactDetailsUtil.CONTACT_NAME;
  public static final String PHONE_NUMBER = ContactDetailsUtil.PHONE_NUMBER;
  public static final String JOB_TITLE = ContactDetailsUtil.JOB_TITLE;
  public static final String EMAIL = ContactDetailsUtil.EMAIL;



  public static CollaborationOpportunityForm getCompleteForm() {
    var form = new CollaborationOpportunityForm();
    form.setFunction(FUNCTION.name());
    setCommonFields(form);
    return form;
  }

  public static CollaborationOpportunityForm getCompletedForm_manualEntry() {
    var form = new CollaborationOpportunityForm();
    form.setFunction(MANUAL_FUNCTION);
    setCommonFields(form);
    return form;
  }

  private static void setCommonFields(CollaborationOpportunityForm form) {
    form.setDescriptionOfWork(DESCRIPTION_OF_WORK);
    form.setEstimatedServiceDate(new ThreeFieldDateInput(ESTIMATED_TENDER_DATE));

    var contactDetailForm = ContactDetailsUtil.createContactDetailForm(
        CONTACT_NAME,
        PHONE_NUMBER,
        JOB_TITLE,
        EMAIL
    );
    form.setContactDetail(contactDetailForm);
  }
}
