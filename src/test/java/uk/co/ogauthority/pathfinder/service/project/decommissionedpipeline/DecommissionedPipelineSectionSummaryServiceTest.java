package uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline;

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
import uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline.DecommissionedPipelineView;
import uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline.DecommissionedPipelineViewUtil;
import uk.co.ogauthority.pathfinder.testutil.DecommissionedPipelineTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissionedPipelineSectionSummaryServiceTest {

  @Mock
  private DecommissionedPipelineService decommissionedPipelineService;

  private DecommissionedPipelineSectionSummaryService decommissionedPipelineSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    decommissionedPipelineSectionSummaryService = new DecommissionedPipelineSectionSummaryService(decommissionedPipelineService);
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(decommissionedPipelineService.canShowInTaskList(detail)).thenReturn(true);

    assertThat(decommissionedPipelineSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(decommissionedPipelineService.canShowInTaskList(detail)).thenReturn(false);

    assertThat(decommissionedPipelineSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @Test
  public void getSummary() {
    var decommissionedPipeline1 = DecommissionedPipelineTestUtil.createDecommissionedPipeline();
    var decommissionedPipeline2 = DecommissionedPipelineTestUtil.createDecommissionedPipeline();
    when(decommissionedPipelineService.getDecommissionedPipelines(detail)).thenReturn(List.of(
        decommissionedPipeline1,
        decommissionedPipeline2
    ));
    var sectionSummary = decommissionedPipelineSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(DecommissionedPipelineSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(DecommissionedPipelineSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(DecommissionedPipelineSectionSummaryService.TEMPLATE_PATH);

    var decommissionedPipelineView1 = DecommissionedPipelineViewUtil.from(decommissionedPipeline1, 1);
    var decommissionedPipelineView2 = DecommissionedPipelineViewUtil.from(decommissionedPipeline2, 2);

    assertThat(model).containsOnly(
        entry("sectionTitle", DecommissionedPipelineSectionSummaryService.PAGE_NAME),
        entry("sectionId", DecommissionedPipelineSectionSummaryService.SECTION_ID),
        entry("decommissionedPipelineViews", List.of(decommissionedPipelineView1, decommissionedPipelineView2))
    );
  }

  @Test
  public void getSummary_noDecommissionedPipelines() {
    when(decommissionedPipelineService.getDecommissionedPipelines(detail)).thenReturn(Collections.emptyList());
    var sectionSummary = decommissionedPipelineSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(DecommissionedPipelineSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(DecommissionedPipelineSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(DecommissionedPipelineSectionSummaryService.TEMPLATE_PATH);

    assertThat(model).containsOnly(
        entry("sectionTitle", DecommissionedPipelineSectionSummaryService.PAGE_NAME),
        entry("sectionId", DecommissionedPipelineSectionSummaryService.SECTION_ID),
        entry("decommissionedPipelineViews", Collections.emptyList())
    );
  }
}
