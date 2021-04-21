package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.WorkPlanUpcomingTenderContractBand;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;

public class WorkPlanUpcomingTenderUtil {
  public static final Function UPCOMING_TENDER_DEPARTMENT = Function.DRILLING;
  public static final String MANUAL_TENDER_DEPARTMENT = SearchSelectablePrefix.FREE_TEXT_PREFIX + "manual department type";
  public static final String DESCRIPTION_OF_WORK = "work description";
  public static final LocalDate ESTIMATED_TENDER_DATE = LocalDate.now().plusMonths(1L);
  public static final WorkPlanUpcomingTenderContractBand CONTRACT_BAND = WorkPlanUpcomingTenderContractBand.GREATER_THAN_OR_EQUAL_TO_5M;
  public static final String CONTACT_NAME = ContactDetailsTestUtil.CONTACT_NAME;
  public static final String PHONE_NUMBER = ContactDetailsTestUtil.PHONE_NUMBER;
  public static final String JOB_TITLE = ContactDetailsTestUtil.JOB_TITLE;
  public static final String EMAIL = ContactDetailsTestUtil.EMAIL;

  public static WorkPlanUpcomingTenderForm getCompleteForm() {
    var form = new WorkPlanUpcomingTenderForm();
    form.setDepartmentType(UPCOMING_TENDER_DEPARTMENT.name());
    setUpcomingTenderFields(form);
    return form;
  }

  public static WorkPlanUpcomingTenderForm getCompleteForm_manualEntry() {
    var form = new WorkPlanUpcomingTenderForm();
    form.setDepartmentType(MANUAL_TENDER_DEPARTMENT);
    setUpcomingTenderFields(form);
    return form;
  }

  public static WorkPlanUpcomingTender getUpcomingTender(ProjectDetail detail) {
    var tender = new WorkPlanUpcomingTender(detail);
    tender.setDepartmentType(UPCOMING_TENDER_DEPARTMENT);
    setUpcomingTenderFields(tender);
    return tender;
  }

  public static WorkPlanUpcomingTender getUpcomingTender_manualEntry(ProjectDetail detail) {
    var tender = new WorkPlanUpcomingTender(detail);
    tender.setManualDepartmentType(MANUAL_TENDER_DEPARTMENT);
    setUpcomingTenderFields(tender);
    return tender;
  }

  private static void setUpcomingTenderFields(WorkPlanUpcomingTender tender) {
    tender.setDescriptionOfWork(DESCRIPTION_OF_WORK);
    tender.setEstimatedTenderDate(ESTIMATED_TENDER_DATE);
    tender.setContractBand(CONTRACT_BAND);
    tender.setContactName(CONTACT_NAME);
    tender.setPhoneNumber(PHONE_NUMBER);
    tender.setJobTitle(JOB_TITLE);
    tender.setEmailAddress(EMAIL);
  }

  private static void setUpcomingTenderFields(WorkPlanUpcomingTenderForm form) {
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