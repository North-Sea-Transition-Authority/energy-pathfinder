package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.contactdetail.ContactDetailView;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class CollaborationOpportunityTestUtil {
  public static final Integer ID = 1;
  public static final Integer PROJECT_ID = 1;
  public static final Function FUNCTION = Function.DRILLING;
  public static final String MANUAL_FUNCTION = SearchSelectablePrefix.FREE_TEXT_PREFIX + "function";
  public static final String DESCRIPTION_OF_WORK = "work description";
  public static final Boolean URGENT_RESPONSE_NEEDED = true;
  public static final LocalDate ESTIMATED_SERVICE_DATE = LocalDate.now().plusMonths(1L);
  public static final String CONTACT_NAME = ContactDetailsTestUtil.CONTACT_NAME;
  public static final String PHONE_NUMBER = ContactDetailsTestUtil.PHONE_NUMBER;
  public static final String JOB_TITLE = ContactDetailsTestUtil.JOB_TITLE;
  public static final String EMAIL = ContactDetailsTestUtil.EMAIL;



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
    form.setUrgentResponseNeeded(URGENT_RESPONSE_NEEDED);

    var contactDetailForm = ContactDetailsTestUtil.createContactDetailForm(
        CONTACT_NAME,
        PHONE_NUMBER,
        JOB_TITLE,
        EMAIL
    );
    form.setContactDetail(contactDetailForm);
  }

  public static CollaborationOpportunityView getView(Integer displayOrder, Boolean isValid) {
    var view = new CollaborationOpportunityView(
        displayOrder,
        ID,
        PROJECT_ID
    );
    view.setIsValid(isValid);
    view.setFunction(FUNCTION.getDisplayName());
    view.setUrgentResponseNeeded(StringDisplayUtil.yesNoFromBoolean(URGENT_RESPONSE_NEEDED));
    var contactDetailsView = new ContactDetailView();
    contactDetailsView.setName(CONTACT_NAME);
    contactDetailsView.setPhoneNumber(PHONE_NUMBER);
    contactDetailsView.setJobTitle(JOB_TITLE);
    contactDetailsView.setEmailAddress(EMAIL);
    view.setContactDetailView(contactDetailsView);
    return view;
  }

  public static CollaborationOpportunity getCollaborationOpportunity(ProjectDetail detail) {
    var opportunity = new CollaborationOpportunity(detail);
    opportunity.setFunction(FUNCTION);
    setCommonFields(opportunity);
    return opportunity;
  }

  public static CollaborationOpportunity getCollaborationOpportunity_manualEntry(ProjectDetail detail) {
    var opportunity = new CollaborationOpportunity(detail);
    opportunity.setManualFunction(SearchSelectorService.removePrefix(MANUAL_FUNCTION));
    setCommonFields(opportunity);
    return opportunity;
  }

  public static CollaborationOpportunityFileLink createCollaborationOpportunityFileLink(CollaborationOpportunity collaborationOpportunity,
                                                                                        ProjectDetailFile projectDetailFile) {
    var collaborationFileLink = new CollaborationOpportunityFileLink();
    collaborationFileLink.setCollaborationOpportunity(collaborationOpportunity);
    collaborationFileLink.setProjectDetailFile(projectDetailFile);
    return collaborationFileLink;
  }

  public static CollaborationOpportunityFileLink createCollaborationOpportunityFileLink() {
    return createCollaborationOpportunityFileLink(
        getCollaborationOpportunity(ProjectUtil.getProjectDetails()),
        new ProjectDetailFile()
    );
  }

  private static void setCommonFields(CollaborationOpportunity opportunity) {
    opportunity.setDescriptionOfWork(DESCRIPTION_OF_WORK);
    opportunity.setUrgentResponseNeeded(URGENT_RESPONSE_NEEDED);
    opportunity.setContactName(CONTACT_NAME);
    opportunity.setPhoneNumber(PHONE_NUMBER);
    opportunity.setJobTitle(JOB_TITLE);
    opportunity.setEmailAddress(EMAIL);
  }
}
