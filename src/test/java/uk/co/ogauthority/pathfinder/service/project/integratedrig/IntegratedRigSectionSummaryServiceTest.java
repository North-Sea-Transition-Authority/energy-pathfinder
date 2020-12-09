package uk.co.ogauthority.pathfinder.service.project.integratedrig;

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
import uk.co.ogauthority.pathfinder.model.view.integratedrig.IntegratedRigView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.testutil.IntegratedRigTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class IntegratedRigSectionSummaryServiceTest {

  @Mock
  private IntegratedRigSummaryService integratedRigSummaryService;

  private IntegratedRigSectionSummaryService integratedRigSectionSummaryService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    integratedRigSectionSummaryService = new IntegratedRigSectionSummaryService(integratedRigSummaryService);
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(integratedRigSummaryService.canShowInTaskList(projectDetail)).thenReturn(true);

    assertThat(integratedRigSectionSummaryService.canShowSection(projectDetail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(integratedRigSummaryService.canShowInTaskList(projectDetail)).thenReturn(false);

    assertThat(integratedRigSectionSummaryService.canShowSection(projectDetail)).isFalse();
  }

  @Test
  public void getSummary_whenIntegratedRigs_thenViewsPopulated() {

    final var integratedRigView1 = IntegratedRigTestUtil.createIntegratedRigView(1, true);
    final var integratedRigView2 = IntegratedRigTestUtil.createIntegratedRigView(2, true);
    final var integratedListViews = List.of(integratedRigView1, integratedRigView2);

    when(integratedRigSummaryService.getIntegratedRigSummaryViews(projectDetail)).thenReturn(integratedListViews);

    final var sectionSummary = integratedRigSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary, integratedListViews);

  }


  @Test

  public void getSummary_whenNoIntegratedRigs_thenEmptyViewList() {
    when(integratedRigSummaryService.getIntegratedRigSummaryViews(projectDetail)).thenReturn(Collections.emptyList());
    final var sectionSummary = integratedRigSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary, List.of());

  }


  private void assertModelProperties(ProjectSectionSummary sectionSummary,
                                     List<IntegratedRigView> integratedRigViews) {

    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(IntegratedRigSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(IntegratedRigSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(IntegratedRigSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    assertThat(model).containsOnly(
        entry("sectionTitle", IntegratedRigSectionSummaryService.PAGE_NAME),
        entry("sectionId", IntegratedRigSectionSummaryService.SECTION_ID),
        entry("integratedRigViews", integratedRigViews)
    );

  }

}