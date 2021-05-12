package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.CollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunitiesSectionSummaryServiceTest {

  @Mock
  private CollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private CollaborationOpportunitiesSectionSummaryService collaborationOpportunitiesSectionSummaryService;

  @Before
  public void setup() {
    collaborationOpportunitiesSectionSummaryService = new CollaborationOpportunitiesSectionSummaryService(
        collaborationOpportunitiesSummaryService,
        differenceService,
        projectSectionSummaryCommonModelService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(collaborationOpportunitiesSummaryService.canShowInTaskList(projectDetail)).thenReturn(true);

    assertThat(collaborationOpportunitiesSectionSummaryService.canShowSection(projectDetail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(collaborationOpportunitiesSummaryService.canShowInTaskList(projectDetail)).thenReturn(false);

    assertThat(collaborationOpportunitiesSectionSummaryService.canShowSection(projectDetail)).isFalse();
  }

  @Test
  public void getSummary_whenOpportunities() {

    final var collaborationOpportunityView1 = CollaborationOpportunityTestUtil.getView(1, true);
    final var collaborationOpportunityView2 = CollaborationOpportunityTestUtil.getView(2, true);
    final var previousCollaborationOpportunityViews = List.of(collaborationOpportunityView1, collaborationOpportunityView2);

    final var collaborationOpportunityView3 = CollaborationOpportunityTestUtil.getView(3, true);
    final var currentCollaborationOpportunityViews = List.of(collaborationOpportunityView3);

    when(collaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(currentCollaborationOpportunityViews);
    when(collaborationOpportunitiesSummaryService.getSummaryViews(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(previousCollaborationOpportunityViews);

    final var sectionSummary = collaborationOpportunitiesSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary, projectDetail);
    assertInteractions(currentCollaborationOpportunityViews, previousCollaborationOpportunityViews);
  }

  @Test
  public void getSummary_whenNoOpportunities_thenEmptyViewList() {

    when(collaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(Collections.emptyList());
    when(collaborationOpportunitiesSummaryService.getSummaryViews(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(Collections.emptyList());

    final var sectionSummary = collaborationOpportunitiesSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(sectionSummary, projectDetail);
    assertInteractions(Collections.emptyList(), Collections.emptyList());
  }

 private void assertModelProperties(ProjectSectionSummary sectionSummary, ProjectDetail projectDetail) {
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(CollaborationOpportunitiesSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(CollaborationOpportunitiesSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(CollaborationOpportunitiesSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        CollaborationOpportunitiesSectionSummaryService.PAGE_NAME,
        CollaborationOpportunitiesSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys("collaborationOpportunityDiffModel");
  }

  private void assertInteractions(List<CollaborationOpportunityView> currentCollaborationOpportunityViews,
                                  List<CollaborationOpportunityView> previousCollaborationOpportunityViews) {

    currentCollaborationOpportunityViews.forEach(collaborationOpportunityView -> {

      var previousCollaborationOpportunityView = previousCollaborationOpportunityViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(collaborationOpportunityView.getDisplayOrder()))
          .findFirst()
          .orElse(new CollaborationOpportunityView());

      verify(differenceService, times(1)).differentiate(
          collaborationOpportunityView,
          previousCollaborationOpportunityView,
          Set.of("summaryLinks", "uploadedFileViews")
      );

      verify(differenceService, times(1)).differentiateComplexLists(
          eq(collaborationOpportunityView.getUploadedFileViews()),
          eq(previousCollaborationOpportunityView.getUploadedFileViews()),
          eq(Set.of("fileUploadedTime")),
          any(),
          any()
      );

    });
  }

}