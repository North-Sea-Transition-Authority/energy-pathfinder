package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure.InfrastructureCollaborationOpportunityView;

public class InfrastructureCollaborationOpportunityTestUtil {

  public static final Function FUNCTION = CollaborationOpportunityTestUtilCommon.FUNCTION;
  public static final String MANUAL_FUNCTION = CollaborationOpportunityTestUtilCommon.MANUAL_FUNCTION;

  public static InfrastructureCollaborationOpportunityForm getCompleteForm() {
    return (InfrastructureCollaborationOpportunityForm) CollaborationOpportunityTestUtilCommon.populateCompleteForm(
        new InfrastructureCollaborationOpportunityForm()
    );
  }

  public static InfrastructureCollaborationOpportunityForm getCompletedForm_manualEntry() {
    return (InfrastructureCollaborationOpportunityForm) CollaborationOpportunityTestUtilCommon.populateCompletedForm_manualEntry(
        new InfrastructureCollaborationOpportunityForm()
    );
  }

  public static InfrastructureCollaborationOpportunityView getView(Integer displayOrder, Boolean isValid) {
    return (InfrastructureCollaborationOpportunityView) CollaborationOpportunityTestUtilCommon.populateView(
        new InfrastructureCollaborationOpportunityView(),
        displayOrder,
        isValid
    );
  }

  public static InfrastructureCollaborationOpportunity getCollaborationOpportunity(ProjectDetail detail) {
    return getCollaborationOpportunity(null, detail);
  }

  public static InfrastructureCollaborationOpportunity getCollaborationOpportunity(Integer id, ProjectDetail detail) {
    return (InfrastructureCollaborationOpportunity) CollaborationOpportunityTestUtilCommon.populateCollaborationOpportunity(
        new InfrastructureCollaborationOpportunity(id, detail)
    );
  }

  public static InfrastructureCollaborationOpportunity getCollaborationOpportunity_manualEntry(ProjectDetail detail) {
    return (InfrastructureCollaborationOpportunity) CollaborationOpportunityTestUtilCommon.getCollaborationOpportunity_manualEntry(
        new InfrastructureCollaborationOpportunity(detail)
    );
  }

  public static InfrastructureCollaborationOpportunityFileLink createCollaborationOpportunityFileLink(
      InfrastructureCollaborationOpportunity collaborationOpportunity,
      ProjectDetailFile projectDetailFile
  ) {

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
}
