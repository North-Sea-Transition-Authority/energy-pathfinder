package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.EnergyTransitionCategory;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;

public class ProjectInformationUtil {

  public static final String PROJECT_TITLE = "PROJECT TITLE";
  public static final String PROJECT_SUMMARY = "SUMMARY";
  public static final FieldStage FIELD_STAGE = FieldStage.DECOMMISSIONING;
  public static final EnergyTransitionCategory ENERGY_TRANSITION_CATEGORY = EnergyTransitionCategory.HYDROGEN;
  public static final String CONTACT_NAME = ContactDetailsTestUtil.CONTACT_NAME;
  public static final String PHONE_NUMBER = ContactDetailsTestUtil.PHONE_NUMBER;
  public static final String JOB_TITLE = ContactDetailsTestUtil.JOB_TITLE;
  public static final String EMAIL = ContactDetailsTestUtil.EMAIL;

  public static ProjectInformation getProjectInformation_withCompleteDetails(ProjectDetail details) {
    var projectInformation = new ProjectInformation();
    projectInformation.setProjectDetail(details);
    projectInformation.setFieldStage(FIELD_STAGE);
    projectInformation.setProjectTitle(PROJECT_TITLE);
    projectInformation.setProjectSummary(PROJECT_SUMMARY);
    projectInformation.setContactName(CONTACT_NAME);
    projectInformation.setPhoneNumber(PHONE_NUMBER);
    projectInformation.setJobTitle(JOB_TITLE);
    projectInformation.setEmailAddress(EMAIL);

    return projectInformation;
  }

  public static ProjectInformationForm getCompleteForm() {
    var form = new ProjectInformationForm();
    form.setProjectSummary(PROJECT_SUMMARY);
    form.setProjectTitle(PROJECT_TITLE);
    form.setFieldStage(FIELD_STAGE);

    var contactDetailForm = ContactDetailsTestUtil.createContactDetailForm(
        CONTACT_NAME,
        PHONE_NUMBER,
        JOB_TITLE,
        EMAIL
    );
    form.setContactDetail(contactDetailForm);
    form.setDevelopmentFirstProductionDate(new QuarterYearInput(null, null));
    form.setDiscoveryFirstProductionDate(new QuarterYearInput(null, null));
    form.setEnergyTransitionCategory(ENERGY_TRANSITION_CATEGORY);

    return form;
  }

}
