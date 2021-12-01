package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityCommon;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityFileLinkCommon;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityFormCommon;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityViewCommon;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class CollaborationOpportunityTestUtilCommon {
  public static final Integer ID = 1;
  public static final Integer PROJECT_ID = 1;
  public static final Function FUNCTION = Function.DRILLING;
  public static final String MANUAL_FUNCTION = SearchSelectablePrefix.FREE_TEXT_PREFIX + "function";
  public static final String DESCRIPTION_OF_WORK = "work description";
  public static final Boolean URGENT_RESPONSE_NEEDED = true;
  public static final String CONTACT_NAME = ContactDetailsTestUtil.CONTACT_NAME;
  public static final String PHONE_NUMBER = ContactDetailsTestUtil.PHONE_NUMBER;
  public static final String JOB_TITLE = ContactDetailsTestUtil.JOB_TITLE;
  public static final String EMAIL = ContactDetailsTestUtil.EMAIL;

  public static CollaborationOpportunityFormCommon populateCompleteForm(CollaborationOpportunityFormCommon form) {
    form.setFunction(FUNCTION.name());
    setCommonFields(form);
    return form;
  }

  protected static CollaborationOpportunityFormCommon populateCompletedForm_manualEntry(
      CollaborationOpportunityFormCommon form
  ) {
    form.setFunction(MANUAL_FUNCTION);
    setCommonFields(form);
    return form;
  }

  private static void setCommonFields(CollaborationOpportunityFormCommon form) {
    form.setDescriptionOfWork(DESCRIPTION_OF_WORK);
    form.setUrgentResponseNeeded(URGENT_RESPONSE_NEEDED);

    var contactDetailForm = ContactDetailsTestUtil.createContactDetailForm(
        CONTACT_NAME,
        PHONE_NUMBER,
        JOB_TITLE,
        EMAIL
    );
    form.setContactDetail(contactDetailForm);
  }

  protected static CollaborationOpportunityViewCommon populateView(CollaborationOpportunityViewCommon view,
                                                                   Integer displayOrder,
                                                                   Boolean isValid) {
    view.setId(ID);
    view.setDisplayOrder(displayOrder);
    view.setProjectId(PROJECT_ID);
    view.setIsValid(isValid);
    view.setFunction(new StringWithTag(FUNCTION.getDisplayName(), Tag.NONE));
    view.setUrgentResponseNeeded(StringDisplayUtil.yesNoFromBoolean(URGENT_RESPONSE_NEEDED));
    view.setContactName(CONTACT_NAME);
    view.setContactPhoneNumber(PHONE_NUMBER);
    view.setContactJobTitle(JOB_TITLE);
    view.setContactEmailAddress(EMAIL);
    return view;
  }

  public static CollaborationOpportunityCommon populateCollaborationOpportunity(
      CollaborationOpportunityCommon opportunity
  ) {
    opportunity.setFunction(FUNCTION);
    setCommonFields(opportunity);
    return opportunity;
  }

  protected static CollaborationOpportunityCommon getCollaborationOpportunity_manualEntry(
      CollaborationOpportunityCommon opportunity
  ) {
    opportunity.setManualFunction(SearchSelectorService.removePrefix(MANUAL_FUNCTION));
    setCommonFields(opportunity);
    return opportunity;
  }

  public static CollaborationOpportunityFileLinkCommon populateCollaborationOpportunityFileLink(
      CollaborationOpportunityFileLinkCommon collaborationOpportunityFileLinkCommon
  ) {
    collaborationOpportunityFileLinkCommon.setId(1);
    collaborationOpportunityFileLinkCommon.setProjectDetailFile(
        ProjectFileTestUtil.getProjectDetailFile(ProjectUtil.getProjectDetails())
    );
    return collaborationOpportunityFileLinkCommon;
  }

  private static void setCommonFields(CollaborationOpportunityCommon opportunity) {
    opportunity.setDescriptionOfWork(DESCRIPTION_OF_WORK);
    opportunity.setUrgentResponseNeeded(URGENT_RESPONSE_NEEDED);
    opportunity.setContactName(CONTACT_NAME);
    opportunity.setPhoneNumber(PHONE_NUMBER);
    opportunity.setJobTitle(JOB_TITLE);
    opportunity.setEmailAddress(EMAIL);
  }
}
