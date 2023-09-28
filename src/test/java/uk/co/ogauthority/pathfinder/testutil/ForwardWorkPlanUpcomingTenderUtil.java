package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.ForwardWorkPlanUpcomingTenderView;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class ForwardWorkPlanUpcomingTenderUtil {
  public static final Function UPCOMING_TENDER_DEPARTMENT = Function.DRILLING;
  public static final String MANUAL_TENDER_DEPARTMENT = SearchSelectablePrefix.FREE_TEXT_PREFIX + "manual department type";
  public static final String DESCRIPTION_OF_WORK = "work description";
  public static final Quarter ESTIMATED_TENDER_QUARTER = Quarter.Q1;
  public static final Integer ESTIMATED_TENDER_YEAR = 2025;
  public static final ContractBand CONTRACT_BAND = ContractBand.GREATER_THAN_OR_EQUAL_TO_5M;
  public static final String CONTACT_NAME = ContactDetailsTestUtil.CONTACT_NAME;
  public static final String PHONE_NUMBER = ContactDetailsTestUtil.PHONE_NUMBER;
  public static final String JOB_TITLE = ContactDetailsTestUtil.JOB_TITLE;
  public static final String EMAIL = ContactDetailsTestUtil.EMAIL;
  public static final DurationPeriod CONTRACT_TERM_DURATION_PERIOD = DurationPeriod.YEARS;
  public static final Integer CONTRACT_TERM_DURATION = 1;
  public static final Integer ADDED_BY_ORGANISATION_GROUP = 1;

  public static final Integer ID = 1;
  public static final Integer PROJECT_ID = 1;

  public static ForwardWorkPlanUpcomingTenderForm getCompleteForm() {
    var form = new ForwardWorkPlanUpcomingTenderForm();
    form.setDepartmentType(UPCOMING_TENDER_DEPARTMENT.name());
    setUpcomingTenderFields(form);
    return form;
  }

  public static ForwardWorkPlanUpcomingTenderForm getCompleteForm_manualEntry() {
    var form = new ForwardWorkPlanUpcomingTenderForm();
    form.setDepartmentType(MANUAL_TENDER_DEPARTMENT);
    setUpcomingTenderFields(form);
    return form;
  }

  public static ForwardWorkPlanUpcomingTenderForm getEmptyForm() {
    var form = new ForwardWorkPlanUpcomingTenderForm();
    form.setEstimatedTenderStartDate(new QuarterYearInput(null, null));
    form.setContactDetail(new ContactDetailForm());
    return form;
  }

  public static ForwardWorkPlanUpcomingTender getUpcomingTender(ProjectDetail detail) {
    var tender = new ForwardWorkPlanUpcomingTender(detail);
    tender.setDepartmentType(UPCOMING_TENDER_DEPARTMENT);
    setUpcomingTenderFields(tender);
    return tender;
  }

  public static ForwardWorkPlanUpcomingTender getUpcomingTender_manualEntry(ProjectDetail detail) {
    var tender = new ForwardWorkPlanUpcomingTender(detail);
    tender.setManualDepartmentType(MANUAL_TENDER_DEPARTMENT);
    setUpcomingTenderFields(tender);
    return tender;
  }

  private static void setUpcomingTenderFields(ForwardWorkPlanUpcomingTender tender) {
    tender.setDescriptionOfWork(DESCRIPTION_OF_WORK);
    tender.setEstimatedTenderDateQuarter(ESTIMATED_TENDER_QUARTER);
    tender.setEstimatedTenderDateYear(ESTIMATED_TENDER_YEAR);
    tender.setContractBand(CONTRACT_BAND);
    tender.setContractTermDuration(CONTRACT_TERM_DURATION);
    tender.setContractTermDurationPeriod(CONTRACT_TERM_DURATION_PERIOD);
    tender.setContactName(CONTACT_NAME);
    tender.setPhoneNumber(PHONE_NUMBER);
    tender.setJobTitle(JOB_TITLE);
    tender.setEmailAddress(EMAIL);
    tender.setAddedByOrganisationGroup(ADDED_BY_ORGANISATION_GROUP);
  }

  private static void setUpcomingTenderFields(ForwardWorkPlanUpcomingTenderForm form) {
    form.setDescriptionOfWork(DESCRIPTION_OF_WORK);
    form.setEstimatedTenderStartDate(new QuarterYearInput(ESTIMATED_TENDER_QUARTER, String.valueOf(ESTIMATED_TENDER_YEAR)));
    form.setContractBand(CONTRACT_BAND);
    form.setContractTermYearDuration(CONTRACT_TERM_DURATION);
    form.setContractTermDurationPeriod(CONTRACT_TERM_DURATION_PERIOD);

    var contactDetailForm = ContactDetailsTestUtil.createContactDetailForm(
        CONTACT_NAME,
        PHONE_NUMBER,
        JOB_TITLE,
        EMAIL
    );
    form.setContactDetail(contactDetailForm);
  }

  public static ForwardWorkPlanUpcomingTenderView getView(Integer displayOrder, boolean isValid) {
    var view = new ForwardWorkPlanUpcomingTenderView(
        displayOrder,
        ID,
        PROJECT_ID
    );
    view.setIsValid(isValid);
    view.setTenderDepartment(new StringWithTag(UPCOMING_TENDER_DEPARTMENT.getDisplayName(), Tag.NONE));
    view.setEstimatedTenderStartDate(DateUtil.getDateFromQuarterYear(ESTIMATED_TENDER_QUARTER, ESTIMATED_TENDER_YEAR));
    view.setContractBand(CONTRACT_BAND.getDisplayName());

    view.setContactName(CONTACT_NAME);
    view.setContactPhoneNumber(PHONE_NUMBER);
    view.setContactJobTitle(JOB_TITLE);
    view.setContactEmailAddress(EMAIL);

    return view;
  }
}
