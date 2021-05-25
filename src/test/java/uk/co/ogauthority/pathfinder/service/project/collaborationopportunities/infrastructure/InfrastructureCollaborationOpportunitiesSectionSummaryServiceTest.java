package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure;

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
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure.InfrastructureCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureCollaborationOpportunitiesSectionSummaryServiceTest {

  @Mock
  private InfrastructureCollaborationOpportunitiesSummaryService infrastructureCollaborationOpportunitiesSummaryService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private InfrastructureCollaborationOpportunitiesSectionSummaryService infrastructureCollaborationOpportunitiesSectionSummaryService;

  @Before
  public void setup() {
    infrastructureCollaborationOpportunitiesSectionSummaryService = new InfrastructureCollaborationOpportunitiesSectionSummaryService(
        infrastructureCollaborationOpportunitiesSummaryService,
        differenceService,
        projectSectionSummaryCommonModelService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(infrastructureCollaborationOpportunitiesSummaryService.canShowInTaskList(projectDetail)).thenReturn(true);

    assertThat(infrastructureCollaborationOpportunitiesSectionSummaryService.canShowSection(projectDetail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(infrastructureCollaborationOpportunitiesSummaryService.canShowInTaskList(projectDetail)).thenReturn(false);

    assertThat(infrastructureCollaborationOpportunitiesSectionSummaryService.canShowSection(projectDetail)).isFalse();
  }

  @Test
  public void getSummary_whenOpportunities() {

    final var collaborationOpportunityView1 = InfrastructureCollaborationOpportunityTestUtil.getView(1, true);
    final var collaborationOpportunityView2 = InfrastructureCollaborationOpportunityTestUtil.getView(2, true);
    final var previousCollaborationOpportunityViews = List.of(collaborationOpportunityView1, collaborationOpportunityView2);

    final var collaborationOpportunityView3 = InfrastructureCollaborationOpportunityTestUtil.getView(3, true);
    final var currentCollaborationOpportunityViews = List.of(collaborationOpportunityView3);

    when(infrastructureCollaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(currentCollaborationOpportunityViews);
    when(infrastructureCollaborationOpportunitiesSummaryService.getSummaryViews(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(previousCollaborationOpportunityViews);

    final var sectionSummary = infrastructureCollaborationOpportunitiesSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary, projectDetail);
    assertInteractions(currentCollaborationOpportunityViews, previousCollaborationOpportunityViews);
  }

  @Test
  public void getSummary_whenNoOpportunities_thenEmptyViewList() {

    when(infrastructureCollaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(Collections.emptyList());
    when(infrastructureCollaborationOpportunitiesSummaryService.getSummaryViews(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(Collections.emptyList());

    final var sectionSummary = infrastructureCollaborationOpportunitiesSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(sectionSummary, projectDetail);
    assertInteractions(Collections.emptyList(), Collections.emptyList());
  }

 private void assertModelProperties(ProjectSectionSummary sectionSummary, ProjectDetail projectDetail) {
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(
        InfrastructureCollaborationOpportunitiesSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(
        InfrastructureCollaborationOpportunitiesSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(
        InfrastructureCollaborationOpportunitiesSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        InfrastructureCollaborationOpportunitiesSectionSummaryService.PAGE_NAME,
        InfrastructureCollaborationOpportunitiesSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys("collaborationOpportunityDiffModel");
  }

  private void assertInteractions(List<InfrastructureCollaborationOpportunityView> currentInfrastructureCollaborationOpportunityViews,
                                  List<InfrastructureCollaborationOpportunityView> previousInfrastructureCollaborationOpportunityViews) {

    currentInfrastructureCollaborationOpportunityViews.forEach(collaborationOpportunityView -> {

      var previousCollaborationOpportunityView = previousInfrastructureCollaborationOpportunityViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(collaborationOpportunityView.getDisplayOrder()))
          .findFirst()
          .orElse(new InfrastructureCollaborationOpportunityView());

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