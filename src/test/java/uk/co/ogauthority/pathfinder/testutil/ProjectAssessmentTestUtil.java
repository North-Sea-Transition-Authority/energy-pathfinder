package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.enums.project.projectassessment.ProjectQuality;
import uk.co.ogauthority.pathfinder.model.form.project.projectassessment.ProjectAssessmentForm;

public class ProjectAssessmentTestUtil {

  private static final ProjectQuality PROJECT_QUALITY = ProjectQuality.GOOD;
  private static final Boolean READY_TO_BE_PUBLISHED = true;
  private static final Boolean UPDATE_REQUIRED = false;

  private ProjectAssessmentTestUtil() {
    throw new IllegalStateException("ProjectAssessmentTestUtil is a utility class and should not be instantiated");
  }

  public static ProjectAssessmentForm createProjectAssessmentForm() {
    var form = new ProjectAssessmentForm();
    form.setProjectQuality(PROJECT_QUALITY);
    form.setReadyToBePublished(READY_TO_BE_PUBLISHED);
    form.setUpdateRequired(UPDATE_REQUIRED);
    return form;
  }
}
