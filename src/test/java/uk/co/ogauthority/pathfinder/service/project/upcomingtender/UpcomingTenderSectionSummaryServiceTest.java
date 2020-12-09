package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

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
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingTenderSectionSummaryServiceTest {

  @Mock
  private UpcomingTenderSummaryService upcomingTenderSummaryService;

  private UpcomingTenderSectionSummaryService upcomingTenderSectionSummaryService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    upcomingTenderSectionSummaryService = new UpcomingTenderSectionSummaryService(upcomingTenderSummaryService);
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
  public void getSummary_whenUpcomingTenders_thenViewsPopulated() {
    final var upcomingTenderView1 = UpcomingTenderUtil.getView(1, true);
    final var upcomingTenderView2 = UpcomingTenderUtil.getView(2, true);
    final var upcomingTenderViews = List.of(upcomingTenderView1, upcomingTenderView2);

    when(upcomingTenderSummaryService.getSummaryViews(projectDetail)).thenReturn(upcomingTenderViews);

    final var sectionSummary = upcomingTenderSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary, upcomingTenderViews);
  }

  @Test
  public void getSummary_whenNoUpcomingTenders_thenEmptyViewList() {
    when(upcomingTenderSummaryService.getSummaryViews(projectDetail)).thenReturn(Collections.emptyList());

    final var sectionSummary = upcomingTenderSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(sectionSummary, List.of());
  }

  private void assertModelProperties(ProjectSectionSummary sectionSummary,
                                     List<UpcomingTenderView> upcomingTenderViews) {
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(UpcomingTenderSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(UpcomingTenderSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(UpcomingTenderSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    assertThat(model).containsOnly(
        entry("sectionTitle", UpcomingTenderSectionSummaryService.PAGE_NAME),
        entry("sectionId", UpcomingTenderSectionSummaryService.SECTION_ID),
        entry("upcomingTenderViews", upcomingTenderViews)
    );
  }
}
