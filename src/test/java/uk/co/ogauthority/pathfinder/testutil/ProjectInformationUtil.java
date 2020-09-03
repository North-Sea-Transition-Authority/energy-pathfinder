package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;

public class ProjectInformationUtil {

  public static final String PROJECT_TITLE = "PROJECT TITLE";
  public static final String PROJECT_SUMMARY = "SUMMARY";
  public static final FieldStage FIELD_STAGE = FieldStage.DECOMMISSIONING;
  public static final String CONTACT_NAME = ContactDetailsUtil.CONTACT_NAME;
  public static final String PHONE_NUMBER = ContactDetailsUtil.PHONE_NUMBER;
  public static final String JOB_TITLE = ContactDetailsUtil.JOB_TITLE;
  public static final String EMAIL = ContactDetailsUtil.EMAIL;

  public static ProjectInformation getProjectInformation_withCompleteDetails(ProjectDetail details) {
    return new ProjectInformation(details,
        FIELD_STAGE,
        PROJECT_TITLE,
        PROJECT_SUMMARY,
        CONTACT_NAME,
        PHONE_NUMBER,
        JOB_TITLE,
        EMAIL
        );
  }

  public static ProjectInformationForm getCompleteForm() {
    var form = new ProjectInformationForm();
    form.setProjectSummary(PROJECT_SUMMARY);
    form.setProjectTitle(PROJECT_TITLE);
    form.setFieldStage(FIELD_STAGE);
    form.setName(CONTACT_NAME);
    form.setPhoneNumber(PHONE_NUMBER);
    form.setJobTitle(JOB_TITLE);
    form.setEmailAddress(EMAIL);

    return form;
  }

}
