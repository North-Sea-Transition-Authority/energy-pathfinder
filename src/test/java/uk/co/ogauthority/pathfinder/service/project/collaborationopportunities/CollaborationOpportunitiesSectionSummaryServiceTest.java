package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.TestCollaborationOpportunityViewCommon;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunitiesSectionSummaryServiceTest {

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Mock
  private DifferenceService differenceService;

  private TestCollaborationOpportunitySectionSummaryService testCollaborationOpportunitySectionSummaryService;

  @Before
  public void setup() {
    testCollaborationOpportunitySectionSummaryService = new TestCollaborationOpportunitySectionSummaryService(
        projectSectionSummaryCommonModelService,
        differenceService
    );
  }

  @Test
  public void getSummaryModel_assertModelProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    final var summaryModel = testCollaborationOpportunitySectionSummaryService.getSummaryModel(
        projectDetail,
        TestCollaborationOpportunityViewCommon.class
    );

    final var collaborationOpportunityDiffModelKey = "collaborationOpportunityDiffModel";
    assertThat(summaryModel).containsOnlyKeys(collaborationOpportunityDiffModelKey);

    final var diffModel = (List<Map<String, Object>>) summaryModel.get(collaborationOpportunityDiffModelKey);

    assertThat(diffModel).hasSize(1);

    assertThat(diffModel.get(0)).containsOnlyKeys(
        "collaborationOpportunityDiff",
        "collaborationOpportunityFiles"
    );
  }

  @Test
  public void getProjectSectionSummary() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    final var summaryModel = testCollaborationOpportunitySectionSummaryService.getSummaryModel(
        projectDetail,
        TestCollaborationOpportunityViewCommon.class
    );

    final var projectSectionSummary = testCollaborationOpportunitySectionSummaryService.getProjectSectionSummary(summaryModel);

    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(
        List.of(testCollaborationOpportunitySectionSummaryService.getSectionLink())
    );
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(
        testCollaborationOpportunitySectionSummaryService.getTemplatePath()
    );
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(
        testCollaborationOpportunitySectionSummaryService.getDisplayOrder()
    );
  }

}