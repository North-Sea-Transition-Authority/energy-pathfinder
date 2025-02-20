package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class CollaborationOpportunityJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var infrastructureCollaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);
    var infrastructureCollaborationOpportunityFileLink =
        InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();

    var infrastructureProjectCollaborationOpportunityJson =
        CollaborationOpportunityJson.from(infrastructureCollaborationOpportunity, infrastructureCollaborationOpportunityFileLink);

    var expectedInfrastructureProjectCollaborationOpportunityJson = new CollaborationOpportunityJson(
        infrastructureCollaborationOpportunity.getId(),
        infrastructureCollaborationOpportunity.getFunction().name(),
        infrastructureCollaborationOpportunity.getManualFunction(),
        infrastructureCollaborationOpportunity.getDescriptionOfWork(),
        infrastructureCollaborationOpportunity.getUrgentResponseNeeded(),
        ContactJson.from(infrastructureCollaborationOpportunity),
        UploadedFileJson.from(infrastructureCollaborationOpportunityFileLink.getProjectDetailFile().getUploadedFile())
    );

    assertThat(infrastructureProjectCollaborationOpportunityJson)
        .isEqualTo(expectedInfrastructureProjectCollaborationOpportunityJson);
  }

  @Test
  void from_functionIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var infrastructureCollaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    infrastructureCollaborationOpportunity.setFunction(Function.LOGISTICS);

    var infrastructureProjectCollaborationOpportunityJson =
        CollaborationOpportunityJson.from(infrastructureCollaborationOpportunity, null);

    assertThat(infrastructureProjectCollaborationOpportunityJson.function()).isEqualTo(Function.LOGISTICS.name());
  }

  @Test
  void from_functionIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var infrastructureCollaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    infrastructureCollaborationOpportunity.setFunction(null);

    var infrastructureProjectCollaborationOpportunityJson =
        CollaborationOpportunityJson.from(infrastructureCollaborationOpportunity, null);

    assertThat(infrastructureProjectCollaborationOpportunityJson.function()).isNull();
  }

  @Test
  void from_collaborationOpportunityFileLinkIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var infrastructureCollaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    var infrastructureProjectCollaborationOpportunityJson =
        CollaborationOpportunityJson.from(infrastructureCollaborationOpportunity, null);

    assertThat(infrastructureProjectCollaborationOpportunityJson.supportingDocumentUploadedFile()).isNull();
  }

  @Test
  void from_collaborationOpportunityFileLinkIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var infrastructureCollaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);
    var infrastructureCollaborationOpportunityFileLink =
        InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();

    var infrastructureProjectCollaborationOpportunityJson =
        CollaborationOpportunityJson.from(infrastructureCollaborationOpportunity, infrastructureCollaborationOpportunityFileLink);

    assertThat(infrastructureProjectCollaborationOpportunityJson.supportingDocumentUploadedFile())
        .isEqualTo(UploadedFileJson.from(infrastructureCollaborationOpportunityFileLink.getProjectDetailFile().getUploadedFile()));
  }
}
