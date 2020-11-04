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
  public void getSummary() {
    when(decommissionedPipelineService.getDecommissionedPipelines(detail)).thenReturn(List.of(
        DecommissionedPipelineTestUtil.createDecommissionedPipeline(),
        DecommissionedPipelineTestUtil.createDecommissionedPipeline()
    ));
    var sectionSummary = decommissionedPipelineSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(DecommissionedPipelineSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(DecommissionedPipelineSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(DecommissionedPipelineSectionSummaryService.TEMPLATE_PATH);

    var decommissionedPipelineViews = (List<DecommissionedPipelineView>) model.get("decommissionedPipelineViews");
    assertThat(decommissionedPipelineViews).isNotNull();
    assertThat(decommissionedPipelineViews.size()).isEqualTo(2);

    assertThat(model).containsOnly(
        entry("sectionTitle", DecommissionedPipelineSectionSummaryService.PAGE_NAME),
        entry("sectionId", DecommissionedPipelineSectionSummaryService.SECTION_ID),
        entry("decommissionedPipelineViews", decommissionedPipelineViews)
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

    var decommissionedPipelineViews = (List<DecommissionedPipelineView>) model.get("decommissionedPipelineViews");
    assertThat(decommissionedPipelineViews).isNotNull();
    assertThat(decommissionedPipelineViews).isEmpty();

    assertThat(model).containsOnly(
        entry("sectionTitle", DecommissionedPipelineSectionSummaryService.PAGE_NAME),
        entry("sectionId", DecommissionedPipelineSectionSummaryService.SECTION_ID),
        entry("decommissionedPipelineViews", decommissionedPipelineViews)
    );
  }
}
