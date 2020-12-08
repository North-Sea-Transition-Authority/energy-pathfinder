package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.testutil.CollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunitiesSectionSummaryServiceTest {

  @Mock
  private CollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private CollaborationOpportunitiesSectionSummaryService collaborationOpportunitiesSectionSummaryService;

  @Before
  public void setup() {
    collaborationOpportunitiesSectionSummaryService = new CollaborationOpportunitiesSectionSummaryService(
        collaborationOpportunitiesSummaryService
    );
  }

  @Test
  public void getSummary_whenOpportunities_thenViewsPopulated() {

    final var collaborationOpportunityView1 = CollaborationOpportunityTestUtil.getView(1, true);
    final var collaborationOpportunityView2 = CollaborationOpportunityTestUtil.getView(2, true);

    when(collaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(
        List.of(collaborationOpportunityView1, collaborationOpportunityView2)
    );

    final var sectionSummary = collaborationOpportunitiesSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(sectionSummary, List.of(collaborationOpportunityView1, collaborationOpportunityView2));
  }

  @Test
  public void getSummary_whenNoOpportunities_thenEmptyViewList() {

    when(collaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(Collections.emptyList());

    final var sectionSummary = collaborationOpportunitiesSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(sectionSummary, List.of());
  }

  private void assertModelProperties(ProjectSectionSummary sectionSummary,
                                     List<CollaborationOpportunityView> collaborationOpportunityViews) {
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(CollaborationOpportunitiesSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(CollaborationOpportunitiesSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(CollaborationOpportunitiesSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    assertThat(model).containsOnly(
        entry("sectionTitle", CollaborationOpportunitiesSectionSummaryService.PAGE_NAME),
        entry("sectionId", CollaborationOpportunitiesSectionSummaryService.SECTION_ID),
        entry("collaborationOpportunityViews", collaborationOpportunityViews)
    );
  }

}