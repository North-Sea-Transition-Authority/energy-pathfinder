package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
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
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationSetupView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunitiesSectionSummaryServiceTest {

  @Mock
  private ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Mock
  private ForwardWorkPlanCollaborationSetupService forwardWorkPlanCollaborationSetupService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private ForwardWorkPlanCollaborationOpportunitiesSectionSummaryService forwardWorkPlanCollaborationOpportunitiesSectionSummaryService;

  @Before
  public void setup() {
    when(differenceService.getDiffableList(any(), any()))
        .thenCallRealMethod();

    forwardWorkPlanCollaborationOpportunitiesSectionSummaryService = new ForwardWorkPlanCollaborationOpportunitiesSectionSummaryService(
        forwardWorkPlanCollaborationOpportunitiesSummaryService,
        forwardWorkPlanCollaborationSetupService,
        differenceService,
        projectSectionSummaryCommonModelService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.canShowInTaskList(projectDetail)).thenReturn(true);

    assertThat(forwardWorkPlanCollaborationOpportunitiesSectionSummaryService.canShowSection(projectDetail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.canShowInTaskList(projectDetail)).thenReturn(false);

    assertThat(forwardWorkPlanCollaborationOpportunitiesSectionSummaryService.canShowSection(projectDetail)).isFalse();
  }

  @Test
  public void getSummary_whenOpportunities() {

    final var collaborationOpportunityView1 = ForwardWorkPlanCollaborationOpportunityTestUtil.getView(1, true);
    final var collaborationOpportunityView2 = ForwardWorkPlanCollaborationOpportunityTestUtil.getView(2, true);
    final var previousCollaborationOpportunityViews = List.of(collaborationOpportunityView1, collaborationOpportunityView2);

    final var collaborationOpportunityView3 = ForwardWorkPlanCollaborationOpportunityTestUtil.getView(3, true);
    final var currentCollaborationOpportunityViews = List.of(collaborationOpportunityView3);

    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(currentCollaborationOpportunityViews);
    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(previousCollaborationOpportunityViews);

    final var sectionSummary = forwardWorkPlanCollaborationOpportunitiesSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary, projectDetail);
    assertInteractions(currentCollaborationOpportunityViews, previousCollaborationOpportunityViews);
  }

  @Test
  public void getSummary_whenNoOpportunities_thenEmptyViewList() {

    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(Collections.emptyList());
    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(Collections.emptyList());

    final var sectionSummary = forwardWorkPlanCollaborationOpportunitiesSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(sectionSummary, projectDetail);
    assertInteractions(Collections.emptyList(), Collections.emptyList());
  }

  @Test
  public void getSummary_assertSetupDifferenceModel() {

    final var currentSetupView = new ForwardWorkPlanCollaborationSetupView();
    currentSetupView.setHasCollaborationsToAdd("Yes");

    when(forwardWorkPlanCollaborationSetupService.getCollaborationSetupView(projectDetail)).thenReturn(currentSetupView);

    final var previousSetupView = new ForwardWorkPlanCollaborationSetupView();
    previousSetupView.setHasCollaborationsToAdd("No");

    when(forwardWorkPlanCollaborationSetupService.getCollaborationSetupView(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(previousSetupView);

    forwardWorkPlanCollaborationOpportunitiesSectionSummaryService.getSummary(projectDetail);

    verify(differenceService, times(1)).differentiate(
        currentSetupView,
        previousSetupView
    );
  }

  private void assertModelProperties(ProjectSectionSummary sectionSummary, ProjectDetail projectDetail) {
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(
        ForwardWorkPlanCollaborationOpportunitiesSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(
        ForwardWorkPlanCollaborationOpportunitiesSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(
        ForwardWorkPlanCollaborationOpportunitiesSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        ForwardWorkPlanCollaborationOpportunitiesSectionSummaryService.PAGE_NAME,
        ForwardWorkPlanCollaborationOpportunitiesSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys(
        "collaborationOpportunityDiffModel",
        "workPlanCollaborationSetupDiffModel"
    );
  }

  private void assertInteractions(List<ForwardWorkPlanCollaborationOpportunityView> currentCollaborationOpportunityViews,
                                  List<ForwardWorkPlanCollaborationOpportunityView> previousCollaborationOpportunityViews) {

    var diffList = differenceService.getDiffableList(currentCollaborationOpportunityViews,
        previousCollaborationOpportunityViews);

    diffList.forEach(collaborationOpportunityView -> {

      var currentCollaborationOpportunityView = currentCollaborationOpportunityViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(collaborationOpportunityView.getDisplayOrder()))
          .findFirst()
          .orElse(new ForwardWorkPlanCollaborationOpportunityView());

      var previousCollaborationOpportunityView = previousCollaborationOpportunityViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(collaborationOpportunityView.getDisplayOrder()))
          .findFirst()
          .orElse(new ForwardWorkPlanCollaborationOpportunityView());

      verify(differenceService, atLeastOnce()) // One extra for the call at the top of the test
          .getDiffableList(currentCollaborationOpportunityViews, previousCollaborationOpportunityViews);

      verify(differenceService, times(1)).differentiate(
          currentCollaborationOpportunityView,
          previousCollaborationOpportunityView,
          Set.of("summaryLinks", "uploadedFileViews")
      );

      verify(differenceService, atLeastOnce()).differentiateComplexLists(
          eq(collaborationOpportunityView.getUploadedFileViews()),
          eq(previousCollaborationOpportunityView.getUploadedFileViews()),
          eq(Set.of("fileUploadedTime")),
          eq(Set.of("fileUrl")),
          any(),
          any()
      );

    });
  }

}