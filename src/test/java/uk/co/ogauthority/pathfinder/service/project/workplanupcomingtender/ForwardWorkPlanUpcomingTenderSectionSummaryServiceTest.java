package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

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
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.ForwardWorkPlanTenderSetupView;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.ForwardWorkPlanUpcomingTenderView;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanUpcomingTenderSectionSummaryServiceTest {

  @Mock
  private ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ForwardWorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Mock
  private ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;

  private ForwardWorkPlanUpcomingTenderSectionSummaryService workPlanUpcomingTenderSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    workPlanUpcomingTenderSectionSummaryService = new ForwardWorkPlanUpcomingTenderSectionSummaryService(
        workPlanUpcomingTenderService,
        differenceService,
        workPlanUpcomingTenderSummaryService,
        projectSectionSummaryCommonModelService,
        forwardWorkPlanTenderSetupService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(workPlanUpcomingTenderService.canShowInTaskList(detail)).thenReturn(true);

    assertThat(workPlanUpcomingTenderSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(workPlanUpcomingTenderService.canShowInTaskList(detail)).thenReturn(false);

    assertThat(workPlanUpcomingTenderSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @Test
  public void getSummary() {
    final var currentTenderViewList = List.of(
      ForwardWorkPlanUpcomingTenderUtil.getView(1, true),
      ForwardWorkPlanUpcomingTenderUtil.getView(2, true)
    );

    when(workPlanUpcomingTenderSummaryService.getSummaryViews(detail)).thenReturn(currentTenderViewList);

    final var previousTenderViewList = List.of(ForwardWorkPlanUpcomingTenderUtil.getView(1, true));

    when(workPlanUpcomingTenderSummaryService.getSummaryViews(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(previousTenderViewList);

    var sectionSummary = workPlanUpcomingTenderSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary, detail);

    verify(differenceService, times(1)).differentiateComplexLists(
        eq(currentTenderViewList),
        eq(previousTenderViewList),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }

  @Test
  public void getSummary_noUpcomingTenders() {
    List<ForwardWorkPlanUpcomingTenderView> currentTenderViewList = Collections.emptyList();
    List<ForwardWorkPlanUpcomingTenderView> previousTenderViewList = Collections.emptyList();

    when(workPlanUpcomingTenderSummaryService.getSummaryViews(detail)).thenReturn(currentTenderViewList);
    when(workPlanUpcomingTenderSummaryService.getSummaryViews(detail.getProject(), detail.getVersion() - 1))
        .thenReturn(previousTenderViewList);

    var sectionSummary = workPlanUpcomingTenderSectionSummaryService.getSummary(detail);
    assertModelProperties(sectionSummary, detail);

    verify(differenceService, times(1)).differentiateComplexLists(
        eq(currentTenderViewList),
        eq(previousTenderViewList),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }

  @Test
  public void getSummary_assertSetupDifferenceModel() {

    final var currentSetupView = new ForwardWorkPlanTenderSetupView();
    currentSetupView.setHasTendersToAdd("Yes");

    when(forwardWorkPlanTenderSetupService.getTenderSetupView(detail)).thenReturn(currentSetupView);

    final var previousSetupView = new ForwardWorkPlanTenderSetupView();
    previousSetupView.setHasTendersToAdd("No");

    when(forwardWorkPlanTenderSetupService.getTenderSetupView(
        detail.getProject(),
        detail.getVersion())
    ).thenReturn(previousSetupView);

    workPlanUpcomingTenderSectionSummaryService.getSummary(detail);

    verify(differenceService, times(1)).differentiate(
        currentSetupView,
        previousSetupView
    );
  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary, ProjectDetail projectDetail) {
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(ForwardWorkPlanUpcomingTenderSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(
        ForwardWorkPlanUpcomingTenderSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(ForwardWorkPlanUpcomingTenderSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    assertThat(model).containsOnlyKeys("upcomingTendersDiffModel", "workPlanTenderSetupDiffModel");

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        ForwardWorkPlanUpcomingTenderSectionSummaryService.PAGE_NAME,
        ForwardWorkPlanUpcomingTenderSectionSummaryService.SECTION_ID
    );
  }
}