package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

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
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoViewUtil;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlatformsFpsosSectionSummaryServiceTest {

  @Mock
  private PlatformsFpsosService platformsFpsosService;

  private PlatformsFpsosSectionSummaryService platformsFpsosSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    platformsFpsosSectionSummaryService = new PlatformsFpsosSectionSummaryService(platformsFpsosService);
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(platformsFpsosService.canShowInTaskList(detail)).thenReturn(true);

    assertThat(platformsFpsosSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(platformsFpsosService.canShowInTaskList(detail)).thenReturn(false);

    assertThat(platformsFpsosSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @Test
  public void getSummary() {
    var platformFpso1 = PlatformFpsoTestUtil.getPlatformFpso_withSubstructuresRemoved(detail);
    var platformFpso2 = PlatformFpsoTestUtil.getPlatformFpso_withSubstructuresRemoved(detail);
    when(platformsFpsosService.getPlatformsFpsosForDetail(detail)).thenReturn(List.of(
        platformFpso1,
        platformFpso2
    ));
    var sectionSummary = platformsFpsosSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(PlatformsFpsosSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(PlatformsFpsosSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(PlatformsFpsosSectionSummaryService.TEMPLATE_PATH);

    var platformFpsoView1 = PlatformFpsoViewUtil.createView(platformFpso1, 1, detail.getProject().getId());
    var platformFpsoView2 = PlatformFpsoViewUtil.createView(platformFpso2, 2, detail.getProject().getId());

    assertThat(model).containsOnly(
        entry("sectionTitle", PlatformsFpsosSectionSummaryService.PAGE_NAME),
        entry("sectionId", PlatformsFpsosSectionSummaryService.SECTION_ID),
        entry("platformFpsoViews", List.of(platformFpsoView1, platformFpsoView2))
    );
  }

  @Test
  public void getSummary_noPlatformsFpsos() {
    when(platformsFpsosService.getPlatformsFpsosForDetail(detail)).thenReturn(Collections.emptyList());
    var sectionSummary = platformsFpsosSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(PlatformsFpsosSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(PlatformsFpsosSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(PlatformsFpsosSectionSummaryService.TEMPLATE_PATH);

    assertThat(model).containsOnly(
        entry("sectionTitle", PlatformsFpsosSectionSummaryService.PAGE_NAME),
        entry("sectionId", PlatformsFpsosSectionSummaryService.SECTION_ID),
        entry("platformFpsoViews", Collections.emptyList())
    );
  }
}
