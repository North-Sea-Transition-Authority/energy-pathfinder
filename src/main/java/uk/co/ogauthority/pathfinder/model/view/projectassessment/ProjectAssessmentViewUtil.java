package uk.co.ogauthority.pathfinder.model.view.projectassessment;

import org.apache.commons.lang3.BooleanUtils;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.projectassessment.ProjectAssessment;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class ProjectAssessmentViewUtil {

  private ProjectAssessmentViewUtil() {
    throw new IllegalStateException("ProjectAssessmentViewUtil is a utility class and should not be instantiated.");
  }

  public static ProjectAssessmentView from(ProjectAssessment projectAssessment,
                                           WebUserAccount assessor) {
    var projectAssessmentView = new ProjectAssessmentView();
    projectAssessmentView.setProjectQuality(
        projectAssessment.getProjectQuality() != null
            ? projectAssessment.getProjectQuality().getDisplayName()
            : null
    );
    projectAssessmentView.setReadyToBePublished(projectAssessment.getReadyToBePublished());
    projectAssessmentView.setUpdateRequired(
        BooleanUtils.isTrue(projectAssessment.getReadyToBePublished())
            ? projectAssessment.getUpdateRequired()
            : null);
    projectAssessmentView.setAssessmentDate(DateUtil.formatInstant(projectAssessment.getAssessedInstant()));
    projectAssessmentView.setAssessedByUser(assessor.getFullName());
    return projectAssessmentView;
  }
}
