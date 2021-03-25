package uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline;

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
import uk.co.ogauthority.pathfinder.testutil.DecommissionedPipelineTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissionedPipelineSectionSummaryServiceTest {

  @Mock
  private DecommissionedPipelineService decommissionedPipelineService;

  @Mock
  private DifferenceService differenceService;

  private DecommissionedPipelineSectionSummaryService decommissionedPipelineSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    decommissionedPipelineSectionSummaryService = new DecommissionedPipelineSectionSummaryService(
        decommissionedPipelineService,
        differenceService
    );
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
    when(decommissionedPipelineService.getDecommissionedPipelines(detail)).thenReturn(List.of(
        DecommissionedPipelineTestUtil.createDecommissionedPipeline(),
        DecommissionedPipelineTestUtil.createDecommissionedPipeline()
    ));

    when(decommissionedPipelineService.getDecommissionedPipelinesByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(List.of(
        DecommissionedPipelineTestUtil.createDecommissionedPipeline(),
        DecommissionedPipelineTestUtil.createDecommissionedPipeline()
    ));

    var sectionSummary = decommissionedPipelineSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary);

    verify(differenceService, times(1)).differentiateComplexLists(
        any(),
        any(),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }

  @Test
  public void getSummary_noDecommissionedPipelines() {
    when(decommissionedPipelineService.getDecommissionedPipelines(detail)).thenReturn(Collections.emptyList());

    var sectionSummary = decommissionedPipelineSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary);
  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary) {
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(DecommissionedPipelineSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(DecommissionedPipelineSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(DecommissionedPipelineSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    assertThat(model).containsOnlyKeys(
        "sectionTitle",
        "sectionId",
        "decommissionedPipelineDiffModel"
    );

    assertThat(model).containsEntry("sectionTitle", DecommissionedPipelineSectionSummaryService.PAGE_NAME);
    assertThat(model).containsEntry("sectionId", DecommissionedPipelineSectionSummaryService.SECTION_ID);
  }
}
