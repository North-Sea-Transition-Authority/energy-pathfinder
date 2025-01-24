package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView;

public class ForwardWorkPlanCollaborationOpportunityTestUtil {

  public static final Function FUNCTION = CollaborationOpportunityTestUtilCommon.FUNCTION;
  public static final String MANUAL_FUNCTION = CollaborationOpportunityTestUtilCommon.MANUAL_FUNCTION;

  public static ForwardWorkPlanCollaborationOpportunityForm getCompleteForm() {
    return (ForwardWorkPlanCollaborationOpportunityForm) CollaborationOpportunityTestUtilCommon.populateCompleteForm(
        new ForwardWorkPlanCollaborationOpportunityForm()
    );
  }

  public static ForwardWorkPlanCollaborationOpportunityForm getCompletedForm_manualEntry() {
    return (ForwardWorkPlanCollaborationOpportunityForm) CollaborationOpportunityTestUtilCommon.populateCompletedForm_manualEntry(
        new ForwardWorkPlanCollaborationOpportunityForm()
    );
  }

  public static ForwardWorkPlanCollaborationOpportunity getCollaborationOpportunity(ProjectDetail detail) {
    return getCollaborationOpportunity(null, detail);
  }

  public static ForwardWorkPlanCollaborationOpportunity getCollaborationOpportunity(Integer id, ProjectDetail detail) {
    return (ForwardWorkPlanCollaborationOpportunity) CollaborationOpportunityTestUtilCommon.populateCollaborationOpportunity(
        new ForwardWorkPlanCollaborationOpportunity(id, detail)
    );
  }

  public static ForwardWorkPlanCollaborationOpportunity getCollaborationOpportunity_manualEntry(ProjectDetail detail) {
    return (ForwardWorkPlanCollaborationOpportunity) CollaborationOpportunityTestUtilCommon.getCollaborationOpportunity_manualEntry(
        new ForwardWorkPlanCollaborationOpportunity(detail)
    );
  }

  public static ForwardWorkPlanCollaborationOpportunityFileLink createCollaborationOpportunityFileLink(
      ForwardWorkPlanCollaborationOpportunity collaborationOpportunity,
      ProjectDetailFile projectDetailFile
  ) {

    var collaborationFileLink = new ForwardWorkPlanCollaborationOpportunityFileLink();
    collaborationFileLink.setCollaborationOpportunity(collaborationOpportunity);
    collaborationFileLink.setProjectDetailFile(projectDetailFile);
    return collaborationFileLink;
  }

  public static ForwardWorkPlanCollaborationOpportunityFileLink createCollaborationOpportunityFileLink() {
    return createCollaborationOpportunityFileLink(
        getCollaborationOpportunity(ProjectUtil.getProjectDetails()),
        new ProjectDetailFile()
    );
  }

  public static ForwardWorkPlanCollaborationOpportunityView getView(Integer displayOrder, Boolean isValid) {
    return (ForwardWorkPlanCollaborationOpportunityView) CollaborationOpportunityTestUtilCommon.populateView(
        new ForwardWorkPlanCollaborationOpportunityView(),
        displayOrder,
        isValid
    );
  }
}
