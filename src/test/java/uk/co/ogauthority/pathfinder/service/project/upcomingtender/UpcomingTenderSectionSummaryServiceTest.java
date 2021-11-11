package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

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
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingTenderSectionSummaryServiceTest {

  @Mock
  private UpcomingTenderSummaryService upcomingTenderSummaryService;

  @Mock
  private DifferenceService differenceService;

  private UpcomingTenderSectionSummaryService upcomingTenderSectionSummaryService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    upcomingTenderSectionSummaryService = new UpcomingTenderSectionSummaryService(
        upcomingTenderSummaryService,
        differenceService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(upcomingTenderSummaryService.canShowInTaskList(projectDetail)).thenReturn(true);

    assertThat(upcomingTenderSectionSummaryService.canShowSection(projectDetail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(upcomingTenderSummaryService.canShowInTaskList(projectDetail)).thenReturn(false);

    assertThat(upcomingTenderSectionSummaryService.canShowSection(projectDetail)).isFalse();
  }

  @Test
  public void getSummary_whenUpcomingTenders() {
    final var upcomingTenderView1 = UpcomingTenderUtil.getView(1, true);
    final var upcomingTenderView2 = UpcomingTenderUtil.getView(2, true);
    final var previousUpcomingTenderViews = List.of(upcomingTenderView1, upcomingTenderView2);

    final var upcomingTenderView3 = UpcomingTenderUtil.getView(3, true);
    final var currentUpcomingTenderViews = List.of(upcomingTenderView3);

    when(upcomingTenderSummaryService.getSummaryViews(projectDetail)).thenReturn(currentUpcomingTenderViews);
    when(upcomingTenderSummaryService.getSummaryViews(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(previousUpcomingTenderViews);

    final var sectionSummary = upcomingTenderSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary);
    assertInteractions(currentUpcomingTenderViews, previousUpcomingTenderViews);
  }

  @Test
  public void getSummary_whenNoUpcomingTenders() {

    when(upcomingTenderSummaryService.getSummaryViews(projectDetail)).thenReturn(Collections.emptyList());
    when(upcomingTenderSummaryService.getSummaryViews(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(Collections.emptyList());

    final var sectionSummary = upcomingTenderSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(sectionSummary);
    assertInteractions(Collections.emptyList(), Collections.emptyList());
  }

  private void assertModelProperties(ProjectSectionSummary sectionSummary) {
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(UpcomingTenderSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(UpcomingTenderSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(UpcomingTenderSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    assertThat(model).containsOnlyKeys(
        "sectionTitle",
        "sectionId",
        "upcomingTenderDiffModel"
    );

    assertThat(model).containsEntry("sectionTitle", UpcomingTenderSectionSummaryService.PAGE_NAME);
    assertThat(model).containsEntry("sectionId", UpcomingTenderSectionSummaryService.SECTION_ID);
  }

  private void assertInteractions(List<UpcomingTenderView> currentTenderViews,
                                  List<UpcomingTenderView> previousTenderViews) {

    currentTenderViews.forEach(upcomingTenderView -> {

      var previousUpcomingTenderView = previousTenderViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(upcomingTenderView.getDisplayOrder()))
          .findFirst()
          .orElse(new UpcomingTenderView());

      verify(differenceService, times(1)).differentiate(
          upcomingTenderView,
          previousUpcomingTenderView,
          Set.of("summaryLinks", "uploadedFileViews")
      );

      verify(differenceService, times(1)).differentiateComplexLists(
          eq(upcomingTenderView.getUploadedFileViews()),
          eq(previousUpcomingTenderView.getUploadedFileViews()),
          eq(Set.of("fileUploadedTime")),
          eq(Set.of("fileUrl")),
          any(),
          any()
      );

    });
  }
}
