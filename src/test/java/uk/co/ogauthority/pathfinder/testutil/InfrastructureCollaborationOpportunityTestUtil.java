package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure.InfrastructureCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class InfrastructureCollaborationOpportunityTestUtil {
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



  public static InfrastructureCollaborationOpportunityForm getCompleteForm() {
    var form = new InfrastructureCollaborationOpportunityForm();
    form.setFunction(FUNCTION.name());
    setCommonFields(form);
    return form;
  }

  public static InfrastructureCollaborationOpportunityForm getCompletedForm_manualEntry() {
    var form = new InfrastructureCollaborationOpportunityForm();
    form.setFunction(MANUAL_FUNCTION);
    setCommonFields(form);
    return form;
  }

  private static void setCommonFields(InfrastructureCollaborationOpportunityForm form) {
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

  public static InfrastructureCollaborationOpportunityView getView(Integer displayOrder, Boolean isValid) {
    var view = new InfrastructureCollaborationOpportunityView(
        displayOrder,
        ID,
        PROJECT_ID
    );
    view.setIsValid(isValid);
    view.setFunction(new StringWithTag(FUNCTION.getDisplayName(), Tag.NONE));
    view.setUrgentResponseNeeded(StringDisplayUtil.yesNoFromBoolean(URGENT_RESPONSE_NEEDED));
    view.setContactName(CONTACT_NAME);
    view.setContactPhoneNumber(PHONE_NUMBER);
    view.setContactJobTitle(JOB_TITLE);
    view.setContactEmailAddress(EMAIL);
    return view;
  }

  public static InfrastructureCollaborationOpportunity getCollaborationOpportunity(ProjectDetail detail) {
    var opportunity = new InfrastructureCollaborationOpportunity(detail);
    opportunity.setFunction(FUNCTION);
    setCommonFields(opportunity);
    return opportunity;
  }

  public static InfrastructureCollaborationOpportunity getCollaborationOpportunity_manualEntry(ProjectDetail detail) {
    var opportunity = new InfrastructureCollaborationOpportunity(detail);
    opportunity.setManualFunction(SearchSelectorService.removePrefix(MANUAL_FUNCTION));
    setCommonFields(opportunity);
    return opportunity;
  }

  public static InfrastructureCollaborationOpportunityFileLink createCollaborationOpportunityFileLink(
      InfrastructureCollaborationOpportunity collaborationOpportunity,
      ProjectDetailFile projectDetailFile) {
    var collaborationFileLink = new InfrastructureCollaborationOpportunityFileLink();
    collaborationFileLink.setCollaborationOpportunity(collaborationOpportunity);
    collaborationFileLink.setProjectDetailFile(projectDetailFile);
    return collaborationFileLink;
  }

  public static InfrastructureCollaborationOpportunityFileLink createCollaborationOpportunityFileLink() {
    return createCollaborationOpportunityFileLink(
        getCollaborationOpportunity(ProjectUtil.getProjectDetails()),
        new ProjectDetailFile()
    );
  }

  private static void setCommonFields(InfrastructureCollaborationOpportunity opportunity) {
    opportunity.setDescriptionOfWork(DESCRIPTION_OF_WORK);
    opportunity.setUrgentResponseNeeded(URGENT_RESPONSE_NEEDED);
    opportunity.setContactName(CONTACT_NAME);
    opportunity.setPhoneNumber(PHONE_NUMBER);
    opportunity.setJobTitle(JOB_TITLE);
    opportunity.setEmailAddress(EMAIL);
  }
}
