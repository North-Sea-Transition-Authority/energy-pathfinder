package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetails;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;

public class ProjectInformationUtil {

  public static final String PROJECT_TITLE = "PROJECT TITLE";
  public static final String PROJECT_SUMMARY = "SUMMARY";
  public static final FieldStage FIELD_STAGE = FieldStage.DECOMMISSIONING;

  public static ProjectInformation getProjectInformation_withCompleteDetails(ProjectDetails details) {
    return new ProjectInformation(details,
        FIELD_STAGE,
        PROJECT_TITLE,
        PROJECT_SUMMARY
        );
  }

  public static ProjectInformationForm getCompleteForm() {
    var form = new ProjectInformationForm();
    form.setProjectSummary(PROJECT_SUMMARY);
    form.setProjectTitle(PROJECT_TITLE);
    form.setFieldStage(FIELD_STAGE);

    return form;
  }

}
