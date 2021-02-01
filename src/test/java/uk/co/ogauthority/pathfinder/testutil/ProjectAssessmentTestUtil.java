package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.projectassessment.ProjectAssessment;
import uk.co.ogauthority.pathfinder.model.form.projectassessment.ProjectAssessmentForm;

public class ProjectAssessmentTestUtil {

  private static final Boolean READY_TO_BE_PUBLISHED = true;
  private static final Boolean UPDATE_REQUIRED = false;

  private ProjectAssessmentTestUtil() {
    throw new IllegalStateException("ProjectAssessmentTestUtil is a utility class and should not be instantiated");
  }

  public static ProjectAssessment createProjectAssessment() {
    var projectAssessment = new ProjectAssessment();
    projectAssessment.setProjectDetail(ProjectUtil.getProjectDetails());
    projectAssessment.setReadyToBePublished(READY_TO_BE_PUBLISHED);
    projectAssessment.setUpdateRequired(UPDATE_REQUIRED);
    return projectAssessment;
  }

  public static ProjectAssessmentForm createProjectAssessmentForm() {
    var form = new ProjectAssessmentForm();
    form.setReadyToBePublished(READY_TO_BE_PUBLISHED);
    form.setUpdateRequired(UPDATE_REQUIRED);
    return form;
  }
}
