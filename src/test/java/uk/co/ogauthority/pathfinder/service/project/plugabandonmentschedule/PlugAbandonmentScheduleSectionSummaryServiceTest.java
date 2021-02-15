package uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule;

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
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.testutil.PlugAbandonmentScheduleTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlugAbandonmentScheduleSectionSummaryServiceTest {

  @Mock
  private PlugAbandonmentScheduleSummaryService plugAbandonmentScheduleSummaryService;

  @Mock
  private DifferenceService differenceService;

  private PlugAbandonmentScheduleSectionSummaryService plugAbandonmentScheduleSectionSummaryService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    plugAbandonmentScheduleSectionSummaryService = new PlugAbandonmentScheduleSectionSummaryService(
        plugAbandonmentScheduleSummaryService,
        differenceService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(plugAbandonmentScheduleSummaryService.canShowInTaskList(projectDetail)).thenReturn(true);

    assertThat(plugAbandonmentScheduleSectionSummaryService.canShowSection(projectDetail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(plugAbandonmentScheduleSummaryService.canShowInTaskList(projectDetail)).thenReturn(false);

    assertThat(plugAbandonmentScheduleSectionSummaryService.canShowSection(projectDetail)).isFalse();
  }

  @Test
  public void getSummary_whenPlugAbandonmentSchedules_thenViewsDiffed() {
    var plugAbandonmentScheduleViews = List.of(
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(1, true),
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(2, true)
    );
    var previousPlugAbandonmentScheduleViews = List.of(
        PlugAbandonmentScheduleTestUtil.createPlugAbandonmentScheduleView(1, true)
    );

    when(plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryViews(projectDetail)).thenReturn(plugAbandonmentScheduleViews);
    when(plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryViewsByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(previousPlugAbandonmentScheduleViews);

    final var sectionSummary = plugAbandonmentScheduleSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary);

    verify(differenceService, times(1)).differentiateComplexLists(
        eq(plugAbandonmentScheduleViews),
        eq(previousPlugAbandonmentScheduleViews),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }

  @Test
  public void getSummary_whenNoPlugAbandonmentSchedules_thenEmptyListsDiffed() {
    when(plugAbandonmentScheduleSummaryService.getPlugAbandonmentScheduleSummaryViews(projectDetail)).thenReturn(
        Collections.emptyList()
    );
    final var sectionSummary = plugAbandonmentScheduleSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary);

    verify(differenceService, times(1)).differentiateComplexLists(
        eq(Collections.emptyList()),
        eq(Collections.emptyList()),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }


  private void assertModelProperties(ProjectSectionSummary sectionSummary) {

    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(PlugAbandonmentScheduleSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(PlugAbandonmentScheduleSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(PlugAbandonmentScheduleSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    assertThat(model).containsOnlyKeys(
        "sectionTitle",
        "sectionId",
        "plugAbandonmentScheduleDiffModel"
    );

    assertThat(model).containsEntry("sectionTitle", PlugAbandonmentScheduleSectionSummaryService.PAGE_NAME);
    assertThat(model).containsEntry("sectionId", PlugAbandonmentScheduleSectionSummaryService.SECTION_ID);
  }
}
